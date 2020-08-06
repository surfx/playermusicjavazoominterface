package br.main.player.util;

import java.security.SecureRandom;

public class RandomNumber {

	private static final SecureRandom rand = new SecureRandom();

	public static int getRandomNumber(int min, int max) {
		return getRandomNumber(min, max, null);
	}

	public static int getRandomNumber(int min, int max, Integer posAtual) {
		int rt = rand.nextInt((max - min) + 1) + min;
		if (posAtual==null) {return rt;}
		while (rt == posAtual) {
			rt = rand.nextInt((max - min) + 1) + min;
		}
		return rt; // return (int) ((Math.random() * (max - min)) + min);
	}

}