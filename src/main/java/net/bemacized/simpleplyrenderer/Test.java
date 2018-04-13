package net.bemacized.simpleplyrenderer;

import com.mortennobel.imagescaling.ResampleOp;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Test {

	public static void main(String[] args) throws IOException {

		// PARAMS
		final int THREADS = 8;
		File outputDir = new File("output");
		File modelDir = new File("/home/bemacized/Downloads/CAESAR/NL A standing/scans");
		File dataFile = new File("/home/bemacized/Downloads/CAESAR/NL info/data/DutchMeasurements.xls");
//		File modelDir = new File("/home/bemacized/Downloads/CAESAR/Italy A standing/scans");
//		File dataFile = new File("/home/bemacized/Downloads/CAESAR/Italy info/data/ItalyMeasurements.xls");
		int renderResolution = 2048;
		int finalResolution = 256;
		Model.RenderStyle renderStyle = Model.RenderStyle.SILHOUETTE;
		Model.ViewPort viewPort = new Model.ViewPort(new Vector2(-1, -1), new Vector2(1, 1));

		// LOAD DATA FILE
		Workbook wb = new HSSFWorkbook(new FileInputStream(dataFile));
		Sheet dataSheet = wb.getSheetAt(0);

		// LOAD SUBJECT HEIGHTS
		Map<String, Float> heights = new HashMap<>();
		for (Row row : dataSheet) {
			if (row.getRowNum() == 0) continue;
			try {
//				heights.put(String.valueOf((int) row.getCell(0).getNumericCellValue()), (float) row.getCell(34).getNumericCellValue()); // ITALIAN
				heights.put(String.valueOf((int) row.getCell(1).getNumericCellValue()), (float) row.getCell(33).getNumericCellValue()); // DUTCH
			} catch (IllegalStateException ex) {
				System.err.println("COULD NOT EXTRACT HEIGHT FOR SUBJECT " + row.getCell(0).getNumericCellValue());
			}
		}

		// RELATIVISE HEIGHTS TO TALLEST PERSON
//		float maxHeight = (float) heights.values().stream().mapToDouble(h -> h).max().orElse(0d);
//		float maxHeight = resolution * 10;
		float maxHeight = 2183f; // TALLEST OF M
		for (String key : heights.keySet()) {
			if (heights.get(key) > maxHeight) {
				System.err.println("WTF" + heights.get(key));
				return;
			}
			heights.put(key, heights.get(key) / maxHeight);
		}

		// ENSURE OUTPUT DIR EXISTS
		if (!outputDir.exists()) outputDir.mkdirs();

		// SPLIT UP MODELS INTO SUBSETS PER THREAD
		File[] allModels = modelDir.listFiles();
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
					String subjectId = modelSubset[modelSubsetIndex].getName().replaceAll("\\D+", "");
					if (!heights.containsKey(subjectId)) {
						System.err.println("NO HEIGHT AVAILABLE FOR SUBJECT " + subjectId);
						continue;
					}
					renderModel(
							modelSubset[modelSubsetIndex],
							renderResolution,
							finalResolution,
							renderStyle,
							viewPort,
							outputDir,
							heights.get(subjectId)
					);
					System.out.println("T" + finalSubsetIndex + ": " + modelSubsetIndex + "/" + modelSubset.length);
				}
			}).start();
		}
	}

	public static void renderModel(File modelFile, int renderResolution, int finalResolution, Model.RenderStyle renderStyle, Model.ViewPort viewPort, File outputDir, float scaleFactor) {
		ResampleOp resampleOp = new ResampleOp (finalResolution,finalResolution);
		try {
			Model model = new Model(modelFile, false)
					.rotateX(-90)
					.rotateY(-45)
					.fit(new Vector3(-1, -1, -1), new Vector3(1, 1, 1))
					.scale(scaleFactor, new Vector3(0, -1, 0));

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
}
