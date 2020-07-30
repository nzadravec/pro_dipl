package hr.fer.zemris.gthpw;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import hr.fer.zemris.gthpw.image.ImageUtil;
import hr.fer.zemris.gthpw.image.algorithms.Grayscale;
import hr.fer.zemris.gthpw.image.binary.BinaryImage;
import hr.fer.zemris.gthpw.image.binary.BinaryImageUtil;
import hr.fer.zemris.gthpw.image.morohology.Erosion;
import hr.fer.zemris.gthpw.image.morohology.MorphologicalOperation;
import hr.fer.zemris.gthpw.transcript.Transcript;
import hr.fer.zemris.gthpw.transcript.TranscriptUtil;
import hr.fer.zemris.gthpw.util.Pair;

public class DefaultAnnotatingDocImageModel implements AnnotatingDocImageModel {

	private DocImage docImage;

	private boolean showSegmentsFlag;
	private boolean showTextLinesFlag;
	private boolean showWordsFlag;
	private boolean showCharsLabelFlag;
	private boolean showTranscriptWordsFlag;

	private Set<ImageSegment> segmentSet = new HashSet<>();
	private Map<ImageSegment, Character> segmentToLabelMap = new HashMap<>();

	private List<Pair<List<ImageSegment>, List<List<ImageSegment>>>> lineAndWordsList = new ArrayList<>();

	private Comparator<ImageSegment> segmentsComparator = new Comparator<ImageSegment>() {

		@Override
		public int compare(ImageSegment o1, ImageSegment o2) {
			return Integer.compare(o1.getMinBoundingBox().x, o2.getMinBoundingBox().x);
		}
	};

	private Comparator<Pair<List<ImageSegment>, List<List<ImageSegment>>>> textLinesComparator = new Comparator<Pair<List<ImageSegment>, List<List<ImageSegment>>>>() {

		@Override
		public int compare(Pair<List<ImageSegment>, List<List<ImageSegment>>> o1,
				Pair<List<ImageSegment>, List<List<ImageSegment>>> o2) {
			return Integer.compare(o1.getFirst().get(0).getMinBoundingBox().y,
					o2.getFirst().get(0).getMinBoundingBox().y);
		}
	};

	private Comparator<List<ImageSegment>> wordsInLineComparator = new Comparator<List<ImageSegment>>() {

		@Override
		public int compare(List<ImageSegment> o1, List<ImageSegment> o2) {
			return Integer.compare(o1.get(0).getMinBoundingBox().x, o2.get(0).getMinBoundingBox().x);
		}
	};

	private List<AnnotatingDocImageModelListener> listeners = new ArrayList<>();

