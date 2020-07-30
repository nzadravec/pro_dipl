package hr.fer.zemris.gthpw.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.image.algorithms.Neighborhood;
import hr.fer.zemris.gthpw.image.algorithms.RecursiveCCsLabelingAlgorithm;
import hr.fer.zemris.gthpw.image.binary.BinaryImage;

public class AutoAnnotateSegmentsAction extends AutoAnnotateAction {

	private static final long serialVersionUID = 1L;
	
	private RecursiveCCsLabelingAlgorithm ccsLabelingAlg;
	
	{
		ccsLabelingAlg = new RecursiveCCsLabelingAlgorithm(false, Neighborhood.EIGHT);
	}

	public AutoAnnotateSegmentsAction(String name, AnnotatingDocImageModel model) {
		super(name, model);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		BinaryImage binaryImage = model.getDocImage().getBinaryImage();
		int width = binaryImage.getWidth();
		int height = binaryImage.getHeight();
		Rectangle boundingBox = new Rectangle(0, 0, width, height);
		
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
		
		setEnabled(false);
		model.showSegments(true);
	}

}
