package net.bemacized.simpleplyrenderer;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderTest {

	public static void main(String[] args) throws IOException {
		File[] landmarkFiles = concat(
				new File("/home/bemacized/Downloads/CAESAR/NL A standing/landmarks").listFiles(),
				new File("/home/bemacized/Downloads/CAESAR/Italy A standing/landmarks").listFiles()
		);

		List<Landmark> allLandmarks = new ArrayList<>();

		for (File file : landmarkFiles) {
			String personId = file.getName().substring(0, file.getName().length() - 4);
			String contents = FileUtils.readFileToString(file);
			contents = contents.substring(contents.indexOf("\n", contents.indexOf("AUX =")), contents.indexOf("END =")).trim();
			String[] lines = contents.split("\n");
			if (lines.length != 73) continue;
			// Parse landmark
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
			// Center around crotch
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
			// Scale to -1,-1,-1_1,1,1 area
			double maxDistance = landmarks.parallelStream()
					.flatMapToDouble(l -> Arrays.stream(new double[]{l.getX(), l.getY(), l.getZ()}))
					.map(Math::abs)
					.max()
					.orElse(Double.NaN);
			for (Landmark landmark : landmarks) {
				landmark.setX(landmark.getX() * (1000000d / maxDistance));
				landmark.setY(landmark.getY() * (1000000d / maxDistance));
				landmark.setZ(landmark.getZ() * (1000000d / maxDistance));
			}

			allLandmarks.addAll(landmarks);
		}

		PrintWriter pw = new PrintWriter("output/landmarks.csv");
		pw.write("personId;index;longitude;latitude;radius;x;y;z;name");
		pw.write("\n");
		for (Landmark landmark : allLandmarks) {
			pw.write(landmark.toString());
			pw.write("\n");
		}
		pw.close();

		System.out.println("DONE");

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

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
