package hr.fer.zemris.gthpw.image.binary;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class BinaryImageUtil {
	
	private static final Color FOREGROUND = Color.BLACK;
	private static final Color BACKGROUND = Color.WHITE;

	public static BinaryImage createBoolArrayBinaryImage(BufferedImage image) {
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		boolean[] pixels = new boolean[width * height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color c = new Color(image.getRGB(x, y));
				if(c.equals(FOREGROUND)) {
					pixels[y * width + x] = true;
				} else if(!c.equals(BACKGROUND)) {
					throw new IllegalArgumentException("input image is not black and white");
				}
			}
		}
		
		return new BoolArrayBinaryImage(width, height, pixels, false);
	}
	
}
