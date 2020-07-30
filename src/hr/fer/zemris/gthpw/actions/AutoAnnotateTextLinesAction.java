package hr.fer.zemris.gthpw.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.separators.DefaultTextLineSeparator;
import hr.fer.zemris.gthpw.separators.TextLineSeparator;
import hr.fer.zemris.gthpw.transcript.Transcript;

public class AutoAnnotateTextLinesAction extends AutoAnnotateAction {

	private static final long serialVersionUID = 1L;

	private TextLineSeparator textLineSeparator = new DefaultTextLineSeparator();
	
	public AutoAnnotateTextLinesAction(String name, AnnotatingDocImageModel model) {
		super(name, model);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Collection<ImageSegment> segments = model.getSegments();
		
		Transcript transcript = model.getDocImage().getTranscript();
		int numberOfLines;
		if(transcript == null) {
			numberOfLines = -1;
		} else {
			numberOfLines = transcript.numberOfLines();
		}
		
		List<List<ImageSegment>> textLines = textLineSeparator.separate(segments, numberOfLines);
		textLines.forEach(textLine -> model.addTextLine(textLine));
		//model.addTextLines(textLines);
		
		setEnabled(false);
		model.showTextLines(true);
	}

}
