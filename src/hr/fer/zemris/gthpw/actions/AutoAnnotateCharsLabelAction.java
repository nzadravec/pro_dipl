package hr.fer.zemris.gthpw.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.transcript.Transcript;

public class AutoAnnotateCharsLabelAction extends AutoAnnotateAction {

	private static final long serialVersionUID = 1L;

	public AutoAnnotateCharsLabelAction(String name, AnnotatingDocImageModel model) {
		super(name, model);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		Transcript transcript = model.getDocImage().getTranscript();
		if(transcript == null) {
			return;
		}
		
		for(int lineIndex = 0; lineIndex < model.numberOfTextLines(); lineIndex++) {
			
			if(lineIndex >= transcript.numberOfLines()) {
				break;
			}
			
			String[] words = transcript.getWordsAtLine(lineIndex);
			
			for(int wordIndex = 0; wordIndex < model.numberOfWordsInLine(lineIndex); wordIndex++) {
				
				List<ImageSegment> lineWord = model.getWordInLine(wordIndex, lineIndex);
				if(wordIndex >= words.length) {
					break;
				}
				String word = words[wordIndex];
				
				for(int charIndex = 0; charIndex < lineWord.size(); charIndex++) {
					if(charIndex >= word.length()) {
						break;
					}
					
					model.setSegmentLabel(lineWord.get(charIndex), word.charAt(charIndex));
				}
				
			}
		}
		
		model.showCharsLabel(true);
	}

}
