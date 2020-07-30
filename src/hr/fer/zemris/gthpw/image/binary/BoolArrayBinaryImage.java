package hr.fer.zemris.gthpw.image.binary;

import java.awt.Point;
import java.util.Arrays;

public class BoolArrayBinaryImage implements BinaryImage {

	private int width;
	private int height;
	private boolean[] pixels;

	public BoolArrayBinaryImage(int width, int height, boolean[] pixels, boolean copyPixels) {
		this.width = width;
		this.height = height;
		
		if(copyPixels) {
			pixels = Arrays.copyOf(pixels, pixels.length);
		}
		
		this.pixels = pixels;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean getValueAt(int x, int y) {
		return pixels[y * width + x];
	}
	
	@Override
	public boolean getValueAt(Point p) {
		return getValueAt(p.x, p.y);
	}

}
