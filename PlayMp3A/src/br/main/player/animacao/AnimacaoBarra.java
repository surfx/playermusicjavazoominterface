package br.main.player.animacao;

import java.util.function.Function;

import javax.swing.JLabel;

public class AnimacaoBarra {

	private boolean executaranimacao = true;
	private Thread thAnimacao = null;
	private JLabel lblStatus;
	
	private int posicaoMusicaHandler;

	public AnimacaoBarra(JLabel lblStatus) {
		this.lblStatus = lblStatus;
	}
	
	public int getPosicaoMusicaHandler() {
		return posicaoMusicaHandler;
	}

	public void setPosicaoMusicaHandler(int posicaoMusicaHandler) {
		this.posicaoMusicaHandler = posicaoMusicaHandler;
	}
	
	@SuppressWarnings("deprecation")
	public void preencherBarraStatus(String texto) {
		if (thAnimacao!=null) { 
			thAnimacao.stop(); 
			executaranimacao = false; 
		}
		if (lblStatus==null) { 
			executaranimacao=false;
			if (thAnimacao!=null) { thAnimacao.stop(); }
			return;
		} 
		lblStatus.setText("");
		if (texto==null||texto.trim().isEmpty()) {
			if (thAnimacao!=null) { thAnimacao.stop(); }
			executaranimacao=false; 
			return;
		}
		
		lblStatus.setText(texto);
		executaranimacao = true;
		p(100);
		
		thAnimacao = new Thread(new Runnable() {
			@Override
			public void run() {
				while(executaranimacao) {
					if (!executaranimacao) {return;}
					
					for(int i = 0; i < texto.length(); i++) {
						lblStatus.setText(texto.substring(i) + " - " + formatHMS(posicaoMusicaHandler));
						p(300);
					}
					lblStatus.setText(" - " + formatHMS(posicaoMusicaHandler));
					p(300);
					for(int i = texto.length()-1; i >=0; i--) {
						lblStatus.setText(texto.substring(i) + " - " + formatHMS(posicaoMusicaHandler));
						p(300);
					}

				}
			}
		});
		thAnimacao.start();
	}
	
	private String formatHMS(int tempoMilisegundos) {
		tempoMilisegundos = tempoMilisegundos /1000; //segundos
        int horas = tempoMilisegundos / 3600;
        int minutos = (tempoMilisegundos - (horas * 3600)) / 60;
        int segundos = tempoMilisegundos - (horas * 3600) - (minutos * 60);
        Function<Integer, String> f = s -> {
        	return ((s+"").length()<2 ? "0" : "")+s;
        };
        return f.apply(horas) + ":" + f.apply(minutos) + ":" + f.apply(segundos);
	}
	
	private void p(long t) { try { Thread.sleep(t); } catch (InterruptedException e) { } }
	
}