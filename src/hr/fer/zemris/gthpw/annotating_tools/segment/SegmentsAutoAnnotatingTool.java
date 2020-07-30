package hr.fer.zemris.gthpw.annotating_tools.segment;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.RectangleAnnotatingTool;
import hr.fer.zemris.gthpw.image.algorithms.Neighborhood;
import hr.fer.zemris.gthpw.image.algorithms.RecursiveCCsLabelingAlgorithm;
import hr.fer.zemris.gthpw.image.binary.BinaryImage;

public class SegmentsAutoAnnotatingTool extends RectangleAnnotatingTool {

	private static final long serialVersionUID = 1L;

	public SegmentsAutoAnnotatingTool(String name, ActivatedAnnotatingTool activatedAnnotatingTool,
			AnnotatingDocImageModel annotatingDocImageModel) {
		this(name, activatedAnnotatingTool, annotatingDocImageModel, Color.DARK_GRAY);
	}

	public SegmentsAutoAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model, Color drawRectColor) {
		super(name, tool, model, new Consumer<Rectangle>() {

			private RecursiveCCsLabelingAlgorithm ccsLabelingAlg = new RecursiveCCsLabelingAlgorithm(false, Neighborhood.EIGHT);
			
			@Override
			public void accept(Rectangle boundingBox) {
				BinaryImage binaryImage = model.getDocImage().getBinaryImage();
				
				Collection<ImageSegment> ccs = ccsLabelingAlg.findConnectedComponents(binaryImage, boundingBox);
				
				List<ImageSegment> ccsList = new ArrayList<>(ccs);

				Collection<ImageSegment> isColl = new ArrayList<>();
				for (int i = 0; i < ccsList.size(); i++) {
					ImageSegment cc = ccsList.get(i);
					Rectangle bb = cc.getMinBoundingBox();

					isColl.clear();
					isColl.add(cc);
					for (int j = ccsList.size() - 1; j > i; j--) {
						ImageSegment cc2 = ccsList.get(j);
						Rectangle bb2 = cc2.getMinBoundingBox();
						if (bb.contains(bb2)) {
							isColl.add(cc2);
							ccsList.remove(j);
						}
					}
					
					ccsList.remove(i);
					ccsList.add(i, ImageSegment.mergeImageSegments(isColl));
				}

				int bbsAreaSum = 0;
				for (ImageSegment cc : ccsList) {
					Rectangle bb = cc.getMinBoundingBox();
					bbsAreaSum += bb.height * bb.width;
				}

				double avgBBArea = bbsAreaSum / (double) ccsList.size();
				double bbAreaThreshold = 0.05 * avgBBArea;
				for (int i = ccsList.size() - 1; i >= 0; i--) {
					ImageSegment cc = ccsList.get(i);
					Rectangle bb = cc.getMinBoundingBox();
					if (bbAreaThreshold > bb.height * bb.width) {
						ccsList.remove(i);
					}
				}
				
				for(ImageSegment cc : ccsList) {
					model.addSegment(cc);
				}
			}
		}, drawRectColor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		model.showSegments(true);
	}

}
