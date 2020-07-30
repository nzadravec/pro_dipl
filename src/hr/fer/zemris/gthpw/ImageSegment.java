package hr.fer.zemris.gthpw;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ImageSegment {

	private Set<Point> pixels;

	private Rectangle minBoundingBox;
	
	private Point segmentCenter;
	
	public ImageSegment(Collection<Point> pixels) {
		Objects.requireNonNull(pixels);
		if(pixels.isEmpty()) {
			throw new IllegalArgumentException("image segment must contain atleast one pixel");
		}
		
		this.pixels = new HashSet<>(pixels);
		
		
		int xMax, xMin;
		int yMax, yMin;

		Point fst = pixels.iterator().next();
		xMax = xMin = fst.x;
		yMax = yMin = fst.y;

		for (Point p : pixels) {
			if (p.x > xMax) {
				xMax = p.x;
			} else if (p.x < xMin) {
				xMin = p.x;
			}

			if (p.y > yMax) {
				yMax = p.y;
			} else if (p.y < yMin) {
				yMin = p.y;
			}
		}
		minBoundingBox = new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
		
		
		int sumX, sumY;
        sumX = sumY = 0;
        for (Point p : pixels) {
            sumX += p.getX();
            sumY += p.getY();
        }
        segmentCenter = new Point(sumX / pixels.size(), sumY / pixels.size());
	}
	
	public Set<Point> getPixels() {
		return Collections.unmodifiableSet(pixels);
	}
	
	public Rectangle getMinBoundingBox() {
		return minBoundingBox;
	}
	
	public Point getSegmentCenter() {
		return segmentCenter;
	}
	
	public static ImageSegment mergeImageSegments(Collection<ImageSegment> isColl) {
		Set<Point> pixels = new HashSet<>();
		for(ImageSegment is : isColl) {
			pixels.addAll(is.pixels);
		}
		
		return new ImageSegment(pixels);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((minBoundingBox == null) ? 0 : minBoundingBox.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageSegment other = (ImageSegment) obj;
		if (minBoundingBox == null) {
			if (other.minBoundingBox != null)
				return false;
		} else if (!minBoundingBox.equals(other.minBoundingBox))
			return false;
		return true;
	}
	
}
