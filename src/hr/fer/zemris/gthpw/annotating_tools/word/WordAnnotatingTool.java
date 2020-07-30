package hr.fer.zemris.gthpw.annotating_tools.word;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.AbstractAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;

public class WordAnnotatingTool extends AbstractAnnotatingTool {

	private static final long serialVersionUID = 1L;

	private boolean secondClick;
	private int clickedLineIndex;
	private int firstIndex;
	private int secondIndex;

	public WordAnnotatingTool(String name, ActivatedAnnotatingTool activatedAnnotatingTool,
			AnnotatingDocImageModel annotatingDocImageModel) {
		super(name, activatedAnnotatingTool, annotatingDocImageModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		if (secondClick) {
			secondClick = false;
			
			boolean flag = false;
			List<ImageSegment> textLine = model.getTextLine(clickedLineIndex);
			for (int index = 0; index < textLine.size(); index++) {
				Rectangle bb = textLine.get(index).getMinBoundingBox();
				if (bb.contains(p)) {
					secondIndex = index;
					flag = true;
					break;
				}
			}
			
			if(!flag) {
				return;
			}
			
			if(secondIndex < firstIndex) {
				int tmp = secondIndex;
				secondIndex = firstIndex;
				firstIndex = tmp;
			}
			
			List<ImageSegment> word = new ArrayList<>(textLine.subList(firstIndex, secondIndex + 1));
			model.addWord(word, clickedLineIndex);

		} else {
			for (int lineIndex = 0; lineIndex < model.numberOfTextLines(); lineIndex++) {
				List<ImageSegment> textLine = model.getTextLine(lineIndex);
				for (int index = 0; index < textLine.size(); index++) {
					Rectangle bb = textLine.get(index).getMinBoundingBox();
					if (bb.contains(p)) {
						clickedLineIndex = lineIndex;
						firstIndex = index;
						secondClick = true;
						break;
					}
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

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
		if (secondClick) {
			List<ImageSegment> textLine = model.getTextLine(clickedLineIndex);
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
		secondClick = false;
		model.showWords(true);
	}

}
