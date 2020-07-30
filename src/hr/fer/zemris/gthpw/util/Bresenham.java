package hr.fer.zemris.gthpw.util;

import java.awt.Point;
import java.util.Objects;
import java.util.function.Consumer;

public class Bresenham {
	
	private Consumer<Point> consumer;
	
	public Bresenham(Consumer<Point> consumer) {
		this.consumer = Objects.requireNonNull(consumer);
	}

	public void generateLinePoints(Point start, Point end) {
		bresenham(start.x, start.y, end.x, end.y, consumer);
	}
	
	public void bresenham(int xs, int ys, int xe, int ye, Consumer<Point> consumer) {
		if (xs <= xe) {
			if (ys <= ye) {
				bresenham2(xs, ys, xe, ye);
			} else {
				bresenham3(xs, ys, xe, ye);
			}
		} else {
			if (ys >= ye) {
				bresenham2(xe, ye, xs, ys);
			} else {
				bresenham3(xe, ye, xs, ys);
			}
		}
	}

	private void bresenham2(int xs, int ys, int xe, int ye) {
		int x, yc, d;
		int a, yf;
		Point p = new Point();

		if (ye - ys <= xe - xs) {
			a = 2 * (ye - ys);
			yc = ys;
			yf = -(xe - xs);
			d = -2 * (xe - xs);
			for (x = xs; x <= xe; x++) {

				p.x = x;
				p.y = yc;
				consumer.accept(new Point(p));

				yf = yf + a;
				if (yf >= 0) {
					yf = yf + d;
					yc = yc + 1;
				}
			}
		} else {
			x = xe;
			xe = ye;
			ye = x;
			x = xs;
			xs = ys;
			ys = x;
			a = 2 * (ye - ys);
			yc = ys;
			yf = -(xe - xs);
			d = -2 * (xe - xs);
			for (x = xs; x <= xe; x++) {

				p.x = yc;
				p.y = x;
				consumer.accept(new Point(p));

				yf = yf + a;
				if (yf >= 0) {
					yf = yf + d;
					yc = yc + 1;
				}
			}
		}
	}

	private void bresenham3(int xs, int ys, int xe, int ye) {
		int x, yc, d;
		int a, yf;
		Point p = new Point();

		if (-(ye - ys) <= xe - xs) {
			a = 2 * (ye - ys);
			yc = ys;
			yf = (xe - xs);
			d = 2 * (xe - xs);
			for (x = xs; x <= xe; x++) {

				p.x = x;
				p.y = yc;
				consumer.accept(new Point(p));

				yf = yf + a;
				if (yf <= 0) {
					yf = yf + d;
					yc = yc - 1;
				}
			}
		} else {
			x = xe;
			xe = ys;
			ys = x;
			x = xs;
			xs = ye;
			ye = x;
			a = 2 * (ye - ys);
			yc = ys;
			yf = (xe - xs);
			d = 2 * (xe - xs);
			for (x = xs; x <= xe; x++) {

				p.x = yc;
				p.y = x;
				consumer.accept(new Point(p));

				yf = yf + a;
				if (yf <= 0) {
					yf = yf + d;
					yc = yc - 1;
				}
			}
		}
	}
	
}
