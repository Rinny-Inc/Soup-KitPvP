package us.noks.kitpvp.utils;

import java.util.Random;

public class MathUtils {
	private static MathUtils instance = new MathUtils();

	public static MathUtils getInstance() {
		return instance;
	}

	public int getRandom(int from, int to) {
		return (from < to) ? (from + (new Random()).nextInt(Math.abs(to - from))) : (from - (new Random()).nextInt(Math.abs(to - from)));
	}
}
