package net.bemacized.simpleplyrenderer;

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Model {

	private final int[][] faces;
	private final Vector3[] vertices;
	private final boolean verbose;

	public Model(int[][] faces, Vector3[] vertices) {
		this(faces, vertices, false);
	}

	public Model(int[][] faces, Vector3[] vertices, boolean verbose) {
		this.faces = faces;
		this.vertices = vertices;
		this.verbose = verbose;
	}

	public Model(File plyFile) throws IOException {
		this(plyFile, false);
	}

	public Model(File plyFile, boolean verbose) throws IOException {
		this.verbose = verbose;

		// Load file
		PlyReader plyReader = new PlyReaderFile(plyFile);

		// Allocate vertex and face arrays
		vertices = new Vector3[plyReader.getElementCount("vertex")];
		faces = new int[plyReader.getElementCount("face")][3];

		// Populate vertex and face arrays
		ElementReader elReader = plyReader.nextElementReader();
		while (elReader != null) {
			switch (elReader.getElementType().getName()) {
				case "vertex": {
					Element vertexEl = elReader.readElement();
					int vertexIndex = 0;
					while (vertexEl != null) {
						vertices[vertexIndex] = new Vector3(
								(float) vertexEl.getDouble("x"),
								(float) vertexEl.getDouble("y"),
								(float) vertexEl.getDouble("z")
						);
						vertexIndex++;
						vertexEl = elReader.readElement();
					}
				}
				case "face": {
					Element faceEl = elReader.readElement();
					int faceIndex = 0;
					while (faceEl != null) {
						for (int i = 0; i < 3; i++) {
							faces[faceIndex][i] = faceEl.getIntList("vertex_indices")[i];
						}
						faceIndex++;
						faceEl = elReader.readElement();
					}
				}
			}
			elReader.close();
			elReader = plyReader.nextElementReader();
		}

		if (verbose)
			System.out.println("Loaded ply with " + faces.length + " faces and " + vertices.length + " vertices!");
	}

	public int[][] getFaces() {
		return faces;
	}

	public Vector3[] getVertices() {
		return vertices;
	}

	public Model rotateX(float degrees) {
		return rotateX(degrees, Vector3.ZERO);
	}

	public Model rotateY(float degrees) {
		return rotateY(degrees, Vector3.ZERO);
	}

	public Model rotateZ(float degrees) {
		return rotateZ(degrees, Vector3.ZERO);
	}

	public Model rotateX(float degrees, Vector3 origin) {
		return rotate(new Vector3(degrees, 0, 0), origin);
	}

	public Model rotateY(float degrees, Vector3 origin) {
		return rotate(new Vector3(0, degrees, 0), origin);
	}

	public Model rotateZ(float degrees, Vector3 origin) {
		return rotate(new Vector3(0, 0, degrees), origin);
	}

	public Model rotate(Vector3 degrees) {
		return rotate(degrees, Vector3.ZERO);
	}

	public Model rotate(Vector3 degrees, Vector3 origin) {
		Vector3[] vertices = Arrays.stream(this.vertices)
				.parallel()
				.map(vertex -> vertex.rotate(origin, degrees))
				.collect(Collectors.toList())
				.toArray(new Vector3[this.vertices.length]);
		return new Model(Utils.copyIntMatrix(faces), vertices, verbose);
	}

	public Vector3 getMin() {
		return new Vector3(getMinX(), getMinY(), getMinZ());
	}

	public Vector3 getMax() {
		return new Vector3(getMaxX(), getMaxY(), getMaxZ());
	}

	public Vector3 getCenter() {
		return getMax().subtract(getMin()).multiply(.5f).add(getMin());
	}

	public float getMinX() {
		return (float) Arrays.stream(vertices).parallel().mapToDouble(Vector3::getX).min().orElse(0);
	}

	public float getMinY() {
		return (float) Arrays.stream(vertices).parallel().mapToDouble(Vector3::getY).min().orElse(0);
	}

	public float getMinZ() {
		return (float) Arrays.stream(vertices).parallel().mapToDouble(Vector3::getZ).min().orElse(0);
	}

	public float getMaxX() {
		return (float) Arrays.stream(vertices).parallel().mapToDouble(Vector3::getX).max().orElse(0);
	}

	public float getMaxY() {
		return (float) Arrays.stream(vertices).parallel().mapToDouble(Vector3::getY).max().orElse(0);
	}

	public float getMaxZ() {
		return (float) Arrays.stream(vertices).parallel().mapToDouble(Vector3::getZ).max().orElse(0);
	}

	public Model center() {
		Vector3 center = getCenter();
		Vector3[] vertices = Arrays.stream(this.vertices)
				.parallel()
				.map(v -> v.subtract(center))
				.collect(Collectors.toList())
				.toArray(new Vector3[this.vertices.length]);
		return new Model(Utils.copyIntMatrix(faces), vertices, verbose);
	}

	public Model normalize() {
		Vector3[] vertices = center().vertices;
		// Normalize to max length
		float maxLength = (float) Arrays.stream(vertices)
				.mapToDouble(Vector3::length)
				.max()
				.orElse(0);
		vertices = Arrays.stream(this.vertices)
				.parallel()
				.map(v -> v.divide(maxLength))
				.collect(Collectors.toList())
				.toArray(new Vector3[this.vertices.length]);
		return new Model(Utils.copyIntMatrix(faces), vertices, verbose);
	}

	public Model fit(Vector3 min, Vector3 max) {
		Model model = move(getMin().invert());
		model = model
				.scale(1 / model.getMax().maxComponentLength() * max.subtract(min).maxComponentLength())
				.center()
				.move(max.subtract(min).multiply(.5f).add(min));
		return model;
	}

	public Model scale(float factor) {
		return scale(factor, Vector3.ZERO);
	}

	public Model scale(float factor, Vector3 origin) {
		return scale(new Vector3(factor, factor, factor), origin);
	}

	public Model scale(Vector3 factor, Vector3 origin) {
		Model model = move(origin.invert());
		Vector3[] vertices = Arrays.stream(model.vertices)
				.parallel()
				.map(vector -> vector.multiply(factor))
				.collect(Collectors.toList())
				.toArray(new Vector3[this.vertices.length]);
		return new Model(Utils.copyIntMatrix(faces), vertices, verbose).move(origin);
	}

	public Model moveX(float distance) {
		return move(new Vector3(distance, 0, 0));
	}

	public Model moveY(float distance) {
		return move(new Vector3(0, distance, 0));
	}

	public Model moveZ(float distance) {
		return move(new Vector3(0, 0, distance));
	}

	public Model move(Vector3 distance) {
		Vector3[] vertices = Arrays.stream(this.vertices)
				.parallel()
				.map(vector -> vector.add(distance))
				.collect(Collectors.toList())
				.toArray(new Vector3[this.vertices.length]);
		return new Model(Utils.copyIntMatrix(faces), vertices, verbose);
	}

	public BufferedImage render(int width, int height) {
		return render(width, height, new ViewPort(new Vector2(-1, -1), new Vector2(1, 1)));
	}

	public BufferedImage render(int width, int height, RenderStyle renderStyle) {
		return render(width, height, renderStyle, new ViewPort(new Vector2(-1, -1), new Vector2(1, 1)));
	}

	public BufferedImage render(int width, int height, ViewPort viewPort) {
		return render(width, height, RenderStyle.SILHOUETTE, viewPort);
	}

	public BufferedImage render(int width, int height, RenderStyle renderStyle, ViewPort viewPort) {
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bimg.createGraphics();

		// Draw Background
//		g.setColor(renderStyle.getBgColor());
//		g.fillRect(0, 0, width, height);

		float minX = getMinX(), minY = getMinY(), minZ = getMinZ(), maxX = getMaxX(), maxY = getMaxY(), maxZ = getMaxZ();

		// Enable AA
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw polygons
		Arrays.stream(getFaces())
				.sorted(renderStyle.getFaceComparator(this))
				.forEach(face -> {
					Polygon p = new Polygon();
					Arrays.stream(face)
							.mapToObj(vI -> vertices[vI])
							.forEach(v -> {
								Vector2 pos = viewPort.getNormalizedVectorInViewPort(v.getVectorXY()).multiply(width, height);
								p.addPoint(Math.round(pos.getX()), height - Math.round(pos.getY()));
							});
					switch (renderStyle) {
						case AXIS_RGB_INVERTED:
						case AXIS_RGB:
							int _r = (int) ((Arrays.stream(face).mapToDouble(vertexIndex -> vertices[vertexIndex].getX()).average().orElse(0d) - minX) / (maxX - minX) * 255d);
							int _g = (int) ((Arrays.stream(face).mapToDouble(vertexIndex -> vertices[vertexIndex].getY()).average().orElse(0d) - minY) / (maxY - minY) * 255d);
							int _b = (int) ((Arrays.stream(face).mapToDouble(vertexIndex -> vertices[vertexIndex].getZ()).average().orElse(0d) - minZ) / (maxZ - minZ) * 255d);
							g.setColor(new Color(_r, _g, _b));
							break;
						case SILHOUETTE_INVERTED:
							g.setColor(Color.WHITE);
							break;
						default:
						case SILHOUETTE:
							g.setColor(Color.BLACK);
							break;
					}

					g.fillPolygon(p);
				});

		// Fill in missing gaps
		if (renderStyle == RenderStyle.SILHOUETTE || renderStyle == RenderStyle.SILHOUETTE_INVERTED) {

		}

		return bimg;
	}

	public static class ViewPort {
		private Vector2 from;
		private Vector2 to;

		public ViewPort(Vector2 from, Vector2 to) {
			this.from = new Vector2(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()));
			this.to = new Vector2(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()));
		}

		public Vector2 getFrom() {
			return from;
		}

		public Vector2 getTo() {
			return to;
		}

		public Vector2 getNormalizedVectorInViewPort(Vector2 vector) {
			return vector.subtract(from).divide(to.subtract(from));
		}
	}

	public void exportWavefrontOBJ(File file) throws IOException {
		if (verbose) System.out.println("Exporting wavefront model...");
		StringBuilder content = new StringBuilder();
		for (Vector3 vertex : vertices)
			content.append("v ")
					.append(vertex.getX())
					.append(" ")
					.append(vertex.getY())
					.append(" ")
					.append(vertex.getZ())
					.append("\n");
		for (int[] face : faces) {
			content.append("f ").append(String.join(" ", Arrays.toString(face).split("[\\[\\]]")[1].split(", "))).append("\n");
		}
		if (!file.exists())
			file.createNewFile();
		PrintWriter pw = new PrintWriter(file);
		pw.write(content.toString());
		pw.close();
		if (verbose) System.out.println("Exported wavefront model to " + file.getAbsolutePath());
	}

	private static final Function<Integer, Function<Model, Comparator<int[]>>> DEPTH_SORT = (dir) -> (model) -> (a, b) -> {
		float avgZA = (float) Arrays.stream(a).mapToDouble(vId -> model.getVertices()[vId].getZ()).average().orElse(0);
		float avgZB = (float) Arrays.stream(b).mapToDouble(vId -> model.getVertices()[vId].getZ()).average().orElse(0);
		float diff = avgZB - avgZA;
		if (diff > 0) return dir;
		else if (diff < 0) return -dir;
		return 0;
	};

	public enum RenderStyle {

		SILHOUETTE(Color.WHITE, (model) -> (a, b) -> 1),
		SILHOUETTE_INVERTED(Color.BLACK, (model) -> (a, b) -> 1),
		AXIS_RGB(Color.BLACK, DEPTH_SORT.apply(1)),
		AXIS_RGB_INVERTED(Color.BLACK, DEPTH_SORT.apply(-1));

		private Color bgColor;
		private Function<Model, Comparator<int[]>> faceComparator;

		RenderStyle(Color bgColor, Function<Model, Comparator<int[]>> faceComparator) {
			this.faceComparator = faceComparator;
		}

		public Comparator<int[]> getFaceComparator(Model model) {
			return faceComparator.apply(model);
		}

		public Color getBgColor() {
			return bgColor;
		}
	}
}
