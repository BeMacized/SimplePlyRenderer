package net.bemacized.simpleplyrenderer;

public class Utils {

	public static float[] rotatePoint2D(float originX, float originY, float pointX, float pointY, float angle) {
		angle = (float) Math.toRadians(angle);
		float s = (float) Math.sin(angle);
		float c = (float) Math.cos(angle);
		pointX -= originX;
		pointY -= originY;
		float newPointX = pointX * c - pointY * s + originX;
		float newPointY = pointX * s + pointY * c + originY;
		return new float[]{newPointX, newPointY};
	}

	public static int[][] copyIntMatrix(int[][] src) {
		int length = src.length;
		int[][] target = new int[length][src[0].length];
		for (int i = 0; i < length; i++) {
			System.arraycopy(src[i], 0, target[i], 0, src[i].length);
		}
		return target;
	}

	public static float[][] copyFloatMatrix(float[][] src) {
		int length = src.length;
		float[][] target = new float[length][src[0].length];
		for (int i = 0; i < length; i++) {
			System.arraycopy(src[i], 0, target[i], 0, src[i].length);
		}
		return target;
	}
}