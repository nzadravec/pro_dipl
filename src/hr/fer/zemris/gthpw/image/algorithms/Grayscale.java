package hr.fer.zemris.gthpw.image.algorithms;

import java.awt.image.BufferedImage;

public class Grayscale {

	public static boolean isImageGray(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();

		int pixel, red, green, blue;

		// indicates if image is NOT gray
		boolean notGray = false;

		Outer: for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// scan through each pixel
				pixel = image.getRGB(x, y);
				red = (pixel >> 16) & 0xff;
				green = (pixel >> 8) & 0xff;
				blue = (pixel) & 0xff;

				// check if R=G=B
				if (red != green || green != blue) {
					notGray = true;
					break Outer;
				}

			}
		}

		return !notGray;
	}

	public static BufferedImage grayscaleImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		
		BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		int pixel, alpha, red, green, blue, gray;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				pixel = image.getRGB(x, y);
				alpha = (pixel >> 24) & 0xff;
				red = (pixel >> 16) & 0xff;
				green = (pixel >> 8) & 0xff;
				blue = pixel & 0xff;
				gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

				pixel = (alpha << 24) | (gray << 16) | (gray << 8) | gray;

				grayscaleImage.setRGB(x, y, pixel);
			}
		}

		return grayscaleImage;
	}

	public static int[] imageToGrayPixels(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		int[] pixels = new int[height * width];

		boolean grayImage = isImageGray(image);

		int pixel, red, green, blue, gray;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				pixel = image.getRGB(x, y);
				if (grayImage) {
					gray = (pixel >> 16) & 0xff;
				} else {
					red = (pixel >> 16) & 0xff;
					green = (pixel >> 8) & 0xff;
					blue = pixel & 0xff;
					gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
				}

				pixels[y * width + x] = gray;
			}
		}

		return pixels;
	}

}