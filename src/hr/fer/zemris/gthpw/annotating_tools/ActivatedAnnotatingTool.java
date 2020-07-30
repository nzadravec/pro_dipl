package hr.fer.zemris.gthpw.annotating_tools;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;

public class ActivatedAnnotatingTool extends AbstractAction implements AnnotatingTool {
	
	private static final long serialVersionUID = 1L;
	
	private AnnotatingTool tool;

	public void setAnnotatingTool(AnnotatingTool tool) {
		this.tool = tool;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		tool.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		tool.mouseReleased(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		tool.mouseClicked(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		tool.mouseMoved(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		tool.mouseDragged(e);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		tool.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		tool.mouseExited(e);
	}

	@Override
	public void paint(Graphics2D g2d) {
		tool.paint(g2d);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
	
}
