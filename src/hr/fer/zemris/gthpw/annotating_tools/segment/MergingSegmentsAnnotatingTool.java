package hr.fer.zemris.gthpw.annotating_tools.segment;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.function.Consumer;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.AnnotatingToolsUtil;
import hr.fer.zemris.gthpw.annotating_tools.RectangleAnnotatingTool;

public class MergingSegmentsAnnotatingTool extends RectangleAnnotatingTool {

	private static final long serialVersionUID = 1L;

	public MergingSegmentsAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model) {
		this(name, tool, model, Color.DARK_GRAY);
	}

	public MergingSegmentsAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model, Color drawRectColor) {
		super(name, tool, model, new Consumer<Rectangle>() {

			@Override
			public void accept(Rectangle boundingBox) {

				Collection<ImageSegment> segments = model.getSegments();
				Collection<ImageSegment> segmentsToBeMerged = AnnotatingToolsUtil.getSegmentsInsideBB(segments, boundingBox);
				
				if(segmentsToBeMerged.isEmpty()) {
					return;
				}
				
				ImageSegment newSegment = ImageSegment.mergeImageSegments(segmentsToBeMerged);
				
				model.removeSegments(segmentsToBeMerged);
				model.addSegment(newSegment);
			}
		}, drawRectColor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		model.showSegments(true);
	}
	
}
