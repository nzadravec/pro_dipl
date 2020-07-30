package hr.fer.zemris.gthpw.annotating_tools.textline;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.AnnotatingToolsUtil;
import hr.fer.zemris.gthpw.annotating_tools.RectangleAnnotatingTool;
import hr.fer.zemris.gthpw.separators.DefaultTextLineSeparator;
import hr.fer.zemris.gthpw.separators.TextLineSeparator;
import hr.fer.zemris.gthpw.transcript.Transcript;

public class TextLinesAutoAnnotatingTool extends RectangleAnnotatingTool {

	private static final long serialVersionUID = 1L;

	public TextLinesAutoAnnotatingTool(String name, ActivatedAnnotatingTool activatedAnnotatingTool,
			AnnotatingDocImageModel annotatingDocImageModel) {
		this(name, activatedAnnotatingTool, annotatingDocImageModel, Color.DARK_GRAY);
	}

	public TextLinesAutoAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model, Color drawRectColor) {
		super(name, tool, model, new Consumer<Rectangle>() {
			
			private TextLineSeparator textLineSeparator = new DefaultTextLineSeparator();
			
			@Override
			public void accept(Rectangle boundingBox) {
				
				Collection<ImageSegment> segments = model.getSegments();
				Collection<ImageSegment> segmentsInsideBB = AnnotatingToolsUtil.getSegmentsInsideBB(segments, boundingBox);
				
				Transcript transcript = model.getDocImage().getTranscript();
				int numberOfLines;
				if(transcript == null) {
					numberOfLines = -1;
				} else {
					numberOfLines = transcript.numberOfLines();
				}
				
				List<List<ImageSegment>> textLines = textLineSeparator.separate(segmentsInsideBB, numberOfLines);
				textLines.forEach(textLine -> model.addTextLine(textLine));
			}
		}, drawRectColor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		model.showTextLines(true);
	}

}
