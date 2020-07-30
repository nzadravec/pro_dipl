package hr.fer.zemris.gthpw.separators;

import java.util.List;

import hr.fer.zemris.gthpw.ImageSegment;

public interface WordSeparator {

	List<List<ImageSegment>> separate(List<ImageSegment> textLine, int numberOfWords);
	
}
