package hr.fer.zemris.gthpw.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import hr.fer.zemris.gthpw.image.algorithms.Grayscale;
import hr.fer.zemris.gthpw.image.algorithms.OtsuThresholder;
import hr.fer.zemris.gthpw.image.algorithms.Threshold;

public class ImageUtil {

	public static BufferedImage createThresholdedImage(BufferedImage image) {
		int[] imgGrayPixels = Grayscale.imageToGrayPixels(image);
		int threshold = OtsuThresholder.getThreshold(imgGrayPixels, 256);
		
		Dimension imageDimension = new Dimension(image.getWidth(), image.getHeight());
		BufferedImage thresholdedImage = Threshold.applyThreshold(imgGrayPixels, imageDimension, threshold);
		return thresholdedImage;
	}
	
}
