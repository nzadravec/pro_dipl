package hr.fer.zemris.gthpw.annotating_tools.segment;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.function.Consumer;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.RectangleAnnotatingTool;
import hr.fer.zemris.gthpw.image.algorithms.Neighborhood;
import hr.fer.zemris.gthpw.image.algorithms.RecursiveCCsLabelingAlgorithm;
import hr.fer.zemris.gthpw.image.binary.BinaryImage;

public class AddingSegmentAnnotatingTool extends RectangleAnnotatingTool {

	private static final long serialVersionUID = 1L;

	public AddingSegmentAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model) {
		this(name, tool, model, Color.DARK_GRAY);
	}
	
	static int count;

	public AddingSegmentAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model, Color drawRectColor) {
		super(name, tool, model, new Consumer<Rectangle>() {

			private RecursiveCCsLabelingAlgorithm ccsLabelingAlg = new RecursiveCCsLabelingAlgorithm(false, Neighborhood.EIGHT);
			
			@Override
			public void accept(Rectangle boundingBox) {
				
				BinaryImage binaryImage = model.getDocImage().getBinaryImage();
				Collection<ImageSegment> segments = ccsLabelingAlg.findConnectedComponents(binaryImage, boundingBox);
				if(segments.isEmpty()) {
					return;
				}
				
				double biggestSegmentBBArea = -1;
				ImageSegment biggestSegment = null;
				for(ImageSegment segment : segments) {
					Rectangle bb = segment.getMinBoundingBox();
					double segmentBBArea = bb.width * bb.height;
					if(biggestSegment == null || segmentBBArea > biggestSegmentBBArea) {
						biggestSegment = segment;
						biggestSegmentBBArea = segmentBBArea;
					}
				}
				
				model.addSegment(biggestSegment);
			}
		}, drawRectColor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		model.showSegments(true);
	}

}
