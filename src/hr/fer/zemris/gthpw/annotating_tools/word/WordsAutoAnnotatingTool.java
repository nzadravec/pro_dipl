package hr.fer.zemris.gthpw.annotating_tools.word;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.AbstractAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.separators.DefaultWordSeparator;
import hr.fer.zemris.gthpw.separators.WordSeparator;
import hr.fer.zemris.gthpw.transcript.Transcript;

public class WordsAutoAnnotatingTool extends AbstractAnnotatingTool {

	private static final long serialVersionUID = 1L;
	
	private WordSeparator wordSeparator = new DefaultWordSeparator();

	public WordsAutoAnnotatingTool(String name, ActivatedAnnotatingTool tool, AnnotatingDocImageModel model) {
		super(name, tool, model);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		boolean flag = false;
		int clickedLineIndex = 0;
		Outer: for (int lineIndex = 0; lineIndex < model.numberOfTextLines(); lineIndex++) {
			List<ImageSegment> textLine = model.getTextLine(lineIndex);
			for (int index = 0; index < textLine.size(); index++) {
				Rectangle bb = textLine.get(index).getMinBoundingBox();
				if (bb.contains(p)) {
					flag = true;
					clickedLineIndex = lineIndex;
					break Outer;
				}
			}
		}
		
		if(!flag) {
			return;
		}
		
		Transcript transcript = model.getDocImage().getTranscript();
		List<ImageSegment> textLine = model.getTextLine(clickedLineIndex);
		
		int numberOfWords;
		if (transcript != null && clickedLineIndex < transcript.numberOfLines()) {
			numberOfWords = transcript.getWordsAtLine(clickedLineIndex).length;
		} else {
			numberOfWords = -1;
		}
		
		List<List<ImageSegment>> words = wordSeparator.separate(textLine, numberOfWords);
		model.addWords(words, clickedLineIndex);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paint(Graphics2D g2d) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		model.showWords(true);
	}

}
