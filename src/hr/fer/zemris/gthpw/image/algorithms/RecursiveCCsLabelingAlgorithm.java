package hr.fer.zemris.gthpw.image.algorithms;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.image.binary.BinaryImage;

public class RecursiveCCsLabelingAlgorithm {

	/**
	 * 4-neighborhood shifts
	 */
	private static final int[][] NS4 = new int[][] { { -1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 } };
	/**
	 * 8-neighborhood shifts
	 */
	private static final int[][] NS8 = new int[][] { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 },
			{ 1, 0 }, { 1, 1 } };

	private final boolean background;
	private final int[][] neighborhoodShifts;

	private int height;
	private int width;
	private int[][] passedPixels;
	private int label;
	private List<Point> connectedPoints = new ArrayList<>();;

	public RecursiveCCsLabelingAlgorithm(boolean background, Neighborhood neighborhood) {
		this.background = background;

		if (neighborhood == Neighborhood.FOUR) {
			neighborhoodShifts = NS4;
		} else if (neighborhood == Neighborhood.EIGHT) {
			neighborhoodShifts = NS8;
		} else {
			throw new IllegalArgumentException("unknown neighborhood: " + neighborhood);
		}
	}

	public Collection<ImageSegment> findConnectedComponents(BinaryImage image, Rectangle boundingBox) {

		height = boundingBox.height;
		width = boundingBox.width;
		
		passedPixels = new int[height][width];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (image.getValueAt(boundingBox.x + x, boundingBox.y + y) == background) {
					passedPixels[y][x] = -1;
				}
			}
		}

		label = 1;

		List<ImageSegment> connectedComponents = new ArrayList<>();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				if (image.getValueAt(boundingBox.x + x, boundingBox.y + y) == background) {
					passedPixels[y][x] = -1;
				}
				
				if (passedPixels[y][x] == 0) {
					connectedPoints.clear();
					Point startingPoint = new Point(x, y);
					connectedPoints.add(startingPoint);
					passedPixels[startingPoint.y][startingPoint.x] = label;
					search();
					for(Point p : connectedPoints) {
						p.x += boundingBox.x;
						p.y += boundingBox.y;
					}
					connectedComponents.add(new ImageSegment(connectedPoints));
					label++;
				}

			}
		}

		return connectedComponents;
	}

	private void search() {
		for (int i = 0; i < connectedPoints.size(); i++) {
			Point p = connectedPoints.get(i);
			neighbors(p);
		}
	}

	private void neighbors(Point p) {
		for (int[] shift : neighborhoodShifts) {
			Point neighbor = new Point();
			neighbor.x = p.x + shift[0];
			neighbor.y = p.y + shift[1];

			if (neighbor.x >= 0 && neighbor.x < width && neighbor.y >= 0 && neighbor.y < height
					&& passedPixels[neighbor.y][neighbor.x] == 0) {
				
				connectedPoints.add(neighbor);
				passedPixels[neighbor.y][neighbor.x] = label;
			}
		}
	}

}