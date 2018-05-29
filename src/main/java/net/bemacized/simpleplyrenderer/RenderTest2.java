package net.bemacized.simpleplyrenderer;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderTest2 {

	public static void main(String[] args) throws IOException {
//		List<Landmark> landmarks = getLandmarks(new File("/home/bemacized/Downloads/CAESAR/NL A standing/landmarks").listFiles()[0]);
//		float[] data = new float[]{-0.084970f, -0.061513f, 0.998737f, -0.106713f, 0.000163f, 0.960671f, -0.045457f, -0.091450f, 0.961166f, -0.094227f, -0.064829f, 0.876769f, -0.033897f, 0.042141f, 0.958495f, -0.031235f, 0.000981f, 0.880379f, 0.001239f, -0.082840f, 0.959568f, 0.000756f, -0.076353f, 0.883022f, -0.000638f, 0.018516f, 0.941221f, -0.036478f, -0.000127f, 0.738855f, -0.000162f, -0.004314f, 0.724285f, -0.000154f, -0.038258f, 0.738561f, -0.177171f, 0.001455f, 0.536440f, -0.000768f, -0.184138f, 0.539724f, -0.101083f, -0.078880f, 0.477833f, -0.143115f, 0.000729f, 0.369264f, -0.156687f, 0.058877f, 0.195590f, 0.001948f, -0.158720f, 0.367433f, 0.001087f, -0.171211f, 0.191123f, -0.109500f, 0.148294f, 0.295712f, -0.126297f, 0.174280f, 0.056052f, 0.103589f, -0.159346f, 0.295726f, 0.126229f, -0.174354f, 0.125254f, 0.068730f, 0.025310f, 0.818697f, 0.088182f, 0.051025f, 0.375526f, 0.061927f, 0.118952f, 0.248670f, 0.138973f, 0.000908f, 0.250244f, 0.094733f, 0.058940f, 0.275799f, -0.087764f, 0.214793f, 0.737804f, -0.152753f, 0.172076f, 0.592541f, -0.322835f, 0.345773f, 0.001343f, -0.000026f, 0.248035f, 0.590237f, -0.118380f, 0.296508f, 0.364704f, -0.152839f, 0.316252f, 0.389236f, -0.113191f, 0.254992f, 0.362047f, -0.162794f, 0.323736f, 0.370935f, -0.359754f, 0.363578f, 0.002164f, -0.355248f, 0.434583f, 0.000919f, -0.266314f, 0.391060f, 0.035331f, -0.266990f, 0.410651f, -0.000383f, 0.168179f, -0.168230f, 0.745994f, 0.117536f, -0.201436f, 0.594997f, 0.234101f, -0.411722f, 0.001044f, 0.232731f, -0.122537f, 0.593542f, 0.249627f, -0.222168f, 0.370163f, 0.253881f, -0.261460f, 0.399081f, 0.208193f, -0.209043f, 0.365953f, 0.257254f, -0.272557f, 0.380257f, 0.237109f, -0.447650f, 0.001847f, 0.288684f, -0.450492f, -0.001058f, 0.298011f, -0.382458f, 0.077479f, 0.313416f, -0.384363f, -0.000649f, -0.001081f, 0.164205f, -0.401515f, -0.000853f, 0.171642f, -0.389742f, 0.000209f, 0.038531f, -0.391381f, -0.145088f, 0.156797f, -0.949526f, 0.000106f, 0.180354f, -0.896419f, 0.001440f, 0.090508f, -0.880664f, 0.000133f, 0.090767f, -0.905711f, -0.139488f, 0.005916f, -0.948715f, 0.043385f, 0.157258f, -0.936386f, -0.224714f, 0.064070f, -0.953263f, 0.170114f, 0.000811f, -0.401427f, 0.144188f, -0.110475f, -0.384924f, 0.025341f, 0.001799f, -0.392494f, 0.089234f, -0.187926f, -0.947116f, 0.160586f, -0.059816f, -0.894674f, 0.086729f, -0.000388f, -0.884230f, 0.085855f, -0.001512f, -0.909722f, 0.000081f, -0.136187f, -0.952448f, 0.174056f, 0.001625f, -0.939639f, -0.000413f, -0.223445f, -0.686273f, 0.000000f, 0.000000f, 0.000000f};
		float[] data = new float[]{-0.084970f, -0.061513f, 0.998737f, -0.106713f, 0.000163f, 0.960671f, -0.045457f, -0.091450f, 0.961166f, -0.094227f, -0.064829f, 0.876769f, -0.033897f, 0.042141f, 0.958495f, -0.031235f, 0.000981f, 0.880379f, 0.001239f, -0.082840f, 0.959568f, 0.000756f, -0.076353f, 0.883022f, -0.000638f, 0.018516f, 0.941221f, -0.036478f, -0.000127f, 0.738855f, -0.000162f, -0.004314f, 0.724285f, -0.000154f, -0.038258f, 0.738561f, -0.177171f, 0.001455f, 0.536440f, -0.000768f, -0.184138f, 0.539724f, -0.101083f, -0.078880f, 0.477833f, -0.143115f, 0.000729f, 0.369264f, -0.156687f, 0.058877f, 0.195590f, 0.001948f, -0.158720f, 0.367433f, 0.001087f, -0.171211f, 0.191123f, -0.109500f, 0.148294f, 0.295712f, -0.126297f, 0.174280f, 0.056052f, 0.103589f, -0.159346f, 0.295726f, 0.126229f, -0.174354f, 0.125254f, 0.068730f, 0.025310f, 0.818697f, 0.088182f, 0.051025f, 0.375526f, 0.061927f, 0.118952f, 0.248670f, 0.138973f, 0.000908f, 0.250244f, 0.094733f, 0.058940f, 0.275799f, -0.087764f, 0.214793f, 0.737804f, -0.152753f, 0.172076f, 0.592541f, -0.322835f, 0.345773f, 0.001343f, -0.000026f, 0.248035f, 0.590237f, -0.118380f, 0.296508f, 0.364704f, -0.152839f, 0.316252f, 0.389236f, -0.113191f, 0.254992f, 0.362047f, -0.162794f, 0.323736f, 0.370935f, -0.359754f, 0.363578f, 0.002164f, -0.355248f, 0.434583f, 0.000919f, -0.266314f, 0.391060f, 0.035331f, -0.266990f, 0.410651f, -0.000383f, 0.168179f, -0.168230f, 0.745994f, 0.117536f, -0.201436f, 0.594997f, 0.234101f, -0.411722f, 0.001044f, 0.232731f, -0.122537f, 0.593542f, 0.249627f, -0.222168f, 0.370163f, 0.253881f, -0.261460f, 0.399081f, 0.208193f, -0.209043f, 0.365953f, 0.257254f, -0.272557f, 0.380257f, 0.237109f, -0.447650f, 0.001847f, 0.288684f, -0.450492f, -0.001058f, 0.298011f, -0.382458f, 0.077479f, 0.313416f, -0.384363f, -0.000649f, -0.001081f, 0.164205f, -0.401515f, -0.000853f, 0.171642f, -0.389742f, 0.000209f, 0.038531f, -0.391381f, -0.145088f, 0.156797f, -0.949526f, 0.000106f, 0.180354f, -0.896419f, 0.001440f, 0.090508f, -0.880664f, 0.000133f, 0.090767f, -0.905711f, -0.139488f, 0.005916f, -0.948715f, 0.043385f, 0.157258f, -0.936386f, -0.224714f, 0.064070f, -0.953263f, 0.170114f, 0.000811f, -0.401427f, 0.144188f, -0.110475f, -0.384924f, 0.025341f, 0.001799f, -0.392494f, 0.089234f, -0.187926f, -0.947116f, 0.160586f, -0.059816f, -0.894674f, 0.086729f, -0.000388f, -0.884230f, 0.085855f, -0.001512f, -0.909722f, 0.000081f, -0.136187f, -0.952448f, 0.174056f, 0.001625f, -0.939639f, -0.000413f, -0.223445f, -0.686273f, 0.000000f, 0.000000f, 0.000000f};
		List<Landmark> landmarks = new ArrayList<>();
		for (int i = 0; i < data.length; i += 3)
			landmarks.add(new Landmark(null, i / 3, 0, 0, 0, (double) data[i], (double) data[i + 1], (double) data[i + 2], null));
		renderLandmarks(landmarks, new File("output/model.obj"), 0.01f);
	}

	public static void renderLandmarks(List<Landmark> landmarks, File outputModel, float cubesize) throws IOException {
		List<Vector3> vertices = new ArrayList<>();
		int[][] faces = new int[landmarks.size() * 6][4];
		cubesize /= 2;
		for (int i = 0; i < landmarks.size(); i++) {
			// Add faces
			faces[i * 6 + 0] = getFace(vertices.size(), 0, 2, 6, 4);
			faces[i * 6 + 1] = getFace(vertices.size(), 1, 5, 7, 3);
			faces[i * 6 + 2] = getFace(vertices.size(), 0, 4, 5, 1);
			faces[i * 6 + 3] = getFace(vertices.size(), 2, 3, 7, 6);
			faces[i * 6 + 4] = getFace(vertices.size(), 0, 1, 3, 2);
			faces[i * 6 + 5] = getFace(vertices.size(), 7, 5, 4, 6);
			// Add vertices
			Landmark landmark = landmarks.get(i);
			Vector3 origin = new Vector3((float) landmark.getX(), (float) landmark.getY(), (float) landmark.getZ());
			for (float x = -1; x <= 1; x += 2)
				for (float y = -1; y <= 1; y += 2)
					for (float z = -1; z <= 1; z += 2)
						vertices.add(origin.subtract(cubesize * x, cubesize * y, cubesize * z).divide(10f));
		}

		Model model = new Model(faces, vertices.toArray(new Vector3[0]));

		model.exportWavefrontOBJ(outputModel);
	}

	public static int[] getFace(int base, int... indices) {
		for (int i = 0; i < indices.length; i++) {
			indices[i] += base + 1;
		}
		return indices;
	}

	public static class Landmark {
		private String personId;
		private int index;
		private int longitude;
		private int latitude;
		private double radius;
		private double x;
		private double y;
		private double z;
		private String name;

		public Landmark(String personId, int index, int longitude, int latitude, double radius, double x, double y, double z, String name) {
			this.personId = personId;
			this.index = index;
			this.longitude = longitude;
			this.latitude = latitude;
			this.radius = radius;
			this.x = x;
			this.y = y;
			this.z = z;
			this.name = name;
		}

		@Override
		public String toString() {
			return String.join(";", new String[]{personId, index + "", "" + longitude, "" + latitude, "" + radius, "" + x, "" + y, "" + z, name});
		}

		public void setPersonId(String personId) {
			this.personId = personId;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setLongitude(int longitude) {
			this.longitude = longitude;
		}

		public void setLatitude(int latitude) {
			this.latitude = latitude;
		}

		public void setRadius(double radius) {
			this.radius = radius;
		}

		public void setX(double x) {
			this.x = x;
		}

		public void setY(double y) {
			this.y = y;
		}

		public void setZ(double z) {
			this.z = z;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPersonId() {
			return personId;
		}

		public int getIndex() {
			return index;
		}

		public int getLongitude() {
			return longitude;
		}

		public int getLatitude() {
			return latitude;
		}

		public double getRadius() {
			return radius;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getZ() {
			return z;
		}

		public String getName() {
			return name;
		}
	}

	public static List<Landmark> getLandmarks(File file) throws IOException {
		String personId = file.getName().substring(0, file.getName().length() - 4);
		String contents = FileUtils.readFileToString(file);
		contents = contents.substring(contents.indexOf("\n", contents.indexOf("AUX =")), contents.indexOf("END =")).trim();
		String[] lines = contents.split("\n");
		if (lines.length != 73) throw new InvalidParameterException("FILE HAS INCORRECT NUMBER OF LANDMARKS");
		List<Landmark> landmarks = new ArrayList<>();
		for (String line : lines) {
			String[] attributes = line.trim().split("\\s+");
			String landmarkName = String.join(" ", Arrays.copyOfRange(attributes, 7, attributes.length)).trim();
			if (landmarkName.contains("#"))
				landmarkName = landmarkName.substring(0, landmarkName.indexOf("#")).trim();
			int index = Integer.parseInt(attributes[0]);
			int longitude = Integer.parseInt(attributes[1]);
			int latitude = Integer.parseInt(attributes[2]);
			double radius = Double.parseDouble(attributes[3]);
			double x = Double.parseDouble(attributes[4]);
			double y = Double.parseDouble(attributes[5]);
			double z = Double.parseDouble(attributes[6]);
			Landmark landmark = new Landmark(personId, index, longitude, latitude, radius, x, y, z, landmarkName);
			landmarks.add(landmark);
		}
		Landmark crotch = landmarks.parallelStream().filter(l -> l.index == 73).findFirst().orElse(null);
		for (Landmark landmark : landmarks) {
			if (landmark.index != 73) {
				landmark.setX(landmark.getX() - crotch.getX());
				landmark.setY(landmark.getY() - crotch.getY());
				landmark.setZ(landmark.getZ() - crotch.getZ());
			}
		}
		crotch.setX(0);
		crotch.setY(0);
		crotch.setZ(0);
		return landmarks;
	}
}
