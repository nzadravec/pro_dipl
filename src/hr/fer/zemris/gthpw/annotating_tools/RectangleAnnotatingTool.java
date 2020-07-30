package hr.fer.zemris.gthpw.annotating_tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.function.Consumer;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.image.binary.BinaryImage;

public class RectangleAnnotatingTool extends AbstractAnnotatingTool {

	private static final long serialVersionUID = 1L;
	
	private Consumer<Rectangle> consumer;
	private Color drawRectColor;

	private boolean mousePressed;
	private Point firstRectCorner;
	private Rectangle rect;
	
	public RectangleAnnotatingTool(String name, ActivatedAnnotatingTool tool,
			AnnotatingDocImageModel model, Consumer<Rectangle> consumer, Color drawRectColor) {
		super(name, tool, model);
		this.consumer = Objects.requireNonNull(consumer);
		this.drawRectColor = Objects.requireNonNull(drawRectColor);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
		firstRectCorner = e.getPoint();
		
		rect = new Rectangle(firstRectCorner);
		rect.add(firstRectCorner);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		rect = new Rectangle(firstRectCorner);
		Point p = e.getPoint();
		p.x = Math.max(p.x, 0);
		p.y = Math.max(p.y, 0);
		rect.add(p);
		
		BinaryImage binaryImage = model.getDocImage().getBinaryImage();
		rect.x = Math.max(rect.x, 0);
		rect.y = Math.max(rect.y, 0);
		rect.width = Math.max(Math.min(rect.width, binaryImage.getWidth() - rect.x), 0);
		rect.height = Math.max(Math.min(rect.height, binaryImage.getHeight() - rect.y), 0);
		
		consumer.accept(rect);
	}

	@Override
	public void mouseClicked(MouseEvent e) {	
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		rect = new Rectangle(firstRectCorner);
		Point p = e.getPoint();
		p.x = Math.max(p.x, 0);
		p.y = Math.max(p.y, 0);
		rect.add(p);
	}

	@Override
	public void mouseEntered(MouseEvent e) {	
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void paint(Graphics2D g2d) {
		if(mousePressed) {
			g2d.setColor(drawRectColor);
			g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		mousePressed = false;
	}

}
