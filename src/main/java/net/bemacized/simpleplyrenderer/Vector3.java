package net.bemacized.simpleplyrenderer;

import java.util.Arrays;
import java.util.Iterator;

public class Vector3 implements Iterable<Float> {

	public static final Vector3 ZERO = new Vector3(0, 0, 0);

	private final float x;
	private final float y;
	private final float z;

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public Vector3 abs() {
		return new Vector3(Math.abs(x), Math.abs(y), Math.abs(z));
	}

	public Vector3 add(Vector3 v) {
		return new Vector3(x + v.getX(), y + v.getY(), z + v.getZ());
	}

	public Vector3 add(float x, float y, float z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	public Vector3 subtract(Vector3 v) {
		return new Vector3(x - v.getX(), y - v.getY(), z - v.getZ());
	}

	public Vector3 subtract(float x, float y, float z) {
		return new Vector3(this.x - x, this.y - y, this.z - z);
	}

	public Vector3 multiply(float factor) {
		return new Vector3(x * factor, y * factor, z * factor);
	}

	public Vector3 multiply(Vector3 v) {
		return new Vector3(x * v.getX(), y * v.getY(), z * v.getZ());
	}

	public Vector3 multiply(float x, float y, float z) {
		return new Vector3(this.x * x, this.y * y, this.z * z);
	}

	public Vector3 divide(float factor) {
		return new Vector3(x / factor, y / factor, z / factor);
	}

	public Vector3 divide(Vector3 v) {
		return new Vector3(x / v.getX(), y / v.getY(), z / v.getZ());
	}

	public Vector3 divide(float x, float y, float z) {
		return new Vector3(this.x / x, this.y / y, this.z / z);
	}


	public float length() {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	public float maxComponentLength() {
		return Math.max(x, Math.max(y, z));
	}

	public float minComponentLength() {
		return Math.min(x, Math.min(y, z));
	}

	public Vector3 normalize() {
		float length = length();
		return new Vector3(x / length, y / length, z / length);
	}

	public Vector3 invert() {
		return multiply(-1);
	}

	public Vector3 clone() {
		return new Vector3(x, y, z);
	}

	public Vector2 getVectorXY() {
		return new Vector2(x, y);
	}

	public Vector2 getVectorYZ() {
		return new Vector2(y, z);
	}

	public Vector2 getVectorXZ() {
		return new Vector2(x, z);
	}

	public Vector3 rotate(Vector3 origin, Vector3 degrees) {
		Vector3 newVector = clone();
		if (degrees.getX() != 0) {
			Vector2 rotation = newVector.getVectorYZ().rotate(origin.getVectorYZ(), degrees.getX());
			newVector = new Vector3(newVector.getX(), rotation.getX(), rotation.getY());
		}
		if (degrees.getY() != 0) {
			Vector2 rotation = newVector.getVectorXZ().rotate(origin.getVectorXZ(), degrees.getY());
			newVector = new Vector3(rotation.getX(), newVector.getY(), rotation.getY());
		}
		if (degrees.getZ() != 0) {
			Vector2 rotation = newVector.getVectorXY().rotate(origin.getVectorXY(), degrees.getZ());
			newVector = new Vector3(rotation.getX(), rotation.getY(), newVector.getZ());
		}
		return newVector;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Vector3{");
		sb.append("x=").append(x);
		sb.append(", y=").append(y);
		sb.append(", z=").append(z);
		sb.append(", length=").append(length());
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Iterator<Float> iterator() {
		return Arrays.asList(new Float[]{x, y, z}).iterator();
	}

	public float[] asArray() {
		return new float[]{x, y, z};
	}
}
