package hr.fer.zemris.gthpw.annotating_tools;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.Action;

public interface AnnotatingTool extends Action {
	public void mousePressed(MouseEvent e);
	public void mouseReleased(MouseEvent e);
	public void mouseClicked(MouseEvent e);
	public void mouseMoved(MouseEvent e);
	public void mouseDragged(MouseEvent e);
	public void mouseEntered(MouseEvent e);
	public void mouseExited(MouseEvent e);
	public void paint(Graphics2D g2d);
}
