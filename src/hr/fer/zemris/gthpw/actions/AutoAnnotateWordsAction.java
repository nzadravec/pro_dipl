package hr.fer.zemris.gthpw.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.separators.DefaultWordSeparator;
import hr.fer.zemris.gthpw.separators.WordSeparator;
import hr.fer.zemris.gthpw.transcript.Transcript;

public class AutoAnnotateWordsAction extends AutoAnnotateAction {

	private static final long serialVersionUID = 1L;

	private WordSeparator wordSeparator = new DefaultWordSeparator();

	public AutoAnnotateWordsAction(String name, AnnotatingDocImageModel model) {
		super(name, model);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Transcript transcript = model.getDocImage().getTranscript();
		
		for(int i = 0; i < model.numberOfTextLines(); i++) {
			List<ImageSegment> textLine = model.getTextLine(i);
			
			int numberOfWords;
			if (transcript != null && i < transcript.numberOfLines()) {
				numberOfWords = transcript.getWordsAtLine(i).length;
			} else {
				numberOfWords = -1;
			}
			
			List<List<ImageSegment>> words = wordSeparator.separate(textLine, numberOfWords);
			model.addWords(words, i);
		}
		
		setEnabled(false);
		model.showWords(true);
	}

}
