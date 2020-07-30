package hr.fer.zemris.gthpw.image.algorithms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class Threshold {

	public static BufferedImage applyThreshold(int[] pixels, Dimension imageDimension, int threshold) {
		
		int width = imageDimension.width;
		int height = imageDimension.height;
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(pixels[y*width + x] >= threshold) {
					image.setRGB(x, y, Color.WHITE.getRGB());
				} else {
					image.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}
		
		return image;
	}
	
}