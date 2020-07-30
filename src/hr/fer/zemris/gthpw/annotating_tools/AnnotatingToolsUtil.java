package hr.fer.zemris.gthpw.annotating_tools;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

import hr.fer.zemris.gthpw.ImageSegment;

public class AnnotatingToolsUtil {

	public static Collection<ImageSegment> getSegmentsInsideBB(Collection<ImageSegment> segments, Rectangle boundingBox) {
		Collection<ImageSegment> segmentsInsideBB = new ArrayList<>();
		for (ImageSegment r : segments) {
			Rectangle bb = r.getMinBoundingBox();
			if (boundingBox.contains(bb)) {
				segmentsInsideBB.add(r);
			}
		}
		return segmentsInsideBB;
	}
	
}
