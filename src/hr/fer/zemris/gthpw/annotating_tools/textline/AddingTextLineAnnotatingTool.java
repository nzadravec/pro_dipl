package hr.fer.zemris.gthpw.annotating_tools.textline;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.AnnotatingToolsUtil;
import hr.fer.zemris.gthpw.annotating_tools.RectangleAnnotatingTool;
import hr.fer.zemris.gthpw.comparators.SegmentsXCoordComparator;

public class AddingTextLineAnnotatingTool extends RectangleAnnotatingTool {

	private static final long serialVersionUID = 1L;

	public AddingTextLineAnnotatingTool(String name, ActivatedAnnotatingTool activatedAnnotatingTool,
			AnnotatingDocImageModel annotatingDocImageModel) {
		this(name, activatedAnnotatingTool, annotatingDocImageModel, Color.DARK_GRAY);
	}

	public AddingTextLineAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model, Color drawRectColor) {
		super(name, tool, model, new Consumer<Rectangle>() {

			private Comparator<ImageSegment> segmentsInLineComparator = new SegmentsXCoordComparator();
			
			@Override
			public void accept(Rectangle boundingBox) {
				
				Collection<ImageSegment> segments = model.getSegments();
				Collection<ImageSegment> segmentsInsideBB = AnnotatingToolsUtil.getSegmentsInsideBB(segments, boundingBox);
				
				List<ImageSegment> textLine = new ArrayList<>(segmentsInsideBB);
				textLine.sort(segmentsInLineComparator);
				
				model.addTextLine(textLine);
			}
		}, drawRectColor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		model.showTextLines(true);
	}

}
