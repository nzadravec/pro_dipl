package hr.fer.zemris.gthpw.annotating_tools.segment;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.LineAnnotatingTool;
import hr.fer.zemris.gthpw.util.Bresenham;
import hr.fer.zemris.gthpw.annotating_tools.Line;

public class SplittingSegmentAnnotatingTool extends LineAnnotatingTool {

	private static final long serialVersionUID = 1L;
	
	public SplittingSegmentAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model) {
		this(name, tool, model, Color.DARK_GRAY);
	}

	public SplittingSegmentAnnotatingTool(String name, ActivatedAnnotatingTool activatedAnnotatingTool,
			AnnotatingDocImageModel annotatingDocImageModel, Color drawRectColor) {
		super(name, activatedAnnotatingTool, annotatingDocImageModel, new Consumer<Line>() {

			private List<Point> linePoints = new ArrayList<>();
			private Bresenham bresenham = new Bresenham(new Consumer<Point>() {
				
				@Override
				public void accept(Point p) {
					linePoints.add(p);
				}
			});
			
			@Override
			public void accept(Line line) {
				linePoints.clear();
				bresenham.generateLinePoints(new Point(line.start), new Point(line.end));
				
				Collection<ImageSegment> segments = annotatingDocImageModel.getSegments();
				ImageSegment segmentToBeSplit = null;
				long maxCount = -1;
				for(ImageSegment segment : segments) {
					Rectangle bb = segment.getMinBoundingBox();
					long count = linePoints.stream().filter(p -> bb.contains(p)).count();
					if(segmentToBeSplit == null || count > maxCount) {
						segmentToBeSplit = segment;
						maxCount = count;
					}
				}
				
				if(maxCount <= 0) {
					return;
				}
				
				Point p1 = line.start;
				Point p2 = line.end;
				
				double a = p1.y - p2.y;
				double b = -(p1.x - p2.x);
				double c = -a * p1.x - b * p1.y;
				
				Set<Point> pixels = segmentToBeSplit.getPixels();
				Collection<Point> pointsAboveLine = new ArrayList<>();
				Collection<Point> pointsBelowLine = new ArrayList<>();
				for(Point p : pixels) {
					double tmp = p.x * a + p.y * b + c;
					if(tmp > 0) {
						pointsAboveLine.add(p);
					} else if(tmp < 0) {
						pointsBelowLine.add(p);
					}
				}
				
				if(pointsAboveLine.isEmpty() || pointsBelowLine.isEmpty()) {
					return;
				}
				
				annotatingDocImageModel.removeSegment(segmentToBeSplit);
				annotatingDocImageModel.addSegment(new ImageSegment(pointsAboveLine));
				annotatingDocImageModel.addSegment(new ImageSegment(pointsBelowLine));
			}
			
		}, drawRectColor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		model.showSegments(true);
	}
	
}
