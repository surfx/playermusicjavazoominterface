package br.main.player;

public class Posicao {

	private int pausedOnFrame = 0;

	public int getPausedOnFrame() {
		return pausedOnFrame;
	}

	public void setPausedOnFrame(int pausedOnFrame) {
		this.pausedOnFrame = pausedOnFrame;
	}

	@Override
	public String toString() {
		return "Posicao [pausedOnFrame=" + pausedOnFrame + "]";
	}

}