	@Override
	public DocImage loadDocImage(Path path) {
		BufferedImage image;
		try {
			image = ImageIO.read(path.toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		BufferedImage grayscaleImage = Grayscale.grayscaleImage(image);
		MorphologicalOperation erosion = new Erosion();
		grayscaleImage = erosion.execute(grayscaleImage);

		BufferedImage thresholdedImage = ImageUtil.createThresholdedImage(grayscaleImage);
		BinaryImage binaryImage = BinaryImageUtil.createBoolArrayBinaryImage(thresholdedImage);

		String imageFileString = path.toString();
		String transcriptFileString = imageFileString.substring(0, imageFileString.lastIndexOf('.') + 1) + "txt";
		Transcript transcript = null;
		try {
			transcript = TranscriptUtil.loadTranscript(transcriptFileString);
		} catch (IOException e) {
		}

		docImage = new DocImageImpl(path, image, binaryImage, transcript);
		resetModel();
		fireDocImageLoaded();

		return docImage;
	}

	private void resetModel() {
		showSegmentsFlag = false;
		showTextLinesFlag = false;
		showWordsFlag = false;
		showCharsLabelFlag = false;
		showTranscriptWordsFlag = false;

		segmentSet.clear();
		lineAndWordsList.clear();
		segmentToLabelMap.clear();

		fireStateChanged();
	}

	@Override
	public void setTranscript(Transcript transcript) {
		docImage = new DocImageImpl(docImage.getFilePath(), docImage.getImage(), docImage.getBinaryImage(), transcript);

		fireStateChanged();
	}

	private void fireDocImageLoaded() {
		for (AnnotatingDocImageModelListener l : listeners) {
			l.docImageLoaded(new ChangeEvent(this));
		}
	}

	@Override
	public DocImage getDocImage() {
		return docImage;
	}

	@Override
	public boolean showSegments() {
		return showSegmentsFlag;
	}

	@Override
	public void showSegments(boolean value) {
		if (showSegmentsFlag != value) {
			showSegmentsFlag = value;
			if (!showSegmentsFlag) {
				showCharsLabel(false);
			}
			fireStateChanged();
		}
	}

	@Override
	public boolean showTextLines() {
		return showTextLinesFlag;
	}

	@Override
	public void showTextLines(boolean value) {
		if (showTextLinesFlag != value) {
			showTextLinesFlag = value;
			fireStateChanged();
		}
	}

	@Override
	public boolean showWords() {
		return showWordsFlag;
	}

	@Override
	public void showWords(boolean value) {
		if (showWordsFlag != value) {
			showWordsFlag = value;
			if (!showWordsFlag) {
				showTranscriptWords(false);
			}
			fireStateChanged();
		}
	}

	@Override
	public boolean showCharsLabel() {
		return showCharsLabelFlag;
	}

	@Override
	public void showCharsLabel(boolean value) {
		if (showCharsLabelFlag != value) {
			showCharsLabelFlag = value;
			fireStateChanged();
		}
	}

	@Override
	public boolean showTranscriptWords() {
		return showTranscriptWordsFlag;
	}

	@Override
	public void showTranscriptWords(boolean value) {
		if (showTranscriptWordsFlag != value) {
			showTranscriptWordsFlag = value;
			fireStateChanged();
		}
	}

	private void fireStateChanged() {
		for (ChangeListener l : listeners) {
			l.stateChanged(new ChangeEvent(this));
		}
	}

	@Override
	public void addSegment(ImageSegment segment) {
		if (!segmentSet.add(segment)) {
			return;
		}

		if (!lineAndWordsList.isEmpty()) {

			double x1 = segment.getMinBoundingBox().getCenterX();
			double y1 = segment.getMinBoundingBox().getCenterY();

			Pair<List<ImageSegment>, List<List<ImageSegment>>> closestTL = null;
			double minDistance = Double.MAX_VALUE;
			for (Pair<List<ImageSegment>, List<List<ImageSegment>>> p : lineAndWordsList) {
				double tmp = Double.MAX_VALUE;
				for (ImageSegment s : p.getFirst()) {
					double x2 = s.getMinBoundingBox().getCenterX();
					double y2 = s.getMinBoundingBox().getCenterY();
					double dist = Point.distance(x1, y1, x2, y2);
					tmp = Math.min(tmp, dist);
				}

				if (closestTL == null || tmp < minDistance) {
					closestTL = p;
					minDistance = tmp;
				}
			}

			closestTL.getFirst().add(segment);
			closestTL.getFirst().sort(segmentsComparator);

			if (!closestTL.getSecond().isEmpty()) {

				List<ImageSegment> closestWord = null;
				double minDistance2 = Double.MAX_VALUE;
				for (List<ImageSegment> word : closestTL.getSecond()) {
					double tmp = Double.MAX_VALUE;
					for (ImageSegment s : word) {
						double x2 = s.getMinBoundingBox().getCenterX();
						double y2 = s.getMinBoundingBox().getCenterY();
						double dist = Point.distance(x1, y1, x2, y2);
						tmp = Math.min(tmp, dist);
					}

					if (closestWord == null || tmp < minDistance2) {
						closestWord = word;
						minDistance2 = tmp;
					}
				}

				closestWord.add(segment);
				closestWord.sort(segmentsComparator);
			}

		}

		fireStateChanged();
	}

	@Override
	public void addSegments(Collection<ImageSegment> segments) {
		if (!segmentSet.addAll(segments)) {
			return;
		}

		if (!lineAndWordsList.isEmpty()) {

			// TODO

		}

		fireStateChanged();
	}

	@Override
	public void removeSegment(ImageSegment segment) {
		if (!segmentSet.remove(segment)) {
			return;
		}

		segmentToLabelMap.remove(segment);

		lineAndWordsList.forEach(p -> p.getFirst().remove(segment));
		lineAndWordsList.removeIf(p -> p.getFirst().size() <= 1);

		lineAndWordsList.forEach(p -> p.getSecond().forEach(w -> w.remove(segment)));
		lineAndWordsList.forEach(p -> p.getSecond().removeIf(w -> w.isEmpty()));

		fireStateChanged();
	}

	@Override
	public void removeSegments(Collection<ImageSegment> segments) {
		if (!segmentSet.removeAll(segments)) {
			return;
		}

		segments.forEach(s -> segmentToLabelMap.remove(s));

		lineAndWordsList.forEach(p -> p.getFirst().removeAll(segments));
		lineAndWordsList.removeIf(p -> p.getFirst().size() <= 1);

		lineAndWordsList.forEach(p -> p.getSecond().forEach(w -> w.removeAll(segments)));
		lineAndWordsList.forEach(p -> p.getSecond().removeIf(w -> w.isEmpty()));

		fireStateChanged();
	}

	@Override
	public Set<ImageSegment> getSegments() {
		return Collections.unmodifiableSet(segmentSet);
	}

	@Override
	public void setSegmentLabel(ImageSegment segment, Character label) {
		segmentToLabelMap.put(segment, label);
		fireStateChanged();
	}

	@Override
	public Character getSegmentLabel(ImageSegment segment) {
		return segmentToLabelMap.get(segment);
	}

	@Override
	public void addTextLine(List<ImageSegment> textLine) {
		if (textLine.size() <= 1) {
			return;
		}

		lineAndWordsList.forEach(p -> p.getFirst().removeAll(textLine));
		lineAndWordsList.removeIf(p -> p.getFirst().size() <= 1);

		lineAndWordsList.forEach(p -> p.getSecond().forEach(w -> w.removeAll(textLine)));
		lineAndWordsList.forEach(p -> p.getSecond().removeIf(w -> w.isEmpty()));

		lineAndWordsList.add(new Pair<>(new ArrayList<>(textLine), new ArrayList<>()));
		lineAndWordsList.sort(textLinesComparator);

		fireStateChanged();
	}

	@Override
	public void addTextLines(Collection<List<ImageSegment>> textLines) {
		textLines.removeIf(tl -> tl.size() <= 1);
		if (textLines.isEmpty()) {
			return;
		}

		// TODO

		fireStateChanged();
	}

	@Override
	public int numberOfTextLines() {
		return lineAndWordsList.size();
	}

	@Override
	public List<ImageSegment> getTextLine(int index) {
		return Collections.unmodifiableList(lineAndWordsList.get(index).getFirst());
	}

	@Override
	public void addWord(List<ImageSegment> word, int lineIndex) {
		if (word.isEmpty()) {
			return;
		}

		List<List<ImageSegment>> words = lineAndWordsList.get(lineIndex).getSecond();

		words.forEach(w -> w.removeAll(word));
		words.removeIf(w -> w.isEmpty());

		words.add(new ArrayList<>(word));
		words.sort(wordsInLineComparator);

		fireStateChanged();
	}

	@Override
	public void addWords(Collection<List<ImageSegment>> words, int lineIndex) {
		words.removeIf(w -> w.isEmpty());
		if (words.isEmpty()) {
			return;
		}

		List<List<ImageSegment>> lineWords = lineAndWordsList.get(lineIndex).getSecond();

		words.forEach(word -> lineWords.forEach(w -> w.removeAll(word)));
		lineWords.removeIf(w -> w.isEmpty());

		words.forEach(word -> lineWords.add(new ArrayList<>(word)));
		lineWords.sort(wordsInLineComparator);

		fireStateChanged();
	}

	@Override
	public int numberOfWordsInLine(int lineIndex) {
		return lineAndWordsList.get(lineIndex).getSecond().size();
	}

	@Override
	public List<ImageSegment> getWordInLine(int wordIndex, int lineIndex) {
		return Collections.unmodifiableList(lineAndWordsList.get(lineIndex).getSecond().get(wordIndex));
	}

	@Override
	public void addListener(AnnotatingDocImageModelListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.add(l);
	}

	@Override
	public void removeListener(AnnotatingDocImageModelListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.remove(l);
	}

}
