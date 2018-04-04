package net.bemacized.simpleplyrenderer;

import java.util.Arrays;
import java.util.Iterator;

public class Vector2 implements Iterable<Float> {

	public static final Vector2 ZERO = new Vector2(0, 0);

	private final float x;
	private final float y;

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Vector2 abs() {
		return new Vector2(Math.abs(x), Math.abs(y));
	}

	public Vector2 add(Vector2 v) {
		return new Vector2(x + v.getX(), y + v.getY());
	}

	public Vector2 add(float x, float y, float z) {
		return new Vector2(this.x + x, this.y + y);
	}

	public Vector2 subtract(Vector2 v) {
		return new Vector2(x - v.getX(), y - v.getY());
	}

	public Vector2 subtract(float x, float y) {
		return new Vector2(this.x - x, this.y - y);
	}

	public Vector2 multiply(float factor) {
		return new Vector2(x * factor, y * factor);
	}

	public Vector2 multiply(Vector2 v) {
		return new Vector2(x * v.getX(), y * v.getY());
	}

	public Vector2 multiply(float x, float y) {
		return new Vector2(this.x * x, this.y * y);
	}

	public Vector2 divide(float factor) {
		return new Vector2(x / factor, y / factor);
	}

	public Vector2 divide(Vector2 v) {
		return new Vector2(x / v.getX(), y / v.getY());
	}

	public Vector2 divide(float x, float y) {
		return new Vector2(this.x / x, this.y / y);
	}

	public Vector2 invert() {
		return multiply(-1);
	}

	public float length() {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	public float maxComponentLength() {
		return Math.max(x, y);
	}

	public float minComponentLength() {
		return Math.min(x, y);
	}

	public Vector2 normalize() {
		float length = length();
		return new Vector2(x / length, y / length);
	}

	public Vector2 clone() {
		return new Vector2(x, y);
	}

	public Vector2 swap() {
		return new Vector2(y, x);
	}

	public Vector2 rotate(Vector2 origin, float degrees) {
		degrees = (float) Math.toRadians(degrees);
		float s = (float) Math.sin(degrees);
		float c = (float) Math.cos(degrees);
		float pointX = x;
		float pointY = y;
		pointX -= origin.getX();
		pointY -= origin.getY();
		float newPointX = pointX * c - pointY * s + origin.getX();
		float newPointY = pointX * s + pointY * c + origin.getY();
		return new Vector2(newPointX, newPointY);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Vector2{");
		sb.append("x=").append(x);
		sb.append(", y=").append(y);
		sb.append(", length=").append(length());
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Iterator<Float> iterator() {
		return Arrays.asList(new Float[]{x, y}).iterator();
	}
}
