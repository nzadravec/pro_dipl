package hr.fer.zemris.gthpw.annotating_tools.textline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.AbstractAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.comparators.SegmentsXCoordComparator;

public class LinkingTextLinesAnnotatingTool extends AbstractAnnotatingTool {

	private static final long serialVersionUID = 1L;

	private boolean firstClick = true;
	private int firstLineIndex;
	private int secondLineIndex;
	
	private Comparator<ImageSegment> segmentsInLineComparator = new SegmentsXCoordComparator();

	public LinkingTextLinesAnnotatingTool(String name, ActivatedAnnotatingTool activatedAnnotatingTool,
			AnnotatingDocImageModel annotatingDocImageModel) {
		super(name, activatedAnnotatingTool, annotatingDocImageModel);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		if (firstClick) {
			Outer: for (int lineIndex = 0; lineIndex < model.numberOfTextLines(); lineIndex++) {
				List<ImageSegment> textLine = model.getTextLine(lineIndex);
				for (int index = 0; index < textLine.size(); index++) {
					Rectangle bb = textLine.get(index).getMinBoundingBox();
					if (bb.contains(p)) {
						firstLineIndex = lineIndex;
						firstClick = false;
						break Outer;
					}
				}
			}
		} else {
			firstClick = true;
			
			boolean flag = false;
			Outer: for (int lineIndex = 0; lineIndex < model.numberOfTextLines(); lineIndex++) {
				List<ImageSegment> textLine = model.getTextLine(lineIndex);
				for (int index = 0; index < textLine.size(); index++) {
					Rectangle bb = textLine.get(index).getMinBoundingBox();
					if (bb.contains(p)) {
						if(lineIndex != firstLineIndex) {
							secondLineIndex = lineIndex;
							flag = true;
						}
						break Outer;
					}
				}
			}
			
			if(!flag) {
				return;
			}
			
			List<ImageSegment> newTextLine = new ArrayList<>(model.getTextLine(firstLineIndex));
			newTextLine.addAll(model.getTextLine(secondLineIndex));
			newTextLine.sort(segmentsInLineComparator);
			model.addTextLine(newTextLine);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paint(Graphics2D g2d) {
		if(!firstClick) {
			List<ImageSegment> textLine = model.getTextLine(firstLineIndex);
			g2d.setColor(Color.ORANGE);
			for(ImageSegment r : textLine) {
				Rectangle bb = r.getMinBoundingBox();
				g2d.drawRect(bb.x, bb.y, bb.width, bb.height);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		firstClick = true;
		model.showTextLines(true);
	}

}
