package hr.fer.zemris.gthpw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

import hr.fer.zemris.gthpw.annotating_tools.AnnotatingTool;
import hr.fer.zemris.gthpw.transcript.Transcript;

public class JAnnotatingDocImage extends JComponent {

	private static final long serialVersionUID = 1L;

	private double zoomLevel = 1;

	private AnnotatingTool tool;
	private AnnotatingDocImageModel model;

	private MouseAdapter mouseAdapter = new MouseAdapter() {

		private Point origin;
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.isControlDown()) {
				if (e.getWheelRotation() == 1) {
					zoomLevel *= 0.95;
				} else {
					zoomLevel *= 1.05;
				}

				setSizeAndRepaintComponent();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
			if(SwingUtilities.isRightMouseButton(e)) {
				origin = new Point(e.getPoint());
				return;
			}
			
			translateMousePosition(e);
			tool.mousePressed(e);
			repaint();
		}

		private void translateMousePosition(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int newX = (int) (x / zoomLevel);
			int newY = (int) (y / zoomLevel);
			e.translatePoint(newX - x, newY - y);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				return;
			}
			
			translateMousePosition(e);
			tool.mouseReleased(e);
			repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				return;
			}
			
			translateMousePosition(e);
			tool.mouseClicked(e);
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				return;
			}
			
			translateMousePosition(e);
			tool.mouseMoved(e);
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			
			if(SwingUtilities.isRightMouseButton(e)) {
				JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, JAnnotatingDocImage.this);
                if (viewPort != null) {
                    int deltaX = origin.x - e.getX();
                    int deltaY = origin.y - e.getY();

                    Rectangle view = viewPort.getViewRect();
                    view.x += deltaX;
                    view.y += deltaY;

                    JAnnotatingDocImage.this.scrollRectToVisible(view);
                    return;
                }
			}
			
			translateMousePosition(e);
			tool.mouseDragged(e);
			repaint();
		}

		public void mouseEntered(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				return;
			}
			
			translateMousePosition(e);
			tool.mouseEntered(e);
			repaint();
		}

		public void mouseExited(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				return;
			}
			
			translateMousePosition(e);
			tool.mouseExited(e);
			repaint();
		}
	};

	public JAnnotatingDocImage(AnnotatingTool tool) {
		this(tool, null);
	}

	public JAnnotatingDocImage(AnnotatingTool tool, AnnotatingDocImageModel model) {
		this.tool = Objects.requireNonNull(tool);
		if (model != null) {
			this.model = model;
		} else {
			this.model = new DefaultAnnotatingDocImageModel();
		}

		addMouseWheelListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);

		this.model.addListener(new AnnotatingDocImageModelAdapter() {

			@Override
			public void stateChanged(ChangeEvent e) {
				repaint();
			}

			@Override
			public void docImageLoaded(ChangeEvent e) {
				zoomLevel = 1;

				setSizeAndRepaintComponent();
			}
		});
	}

	private void setSizeAndRepaintComponent() {
		DocImage docImage = model.getDocImage();
		int width = (int) (docImage.getImage().getWidth() * zoomLevel);
		int height = (int) (docImage.getImage().getHeight() * zoomLevel);
		Dimension componentSize = new Dimension(width, height);
		setPreferredSize(componentSize);
		setMinimumSize(componentSize);
		revalidate();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		DocImage docImage = model.getDocImage();
		if (docImage == null) {
			return;
		}

		BufferedImage image = docImage.getImage();
		int imageWidth = (int) (image.getWidth() * zoomLevel);
		int imageHeight = (int) (image.getHeight() * zoomLevel);
		Image offScreenImage = createImage(imageWidth + 30, imageHeight + 30);
		Graphics2D g2d = (Graphics2D) offScreenImage.getGraphics();
		g2d.drawImage(image, 0, 0, imageWidth, imageHeight, null);

		g2d.scale(zoomLevel, zoomLevel);
		// g2d.translate(x, y);

		if (model.showSegments()) {
			for (ImageSegment s : model.getSegments()) {
				Rectangle bb = s.getMinBoundingBox();
				g2d.setColor(Color.MAGENTA);
				g2d.drawRect(bb.x, bb.y, bb.width, bb.height);

				if (model.showCharsLabel()) {
					Character label = model.getSegmentLabel(s);
					if (label != null) {
						g2d.setColor(Color.BLUE);
						g2d.setFont(new Font(null, Font.PLAIN, 24));
						g2d.drawString("" + label, bb.x, bb.y + bb.height + 24 + 5);
					}
				}
			}
		}

		if (model.showTextLines()) {
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Color.CYAN);

			for (int lineIndex = 0; lineIndex < model.numberOfTextLines(); lineIndex++) {
				List<ImageSegment> textLine = model.getTextLine(lineIndex);
				for (int index = 1; index < textLine.size(); index++) {
					ImageSegment r1 = textLine.get(index - 1);
					Rectangle bb1 = r1.getMinBoundingBox();
					ImageSegment r2 = textLine.get(index);
					Rectangle bb2 = r2.getMinBoundingBox();

					int x1 = (int) (bb1.getCenterX());
					int y1 = (int) (bb1.getCenterY());
					int x2 = (int) (bb2.getCenterX());
					int y2 = (int) (bb2.getCenterY());
					g2d.drawLine(x1, y1, x2, y2);
				}
			}
		}

		Transcript transcript = model.getDocImage().getTranscript();
		if (model.showWords()) {

			g2d.setStroke(new BasicStroke(2));
			g2d.setFont(new Font(null, Font.PLAIN, 30));

			for (int lineIndex = 0; lineIndex < model.numberOfTextLines(); lineIndex++) {

				String[] words = null;
				if (transcript != null && lineIndex < transcript.numberOfLines()) {
					words = transcript.getWordsAtLine(lineIndex);
				}

				for (int wordIndex = 0; wordIndex < model.numberOfWordsInLine(lineIndex); wordIndex++) {
					List<ImageSegment> word = model.getWordInLine(wordIndex, lineIndex);
//					if (word.isEmpty()) {
//						break;
//					}

					if (model.showTranscriptWords() && words != null
							&& (wordIndex >= words.length || word.size() != words[wordIndex].length())) {
						g2d.setColor(Color.RED);
					} else {
						g2d.setColor(Color.DARK_GRAY);
					}

					Rectangle bb = word.get(0).getMinBoundingBox();
					for (int index = 1; index < word.size(); index++) {
						bb = bb.union(word.get(index).getMinBoundingBox());
					}
					//g2d.drawLine(bb.x, bb.y + bb.height + 2, bb.x + bb.width, bb.y + bb.height + 2);
					g2d.drawRect(bb.x - 5, bb.y - 5, bb.width + 10, bb.height + 10);

					if (model.showTranscriptWords() && words != null && wordIndex < words.length) {

						g2d.drawString(words[wordIndex], bb.x + 1, bb.y + bb.height + 30 + 4);
//						if(word.size() != words[wordIndex].length()) {
//							g2d.drawString(String.valueOf(word.size()), bb.x + bb.width, bb.y + bb.height + 30 + 1);
//						}
					}
				}
			}
		}

		tool.paint(g2d);
		g2d.dispose();

//		Insets insets = getInsets();
//		int x = insets.left;
//		int y = insets.bottom;

//        int x = insets.left + (getWidth()-imageWidth)/2;
//        int y = insets.bottom + (getHeight()-imageHeight)/2;

		g.drawImage(offScreenImage, 0, 0, null);
		// g.drawImage(offScreenImage, x, y, null);
	}

	public AnnotatingDocImageModel getModel() {
		return model;
	}

}
