package hr.fer.zemris.gthpw.separators;

import java.util.Collection;
import java.util.List;

import hr.fer.zemris.gthpw.ImageSegment;

public interface TextLineSeparator {

	List<List<ImageSegment>> separate(Collection<ImageSegment> segments, int numberOfLines);
	
}