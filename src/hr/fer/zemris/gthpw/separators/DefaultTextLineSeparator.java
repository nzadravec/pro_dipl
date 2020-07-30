package hr.fer.zemris.gthpw.separators;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.comparators.SegmentsXCoordComparator;
import hr.fer.zemris.gthpw.util.Pair;

public class DefaultTextLineSeparator implements TextLineSeparator {
	
	private Comparator<ImageSegment> segmentsYCoordComparator = new Comparator<ImageSegment>() {
		
		@Override
		public int compare(ImageSegment o1, ImageSegment o2) {
			return Integer.compare(o1.getMinBoundingBox().y, o2.getMinBoundingBox().y);
		}
	};
	
	@Override
	public List<List<ImageSegment>> separate(Collection<ImageSegment> segments, int numberOfLines) {
		if(segments.isEmpty()) {
			return new ArrayList<>();
		}
		
		boolean unknownNumOfLines = numberOfLines == -1 || segments.size() < numberOfLines;
		
		List<ImageSegment> segmentList = new ArrayList<>(segments);
		segmentList.sort(new SegmentsXCoordComparator().reversed());
		
		List<Pair<ImageSegment, Boolean>> fstInLineAndSndInLineSeenPairs = new ArrayList<>();
		fstInLineAndSndInLineSeenPairs.add(new Pair<>(segmentList.remove(segmentList.size() - 1), false));

		int sndSegmentInLineSeenCounter = 0;
		Outer: for (int i = segmentList.size() - 1; i >= 0; i--) {
			Rectangle bb1 = segmentList.get(i).getMinBoundingBox();
			for (Pair<ImageSegment, Boolean> p : fstInLineAndSndInLineSeenPairs) {
				Rectangle bb2 = p.getFirst().getMinBoundingBox();
				if (!((bb1.y + bb1.getHeight()) < bb2.y || (bb2.y + bb2.getHeight()) < bb1.y)) {
					if(unknownNumOfLines && !p.getSecond()) {
						p.setSecond(true);
						sndSegmentInLineSeenCounter++;
						if(sndSegmentInLineSeenCounter == fstInLineAndSndInLineSeenPairs.size()) {
							break Outer;
						}
					}
					continue Outer;
				}
			}

			fstInLineAndSndInLineSeenPairs.add(new Pair<>(segmentList.remove(i), false));		
			if (fstInLineAndSndInLineSeenPairs.size() == numberOfLines) {
				break;
			}
		}
		
		List<ImageSegment> fstSegmentInLine = fstInLineAndSndInLineSeenPairs.stream().map(x -> x.getFirst()).collect(Collectors.toList());
		fstSegmentInLine.sort(segmentsYCoordComparator);
		
		List<List<ImageSegment>> textLines = new ArrayList<>();
		for (ImageSegment r : fstSegmentInLine) {
			List<ImageSegment> textLine = new ArrayList<>();
			textLine.add(r);
			textLines.add(textLine);
		}

		for (int i = segmentList.size() - 1; i >= 0; i--) {
			Rectangle bb1 = segmentList.get(i).getMinBoundingBox();
			double minYDistance = Double.MAX_VALUE;
			List<ImageSegment> closestTextLine = null;
			for (List<ImageSegment> textLine : textLines) {
				ImageSegment lastTextLineSegment = textLine.get(textLine.size() - 1);
				Rectangle bb2 = lastTextLineSegment.getMinBoundingBox();
				double yDistance = Math.abs(bb1.getCenterY() - bb2.getCenterY());
				if (closestTextLine == null || yDistance < minYDistance) {
					minYDistance = yDistance;
					closestTextLine = textLine;
				}
			}

			closestTextLine.add(segmentList.remove(i));
		}

		return textLines;
	}

}
