package net.bemacized.simpleplyrenderer;

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public class Model {

	private final int[][] faces;
	private final float[][] vertices;
	private final boolean verbose;

	public Model(int[][] faces, float[][] vertices) {
		this(faces, vertices, false);
	}

	public Model(int[][] faces, float[][] vertices, boolean verbose) {
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
		vertices = new float[plyReader.getElementCount("vertex")][3];
		faces = new int[plyReader.getElementCount("face")][3];

		// Populate vertex and face arrays
		ElementReader elReader = plyReader.nextElementReader();
		while (elReader != null) {
			switch (elReader.getElementType().getName()) {
				case "vertex": {
					Element vertexEl = elReader.readElement();
					int vertexIndex = 0;
					while (vertexEl != null) {
						vertices[vertexIndex][0] = (float) vertexEl.getDouble("x");
						vertices[vertexIndex][1] = (float) vertexEl.getDouble("y");
						vertices[vertexIndex][2] = (float) vertexEl.getDouble("z");
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

	public float[][] getVertices() {
		return vertices;
	}

	public Model rotate(float rotX, float rotY, float rotZ, float originX, float originY, float originZ) {
		int[][] _faces = Utils.copyIntMatrix(faces);
		float[][] _vertices = Utils.copyFloatMatrix(vertices);

		// Rotate around X axis
		if (rotX != 0) {
			for (int vertexIndex = 0; vertexIndex < _vertices.length; vertexIndex++) {
				float[] vertex = _vertices[vertexIndex];
				float[] newData = Utils.rotatePoint2D(originY, originZ, vertex[1], vertex[2], rotX);
				_vertices[vertexIndex][1] = newData[0];
				_vertices[vertexIndex][2] = newData[1];
			}
		}
		// Rotate around Y axis
		if (rotY != 0) {
			for (int vertexIndex = 0; vertexIndex < _vertices.length; vertexIndex++) {
				float[] vertex = _vertices[vertexIndex];
				float[] newData = Utils.rotatePoint2D(originX, originZ, vertex[0], vertex[2], rotY);
				_vertices[vertexIndex][0] = newData[0];
				_vertices[vertexIndex][2] = newData[1];
			}
		}
		// Rotate around Z axis
		if (rotZ != 0) {
			for (int vertexIndex = 0; vertexIndex < _vertices.length; vertexIndex++) {
				float[] vertex = _vertices[vertexIndex];
				float[] newData = Utils.rotatePoint2D(originX, originY, vertex[0], vertex[1], rotZ);
				_vertices[vertexIndex][0] = newData[0];
				_vertices[vertexIndex][1] = newData[1];
			}
		}

		return new Model(_faces, _vertices, verbose);
	}

	private float getMin(int axisIndex) {
		return (float) Arrays.stream(vertices).mapToDouble(v -> v[axisIndex]).min().orElse(0);
	}

	private float getMax(int axisIndex) {
		return (float) Arrays.stream(vertices).mapToDouble(v -> v[axisIndex]).max().orElse(0);
	}

	private float getMinX() {
		return getMin(0);
	}

	private float getMinY() {
		return getMin(1);
	}

	private float getMinZ() {
		return getMin(2);
	}

	private float getMaxX() {
		return getMax(0);
	}

	private float getMaxY() {
		return getMax(1);
	}

	private float getMaxZ() {
		return getMax(2);
	}

	public Model normalizeVertices(boolean centered) {
		int[][] _faces = Utils.copyIntMatrix(faces);
		float[][] _vertices = Utils.copyFloatMatrix(vertices);

		float minX = getMinX(), minY = getMinY(), minZ = getMinZ();

		// Reset vertices relative to 0,0,0 origin
		for (int verticeIndex = 0; verticeIndex < _vertices.length; verticeIndex++) {
			_vertices[verticeIndex][0] -= minX;
			_vertices[verticeIndex][1] -= minY;
			_vertices[verticeIndex][2] -= minZ;
		}

		float maxX = getMaxX() - minX, maxY = getMaxY() - minY, maxZ = getMaxZ() - minZ;

		// Find largest axis
		float maxAll = Math.max(Math.max(maxX, maxY), maxZ);

		// Normalize vertices
		for (int verticeIndex = 0; verticeIndex < _vertices.length; verticeIndex++) {
			_vertices[verticeIndex][0] /= maxAll;
			_vertices[verticeIndex][1] /= maxAll;
			_vertices[verticeIndex][2] /= maxAll;
		}

		// Center if required
		if (centered) {
			for (int verticeIndex = 0; verticeIndex < _vertices.length; verticeIndex++) {
				_vertices[verticeIndex][0] += .5f - maxX / 2f;
				_vertices[verticeIndex][1] += .5f - maxY / 2f;
				_vertices[verticeIndex][2] += .5f - maxZ / 2f;
			}
		}

		return new Model(_faces, _vertices, verbose);
	}

	public BufferedImage render(int resolution, RenderStyle renderStyle) {
		if (verbose) System.out.println("Rendering at resolution " + resolution + " with style " + renderStyle.name());

		// Get a normalized, centered, model
		Model model = this.normalizeVertices(true);

		// Obtain dimensions
		float minX = model.getMinX(), minY = model.getMinY(), minZ = model.getMinZ(), maxX = model.getMaxX(), maxY = model.getMaxY(), maxZ = model.getMaxZ();


		// Initialize image
		BufferedImage bimg = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bimg.createGraphics();

		// Set white background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, resolution, resolution);

		// Draw polygons
		Arrays.stream(model.getFaces())
				.sorted(renderStyle.getFaceComparator(model)).forEach(face -> {
			Polygon p = new Polygon();
			for (int vertexIndex : face) {
				int x = (int) (model.getVertices()[vertexIndex][0] * resolution);
				int y = resolution - (int) (model.getVertices()[vertexIndex][2] * resolution);
				p.addPoint(x, y);
			}
			switch (renderStyle) {
				case DEPTH_MAP_INVERTED:
				case DEPTH_MAP:
					int _v = (int) ((Arrays.stream(face).mapToDouble(vertexIndex -> model.getVertices()[vertexIndex][1]).average().orElse(0d) - minY) / (maxY - minY) * 255d);
					g.setColor(new Color(_v, _v, _v));
					break;
				case AXIS_RGB_INVERTED:
				case AXIS_RGB:
					int _r = (int) ((Arrays.stream(face).mapToDouble(vertexIndex -> model.getVertices()[vertexIndex][0]).average().orElse(0d) - minX) / (maxX - minX) * 255d);
					int _g = (int) ((Arrays.stream(face).mapToDouble(vertexIndex -> model.getVertices()[vertexIndex][1]).average().orElse(0d) - minY) / (maxY - minY) * 255d);
					int _b = (int) ((Arrays.stream(face).mapToDouble(vertexIndex -> model.getVertices()[vertexIndex][2]).average().orElse(0d) - minZ) / (maxZ - minZ) * 255d);
					g.setColor(new Color(_r, _g, _b));
					break;
				default:
				case SILHOUETTE:
					g.setColor(Color.BLACK);
					break;
			}

			g.fillPolygon(p);
		});

		// Return buffered image
		return bimg;
	}


	private static final Function<Integer, Function<Model, Comparator<int[]>>> DEPTH_SORT = (dir) -> (model) -> (a, b) -> {
		float avgYA = (float) Arrays.stream(a).mapToDouble(vId -> model.getVertices()[vId][1]).average().orElse(0);
		float avgYB = (float) Arrays.stream(b).mapToDouble(vId -> model.getVertices()[vId][1]).average().orElse(0);
		float diff = avgYB - avgYA;
		if (diff > 0) return dir;
		else if (diff < 0) return -dir;
		return 0;
	};

	public enum RenderStyle {

		SILHOUETTE((model) -> (a, b) -> 1),
		DEPTH_MAP(DEPTH_SORT.apply(1)),
		DEPTH_MAP_INVERTED(DEPTH_SORT.apply(-1)),
		AXIS_RGB(DEPTH_SORT.apply(1)),
		AXIS_RGB_INVERTED(DEPTH_SORT.apply(-1));

		private Function<Model, Comparator<int[]>> faceComparator;

		RenderStyle(Function<Model, Comparator<int[]>> faceComparator) {
			this.faceComparator = faceComparator;
		}

		public Comparator<int[]> getFaceComparator(Model model) {
			return faceComparator.apply(model);
		}
	}
}
