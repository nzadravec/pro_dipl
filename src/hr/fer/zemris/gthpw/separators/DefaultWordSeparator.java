package hr.fer.zemris.gthpw.separators;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.image.algorithms.OtsuThresholder;

public class DefaultWordSeparator implements WordSeparator {

	@Override
	public List<List<ImageSegment>> separate(List<ImageSegment> textLine, int numberOfWords) {
		if(textLine.isEmpty()) {
			return new ArrayList<>();
		}
		
		boolean unknownNumOfWords = numberOfWords == -1 || textLine.size() < numberOfWords;

		int[] adjacentBBSegmentCenterXDistances = new int[textLine.size() - 1];
		int maxDistance = -1;
		for (int i = 1; i < textLine.size(); i++) {
			Rectangle bb1 = textLine.get(i - 1).getMinBoundingBox();
			Rectangle bb2 = textLine.get(i).getMinBoundingBox();
			
			int distance = Math.max((int)(bb2.x - (bb1.x + bb1.width)), 0);
			adjacentBBSegmentCenterXDistances[i - 1] = distance;
			if(distance > maxDistance) {
				maxDistance = distance;
			}
		}
		
		List<List<ImageSegment>> words = new ArrayList<>();
		int threshold;
		if(unknownNumOfWords) {
			threshold = OtsuThresholder.getThreshold(adjacentBBSegmentCenterXDistances, maxDistance + 1);
		} else {
			int[] copy = Arrays.copyOf(adjacentBBSegmentCenterXDistances, adjacentBBSegmentCenterXDistances.length);
			Arrays.sort(copy);
			threshold = copy[copy.length-(numberOfWords-1)];
		}
		
		List<ImageSegment> word = new ArrayList<>();
		word.add(textLine.get(0));
		for (int i = 1; i < textLine.size(); i++) {
			if(adjacentBBSegmentCenterXDistances[i - 1] >= threshold) {
				words.add(word);
				word = new ArrayList<>();
			}
			word.add(textLine.get(i));
		}
		words.add(word);

		return words;
	}

}
