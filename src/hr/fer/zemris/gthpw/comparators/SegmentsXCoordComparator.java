package hr.fer.zemris.gthpw.comparators;

import java.util.Comparator;

import hr.fer.zemris.gthpw.ImageSegment;

public class SegmentsXCoordComparator implements Comparator<ImageSegment> {

	@Override
	public int compare(ImageSegment o1, ImageSegment o2) {
		return Integer.compare(o1.getMinBoundingBox().x, o2.getMinBoundingBox().x);
	}
	
}
