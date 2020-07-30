package hr.fer.zemris.gthpw.image.binary;

import java.awt.Point;

public interface BinaryImage {

	int getWidth();

	int getHeight();

	boolean getValueAt(int x, int y);
	
	boolean getValueAt(Point p);

}