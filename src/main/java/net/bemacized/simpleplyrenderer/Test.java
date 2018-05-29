package net.bemacized.simpleplyrenderer;

import com.mortennobel.imagescaling.ResampleOp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

	public static void main(String[] args) throws IOException {

		// PARAMS
		final int THREADS = 8;
		File outputDir = new File("output");
		File[] allModels = concat(
				new File("/home/bemacized/Downloads/CAESAR/NL A standing/scans").listFiles(),
				new File("/home/bemacized/Downloads/CAESAR/Italy A standing/scans").listFiles()
		);
		int renderResolution = 2048;
		int finalResolution = 128;
		Model.RenderStyle renderStyle = Model.RenderStyle.SILHOUETTE;
		Model.ViewPort viewPort = new Model.ViewPort(new Vector2(-1, -1), new Vector2(1, 1));

		// ENSURE OUTPUT DIR EXISTS
		if (!outputDir.exists()) outputDir.mkdirs();

		// SPLIT UP MODELS INTO SUBSETS PER THREAD
		List<File[]> modelSubsets = new ArrayList<>();
		for (int thread = 0; thread < THREADS; thread++) {
			int from = allModels.length / THREADS * thread;
			int to = thread == THREADS - 1
					? allModels.length
					: Math.min(allModels.length / THREADS * (thread + 1), allModels.length);
			modelSubsets.add(Arrays.copyOfRange(allModels, from, to));
		}
		for (int subsetIndex = 0; subsetIndex < modelSubsets.size(); subsetIndex++) {
			File[] modelSubset = modelSubsets.get(subsetIndex);
			int finalSubsetIndex = subsetIndex;
			new Thread(() -> {
				for (int modelSubsetIndex = 0; modelSubsetIndex < modelSubset.length; modelSubsetIndex++) {
					renderModel(
							modelSubset[modelSubsetIndex],
							renderResolution,
							finalResolution,
							renderStyle,
							viewPort,
							outputDir
					);
					System.out.println("T" + finalSubsetIndex + ": " + modelSubsetIndex + "/" + modelSubset.length);
				}
			}).start();
		}
	}

	public static void renderModel(File modelFile, int renderResolution, int finalResolution, Model.RenderStyle renderStyle, Model.ViewPort viewPort, File outputDir) {
		ResampleOp resampleOp = new ResampleOp(finalResolution, finalResolution);
		try {
			Model model = new Model(modelFile, false)
					.rotateX(-90)
					.rotateY(-45)
					.fit(new Vector3(-1, -1, -1), new Vector3(1, 1, 1));

			BufferedImage img = model.render(renderResolution, renderResolution, renderStyle, viewPort);
			img = resampleOp.filter(img, null);
			ImageIO.write(img, "png", new File(outputDir.getAbsolutePath() + "/" + modelFile.getName().substring(0, modelFile.getName().length() - 4) + "_front.png"));
			img = model.rotateY(90).render(renderResolution, renderResolution, renderStyle, viewPort);
			img = resampleOp.filter(img, null);
			ImageIO.write(img, "png", new File(outputDir.getAbsolutePath() + "/" + modelFile.getName().substring(0, modelFile.getName().length() - 4) + "_side.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
