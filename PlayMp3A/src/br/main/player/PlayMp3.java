package br.main.player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import br.main.player.interfaces.IHandlerMusicPosition;
import br.main.player.interfaces.IHandlerMusicStopped;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class PlayMp3 {

	private String filename;
	private AdvancedPlayer player = null;
	
	private int totalFrames = 0;
	private Posicao pos;
	private PlaybackListener listener;
	private boolean isPlaying;
	
	private AudioDevice audio;
	//Decoder decoder;
	
	private IHandlerMusicStopped iHandlerMusicStopped;
	private IHandlerMusicPosition iHandlerMusicPosition;

	public int getPausedOnFrame() {
		return pos == null ? -1 : pos.getPausedOnFrame();
	}
	
	public PlayMp3(String filename) {
		this(filename, null, null);
	}
	public PlayMp3(String filename, IHandlerMusicStopped iHandlerMusicStopped) {
		this(filename, iHandlerMusicStopped, null);
	}
	public PlayMp3(String filename, IHandlerMusicPosition iHandlerMusicPosition) {
		this(filename, null, iHandlerMusicPosition);
	}
	public PlayMp3(String filename, IHandlerMusicStopped iHandlerMusicStopped, IHandlerMusicPosition iHandlerMusicPosition) {
		this.filename = filename;
		this.iHandlerMusicStopped = iHandlerMusicStopped;
		this.iHandlerMusicPosition = iHandlerMusicPosition;
		pos = new Posicao();
		isPlaying = false;
		this.listener = getPlaybackListener();
	}

	public void stop() {
		isPlaying = false;
		if (player == null) return;
		try { player.stop(); } catch (Exception e) { }
	}
	
	public void close() {
		isPlaying = false;
		stop();
		if (player == null) return;
		try { player.close(); } catch (Exception e) { }
		if (pos!=null) {pos.setPausedOnFrame(0);} //depois do close não terá despause()
	}
	
	public void playPause() {
		playPause(false);
	}
	
	public void playPause(boolean reiniciar) {
		//System.out.println("pos: " + pos.toString());
		if (reiniciar) {
			//pos = new Posicao();
			close();
			if (pos!=null) {pos.setPausedOnFrame(0);}
			//this.listener = getPlaybackListener();
			//System.out.println("reiniciar: " + reiniciar + ", pos: " + pos.toString());
		}
		if (isPlaying) {
			stop();
		} else {
			play();
		}
	}
	
	public void avancar(int frames) {
		if (frames<0||frames>=totalFrames) {
			System.err.println("frames < 0 ou frames >= " + totalFrames);
			return;
		}
		close();
		if (pos!=null) {pos.setPausedOnFrame(frames);}
		play();
	}
	

	
	private void play() {

		player = null;
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
			totalFrames = this.totalFrames();
			
			audio = FactoryRegistry.systemRegistry().createAudioDevice();
			//decoder = new Decoder();
			//audio.open(decoder);

			player = new AdvancedPlayer(bis, audio);
			if (listener != null) player.setPlayBackListener(listener);
			
		} catch (Exception e) {
			//System.out.println("play(): Problem playing file " + filename + ", e: " + e);
			return;
		}


		new Thread() {
			public void run() {
				try {
					isPlaying = true;
					if (pos.getPausedOnFrame() <= 0) {
						player.play();
					} else {
						// possível erro - totalFrames
						player.play(pos.getPausedOnFrame(), totalFrames <= 0 ? Integer.MAX_VALUE : totalFrames);
					}
				} catch (Exception e) {
					//System.out.println("play(): " + e);
					isPlaying = false;
				}
			}
		}.start();

		if (iHandlerMusicPosition!=null) {
			new Thread() {
				public void run() {
					while(isPlaying) {
						iHandlerMusicPosition.setPosition(audio.getPosition());
						//System.out.println("audio.getPosition(): " + audio.getPosition());
						p(1000); //1s
					}
				}
			}.start();	
		}
		
	}

	private int totalFrames() {
		int count = 0;
		try {
			Bitstream b = new Bitstream(new BufferedInputStream(new FileInputStream(filename)));
			while (b.readFrame() != null) {
				count++;
				b.closeFrame();
			}
			b.close();
		} catch (FileNotFoundException | BitstreamException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	private PlaybackListener getPlaybackListener() {
		if (listener!=null) {return listener;}
		listener = new PlaybackListener() {
			@Override
			public void playbackStarted(PlaybackEvent event) {
				super.playbackStarted(event);
				//System.out.println("playbackStarted - event.getFrame(): " + event.getFrame());
			}
			
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				//System.out.println("finalize() called");
			}
			
			@Override
		    public void playbackFinished(PlaybackEvent event) {
				isPlaying = false;
				//System.out.println("-> playbackFinished() - " + event.getFrame() + " / totalFrames: "  + totalFrames);
				// chamar handler aqui se event.getFrame()==0
				if (event.getFrame()==0 && iHandlerMusicStopped!=null) {
					iHandlerMusicStopped.executarHandler();
				}				
				pos.setPausedOnFrame(event.getFrame());
		    }
		};
		return listener;
	}

	private void p(int t) {	try { Thread.sleep(t); } catch (InterruptedException e) { e.printStackTrace(); }}
	
	@Override
	public String toString() {
		return "PlayMp3 [filename=" + filename + ", totalFrames=" + totalFrames
				+ ", pos=" + pos + ", isPlaying=" + isPlaying + "]";
	}
	
}