package hr.fer.zemris.gthpw.annotating_tools.segment;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComponent;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.annotating_tools.AbstractAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.image.binary.BinaryImage;

public class ReshapingSegmentAnnotatingTool extends AbstractAnnotatingTool {

	private static final long serialVersionUID = 1L;

	private static final int CORNER_NEIGHBORHOOD = 10;
	private static final int EDGE_NEIGHBORHOOD = 10;

	// Rectangle's corners
	private static final int NW_CORNER = 1;
	private static final int NE_CORNER = 2;
	private static final int SE_CORNER = 3;
	private static final int SW_CORNER = 4;

	// Rectangle's edges
	private static final int E_EDGE = 5;
	private static final int W_EDGE = 6;
	private static final int N_EDGE = 7;
	private static final int S_EDGE = 8;

	private static final int DEFAULT = 0;

	private int mousePressedAt;

	private boolean segmentSelected;
	private Rectangle boundingBox;

	public ReshapingSegmentAnnotatingTool(String name, ActivatedAnnotatingTool tool, AnnotatingDocImageModel model) {
		super(name, tool, model);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (segmentSelected && mousePressedAt == DEFAULT) {
			reshapeSegment();
			segmentSelected = false;
		}
	}
	
	private void reshapeSegment() {
		BinaryImage binaryImage = model.getDocImage().getBinaryImage();
		boundingBox.x = Math.max(boundingBox.x, 0);
		boundingBox.y = Math.max(boundingBox.y, 0);
		boundingBox.width = Math.max(Math.min(boundingBox.width, binaryImage.getWidth() - boundingBox.x), 0);
		boundingBox.height = Math.max(Math.min(boundingBox.height, binaryImage.getHeight() - boundingBox.y), 0);
		
		int height = boundingBox.height;
		int width = boundingBox.width;
		
		Collection<Point> pixels = new ArrayList<>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
//				if (binaryImage.getValueAt(boundingBox.x + x, boundingBox.y + y)) {
//					pixels.add(new Point(boundingBox.x + x, boundingBox.y + y));
//				}
				
				pixels.add(new Point(boundingBox.x + x, boundingBox.y + y));
			}
		}
		
		if(pixels.isEmpty()) {
			return;
		}
		
		model.addSegment(new ImageSegment(pixels));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (segmentSelected && mousePressedAt == DEFAULT) {
			reshapeSegment();
			segmentSelected = false;
			return;
		}

		Point p = e.getPoint();

		Collection<ImageSegment> segments = model.getSegments();
		List<ImageSegment> segmentsContainingPoint = segments.stream().filter(s -> s.getMinBoundingBox().contains(p))
				.collect(Collectors.toList());

		if (segmentsContainingPoint.size() != 1) {
			return;
		}

		ImageSegment selectedSegment = segmentsContainingPoint.get(0);
		model.removeSegment(selectedSegment);
		
		boundingBox = selectedSegment.getMinBoundingBox();
		segmentSelected = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (segmentSelected) {
			setCursor(e);
		}
	}
	
	private void setCursor(MouseEvent e) {
		JComponent component = (JComponent) e.getSource();

		// mouse position
		Point p = e.getPoint();
		Rectangle r = boundingBox;

		if (Point.distance(p.x, p.y, r.x, r.y) < CORNER_NEIGHBORHOOD) {

			component.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			mousePressedAt = NW_CORNER;

		} else if (Point.distance(p.x, p.y, r.x + r.width, r.y) < CORNER_NEIGHBORHOOD) {

			component.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			mousePressedAt = NE_CORNER;

		} else if (Point.distance(p.x, p.y, r.x, r.y + r.height) < CORNER_NEIGHBORHOOD) {

			component.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			mousePressedAt = SW_CORNER;

		} else if (Point.distance(p.x, p.y, r.x + r.width, r.y + r.height) < CORNER_NEIGHBORHOOD) {

			component.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			mousePressedAt = SE_CORNER;

		}

		else if (Point.distance(p.x, p.y, r.x, r.getCenterY()) < EDGE_NEIGHBORHOOD) {

			component.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			mousePressedAt = W_EDGE;

		} else if (Point.distance(p.x, p.y, r.getCenterX(), r.y) < EDGE_NEIGHBORHOOD) {

			component.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			mousePressedAt = N_EDGE;

		} else if (Point.distance(p.x, p.y, r.x + r.width, r.getCenterY()) < EDGE_NEIGHBORHOOD) {

			component.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			mousePressedAt = E_EDGE;

		} else if (Point.distance(p.x, p.y, r.getCenterX(), r.y + r.height) < EDGE_NEIGHBORHOOD) {

			component.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			mousePressedAt = S_EDGE;

		}

		else {
			component.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			mousePressedAt = DEFAULT;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (segmentSelected) {
			Point p = e.getPoint();
			Rectangle r = new Rectangle(boundingBox);

			switch (mousePressedAt) {
			case NW_CORNER:
				r = new Rectangle(new Point(r.x + r.width, r.y + r.height));
				r.add(p);
				break;
			case NE_CORNER:
				r = new Rectangle(new Point(r.x, r.y + r.height));
				r.add(p);
				break;
			case SE_CORNER:
				r = new Rectangle(new Point(r.x, r.y));
				r.add(p);
				break;
			case SW_CORNER:
				r = new Rectangle(new Point(r.x + r.width, r.y));
				r.add(p);
				break;
			case E_EDGE:
				r.width += p.x - (r.x + r.width);
				break;
			case W_EDGE:
				r.width -= p.x - r.x;
				r.x = p.x;
				break;
			case N_EDGE:
				r.height -= p.y - r.y;
				r.y = p.y;
				break;
			case S_EDGE:
				r.height += p.y - (r.y + r.height);
				break;

			default:
				System.err.println("error");
				System.exit(1);
				break;
			}
			
			if(r.width > 9 && r.height > 9) {
				boundingBox = r;
			}
		}
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
		if(!segmentSelected) {
			return;
		}
		
		Rectangle r = boundingBox;
		
		g2d.setColor(Color.BLACK);
		g2d.drawRect(r.x, r.y, r.width, r.height);

		int len = CORNER_NEIGHBORHOOD >> 1;
		int halfLen = (len >> 1);
		g2d.fillRect(r.x - halfLen, r.y - halfLen, len, len);
		g2d.fillRect(r.x + r.width - halfLen, r.y - halfLen, len, len);
		g2d.fillRect(r.x - halfLen, r.y + r.height - halfLen, len, len);
		g2d.fillRect(r.x + r.width - halfLen, r.y + r.height - halfLen, len, len);

		g2d.fillRect(r.x - halfLen, (int) r.getCenterY() - halfLen, len, len);
		g2d.fillRect((int) r.getCenterX() - halfLen, r.y - halfLen, len, len);
		g2d.fillRect(r.x + r.width - halfLen, (int) r.getCenterY() - halfLen, len, len);
		g2d.fillRect((int) r.getCenterX() - halfLen, r.y + r.height - halfLen, len, len);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		segmentSelected = false;
		model.showSegments(true);
	}

}
