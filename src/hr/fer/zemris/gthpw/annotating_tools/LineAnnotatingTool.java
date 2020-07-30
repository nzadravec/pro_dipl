package hr.fer.zemris.gthpw.annotating_tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.function.Consumer;

import hr.fer.zemris.gthpw.annotating_tools.Line;
import hr.fer.zemris.gthpw.image.binary.BinaryImage;
import hr.fer.zemris.gthpw.AnnotatingDocImageModel;

public class LineAnnotatingTool extends AbstractAnnotatingTool {

	private static final long serialVersionUID = 1L;

	private Consumer<Line> consumer;
	private Color drawLineColor;

	private boolean mousePressed;
	private Point lineStart;
	private Line line;

	public LineAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model, Consumer<Line> consumer, Color drawLineColor) {
		super(name, tool, model);
		this.consumer = Objects.requireNonNull(consumer);
		this.drawLineColor = Objects.requireNonNull(drawLineColor);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
		lineStart = e.getPoint();

		line = new Line(lineStart, e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		line = new Line(lineStart, e.getPoint());
		
		BinaryImage binaryImage = model.getDocImage().getBinaryImage();
		line.start.x = Math.max(Math.min(line.start.x, binaryImage.getWidth() - 1), 0);
		line.start.y = Math.max(Math.min(line.start.y, binaryImage.getHeight() - 1), 0);
		line.end.x = Math.max(Math.min(line.end.x, binaryImage.getWidth() - 1), 0);
		line.end.y = Math.max(Math.min(line.end.y, binaryImage.getHeight() - 1), 0);
		
		consumer.accept(line);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		line = new Line(lineStart, e.getPoint());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void paint(Graphics2D g2d) {
		if (mousePressed) {
			g2d.setColor(drawLineColor);
			g2d.drawLine(line.start.x, line.start.y, line.end.x, line.end.y);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		mousePressed = false;
	}

}
