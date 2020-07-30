package hr.fer.zemris.gthpw;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import hr.fer.zemris.gthpw.transcript.Transcript;

public interface AnnotatingDocImageModel {
	
	DocImage loadDocImage(Path path) throws IOException;
	DocImage getDocImage();
	public void setTranscript(Transcript transcript);
	
	boolean showSegments();
	void showSegments(boolean value);
	boolean showTextLines();
	void showTextLines(boolean value);
	boolean showWords();
	void showWords(boolean value);
	boolean showCharsLabel();
	void showCharsLabel(boolean value);
	boolean showTranscriptWords();
	void showTranscriptWords(boolean value);
	
	void addSegment(ImageSegment segment);
	void addSegments(Collection<ImageSegment> segments);
	void removeSegment(ImageSegment segment);
	void removeSegments(Collection<ImageSegment> segments);
	Collection<ImageSegment> getSegments();
	void setSegmentLabel(ImageSegment segment, Character label);
	Character getSegmentLabel(ImageSegment segment);
	
	void addTextLine(List<ImageSegment> textLine);
	void addTextLines(Collection<List<ImageSegment>> textLines);
	int numberOfTextLines();
	List<ImageSegment> getTextLine(int index);
	
	void addWord(List<ImageSegment> word, int lineIndex);
	void addWords(Collection<List<ImageSegment>> words, int lineIndex);
	int numberOfWordsInLine(int lineIndex);
	List<ImageSegment> getWordInLine(int wordIndex, int lineIndex);
	
	void addListener(AnnotatingDocImageModelListener l);
	void removeListener(AnnotatingDocImageModelListener l);
	
}