package net.bemacized.simpleplyrenderer;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SimplePlyRenderer {

	public static void main(String[] args) {
		File modelFile = null;
		int resolution = 512;
		Model.RenderStyle renderStyle = Model.RenderStyle.SILHOUETTE;
		File outputFile = new File("render.png");
		float xrot = 0;
		float yrot = 0;
		float zrot = 0;
		boolean verbose = false;
		// Obtain parameters
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "--verbose":
				case "-v": {
					verbose = true;
					break;
				}
				case "--rotation":
				case "-rot": {
					i++;
					try {
						String[] rotstrs = args[i].split(",");
						try {
							if (rotstrs.length != 3) {
								throw new NumberFormatException();
							}
							xrot = Float.parseFloat(rotstrs[0]);
							yrot = Float.parseFloat(rotstrs[1]);
							zrot = Float.parseFloat(rotstrs[2]);
						} catch (NumberFormatException e) {
							System.err.println("Parameter -rot (rotation) expects its value in degrees per axis, formatted like 'x,y,z' (e.g. '4.5,30,6.2)");
							return;
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("Parameter -rot (rotation) expects a value.");
						return;
					}
					break;
				}
				case "--help":
				case "-h": {
					printHelp();
					break;
				}
				case "--resolution":
				case "-res": {
					i++;
					try {
						resolution = Integer.parseInt(args[i]);
						if (resolution <= 0) throw new NumberFormatException();
					} catch (NumberFormatException e) {
						System.err.println("Parameter -res (resolution) only takes positive integers.");
						return;
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("Parameter -res (resolution) expects a value.");
						return;
					}
					break;
				}
				case "--output":
				case "-o": {
					i++;
					try {
						outputFile = new File(args[i]);
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("Parameter -o (output) expects a value.");
						return;
					}
					break;
				}
				case "--style":
				case "-s": {
					i++;
					try {
						renderStyle = Model.RenderStyle.valueOf(args[i].toUpperCase());
					} catch (IllegalArgumentException e) {
						System.err.println("The provided render style is invalid. The following options are available: [" + String.join(", ", Arrays.stream(Model.RenderStyle.values()).map(Enum::name).collect(Collectors.toList())));
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("Parameter -s (style) expects a value.");
						return;
					}
					break;
				}
				default: {
					if (modelFile != null) {
						System.err.println("You cannot provide the source model more than once.");
						return;
					}
					modelFile = new File(args[i]);
					if (!modelFile.exists()) {
						System.err.println("The provided source model could not be found.");
						return;
					}
				}
			}
		}

		// Show help if needed
		if (args.length == 0 || modelFile == null) {
			printHelp();
			return;
		}

		// Load model
		if (verbose) System.out.println("Loading model...");
		Model model;
		try {
			model = new Model(modelFile, verbose);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("The provided source model could not be loaded.");
			System.exit(1);
			return;
		}

		// Render model
		BufferedImage render = model.normalizeVertices(true).rotate(xrot, yrot, zrot, .5f, .5f, .5f).render(resolution, renderStyle);
		if (verbose) System.out.println("Model rendered");

		// Save model
		if (verbose) System.out.println("Saving render...");
		try {
			ImageIO.write(render, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("The rendered image could not be saved.");
			System.exit(1);
			return;
		}
		System.out.println("The render has been saved at " + outputFile.getAbsolutePath());
	}

	private static void printHelp() {
		System.out.println(new StringBuilder()
				.append("Simple PLY Renderer\n")
				.append("Author: Bodhi Mulders (BeMacized)\n")
				.append("\n")
				.append("Usage: java -jar SimplePlyRenderer.jar <inputModel> [options]")
				.append("\n")
				.append("Parameters:\n")
				.append("\n")
				.append("-h, --help:\n")
				.append("\tShow this help text\n")
				.append("\n")
				.append("-o <outputFile>,  --output <outputFile>\n")
				.append("\tSpecify what file to output the rendered image to (Default render.png)\n")
				.append("\n")
				.append("-res <resolution>, --resolution <resolution>\n")
				.append("\tSpecify what resolution the resulting image will be (Default 512)\n")
				.append("\n")
				.append("-rot <x rotation,y rotation,z rotation>, --rotation <x rotation,y rotation,z rotation>\n")
				.append("\tSpecify how the model should be rotated (Default '0,0,0')\n")
				.append("\n")
				.append("-v, --verbose\n")
				.append("\tEnables verbose behaviour\n")
				.append("\n")
				.append("-s <SILHOUETTE|AXIS_RGB|AXIS_RGB_INVERTED>, --style <SILHOUETTE|AXIS_RGB|AXIS_RGB_INVERTED>\n")
				.append("\tSpecify the render style out of the available options")
				.toString()
		);
	}


}