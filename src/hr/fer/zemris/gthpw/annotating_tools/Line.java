package hr.fer.zemris.gthpw.annotating_tools;

import java.awt.Point;

public class Line {

	public Point start;
	public Point end;
	
	public Line(Point start, Point end) {
		this(start.x, start.y, end.x, end.y);
	}
	
	public Line(int startX, int startY, int endX, int endY) {
		start = new Point(startX, startY);
		end = new Point(endX, endY);
	}
	
}
