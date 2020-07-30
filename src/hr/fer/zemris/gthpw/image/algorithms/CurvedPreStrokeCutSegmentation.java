package hr.fer.zemris.gthpw.image.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import hr.fer.zemris.gthpw.image.binary.BinaryImage;

public class CurvedPreStrokeCutSegmentation {
	
	public static List<List<Point>> segment(BinaryImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		
		int[] di = new int[w];
		
		int area = 0;
		int sum = 0;
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				if(image.getValueAt(x, y)) {
					area++;
					sum += y;
				}
			}
		}
		int yc = Math.round(sum/(float)area);
		
		int[][] cost = new int[h][w];
		int[][] source = new int[h][w];
		
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				cost[y][x] = Integer.MAX_VALUE;
				source[y][x] = Integer.MAX_VALUE;
			}
		}
		
		List<Point> queue = new LinkedList<>();

		for(int x = 0; x < w; x++) {
			queue.add(new Point(x, h-1));
			cost[h-1][x] = 0;
		}
		
		while (!queue.isEmpty()) {
			Point p = queue.remove(0);
			
			if (p.y >= yc) {
				for (int delta : new int[] { -1, 0, 1 }) {
					Point p2 = new Point(p.x+delta, p.y-1);
					if(p2.x < 0 || p2.x >= w) {
						continue;
					}
					
					int newCost = cost[p.y][p.x] + cs(delta) + ci(p2, image, di);
					
					if (newCost<cost[p2.y][p2.x]) {
						source[p2.y][p2.x] = p.x;
						cost[p2.y][p2.x] = newCost;
						queue.add(p2);
					}
				}
			}
		}
		
		int[] costLine = new int[w];
		int[] sourceLine = new int[w];
		for (int x = 0; x < w; x++) {
			costLine[x] = cost[yc][x];
			cost[yc][x] = Integer.MAX_VALUE;
			source[yc][x] = Integer.MAX_VALUE;
			sourceLine[x] = source[yc][x];
		}
		
		for (int x = 0; x < w; x++) {
			queue.add(new Point(x, 0));
			cost[0][x] = 0;
		}
		
		while (!queue.isEmpty()) {
			Point p = queue.remove(0);
			if (p.y <= yc) {
				for (int delta : new int[] { -1, 0, 1 }) {
					Point p2 = new Point(p.x+delta, p.y+1);
					if(p2.x < 0 || p2.x >= w) {
						continue;
					}
					
					int newCost = cost[p.y][p.x] + cs(delta) + ci(p2, image, di);
					
					if (newCost<cost[p2.y][p2.x]) {
						source[p2.y][p2.x] = p.x;
						cost[p2.y][p2.x] = newCost;
						queue.add(p2);
					}
				}
			}
		}
		
		for (int x = 0; x < w; x++) {
			costLine[x] += cost[yc][x];
		}
		
		List<Integer> localMinXs = new ArrayList<>();
		
//		boolean flag = false;
//		for (int i = 1; i < w; i++) {
//			if(flag && costLine[i-1] < costLine[i]) {
//				flag = false;
//				localMinXs.add(i-1);
//			} else if(costLine[i-1] > costLine[i]) {
//				flag = true;
//			}
//		}
		
		int min = Integer.MAX_VALUE;
		int idx = -1;
		for (int x = 0; x < w; x++) {
			if(costLine[x] < min) {
				min = costLine[x];
				idx = x;
			}
		}
		
		localMinXs.add(idx);
		
		System.out.println(Arrays.toString(costLine));

		List<List<Point>> optimalPaths = new ArrayList<>();
		for(Integer i : localMinXs) {
			List<Point> path = new ArrayList<>();
			//Point p = new Point(i, yc);
			//path.add(p);
			
			int x = i;
			for(int y = yc; y < (h-1); y++) {
				System.out.println(yc + "\t" + y + " " + x);
				x = source[y][x];
				Point p2 = new Point(x, y+1);
				path.add(p2);
			}
			
			x = i;
			source[yc][x] = sourceLine[x];
			for(int y = yc; y > 0; y--) {
				x = source[y][x];
				Point p2 = new Point(x, y-1);
				path.add(p2);
			}
			
			optimalPaths.add(path);
		}
		
		return optimalPaths;
	}
	
	public static List<List<Point>> segment2(BinaryImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		
		int[] di = new int[w];
		
		int area = 0;
		int sum = 0;
		for (int i = 0; i < w; i++) {
			int sum2 = 0;
			for (int j = 0; j < h; j++) {
				if(image.getValueAt(i, j)) {
					area++;
					sum += j;
					sum2++;
				}
			}
			di[i] = sum2;
		}
		int yc = Math.round(sum/(float)area);
		
		int[][] cost = new int[w][h];
		int[][] source = new int[w][h];
		
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				cost[i][j] = Integer.MAX_VALUE/2;
				source[i][j] = Integer.MAX_VALUE;
			}
		}
		
		List<Point> queue = new LinkedList<>();

		for (int i = 0; i < w; i++) {
			queue.add(new Point(i, h-1));
			cost[i][h-1] = 0;
		}
		
		while (!queue.isEmpty()) {
			Point p = queue.remove(0);
			
			if (p.y > yc) {
				for (int delta : new int[] { -1, 0, 1 }) {
					Point p2 = new Point(p.x+delta, p.y-1);
					if(p2.x < 0 || p2.x >= w) {
						continue;
					}
					
					int newCost = cost[p.x][p.y] + cs(delta) + ci(p2, image, di);
					
					if (newCost<cost[p2.x][p2.y]) {
						source[p2.x][p2.y] = p.x;
						cost[p2.x][p2.y] = newCost;
						queue.add(p2);
					}
				}
			}
		}
		
		int[] costLine = new int[w];
		int[] sourceLine = new int[w];
		for (int i = 0; i < w; i++) {
			costLine[i] = cost[i][yc];
			cost[i][yc] = Integer.MAX_VALUE/2;
			sourceLine[i] = source[i][yc];
			source[i][yc] = Integer.MAX_VALUE;
		}
		
		for (int i = 0; i < w; i++) {
			queue.add(new Point(i, 0));
			cost[i][0] = 0;
		}
		
		while (!queue.isEmpty()) {
			Point p = queue.remove(0);
			if (p.y < yc) {
				for (int delta : new int[] { -1, 0, 1 }) {
					Point p2 = new Point(p.x+delta, p.y+1);
					if(p2.x < 0 || p2.x >= w) {
						continue;
					}
					
					int newCost = cost[p.x][p.y] + cs(delta) + ci(p2, image, di);
					
					if (newCost<cost[p2.x][p2.y]) {
						source[p2.x][p2.y] = p.x;
						cost[p2.x][p2.y] = newCost;
						queue.add(p2);
					}
				}
			}
		}
		
		for (int i = 0; i < w; i++) {
			costLine[i] += cost[i][yc];
		}
		
		System.out.println(Arrays.toString(costLine));
		
		List<Integer> localMinXs = new ArrayList<>();
		
		boolean flag = false;
		for (int i = 1; i < w; i++) {
			if(flag && costLine[i-1] < costLine[i]) {
				flag = false;
				localMinXs.add(i-1);
			} else if(costLine[i-1] > costLine[i]) {
				flag = true;
			}
		}
		
		int count = (int)(w / (h/2.));
		count = Math.min(count, localMinXs.size());
		int[] locMinXs = new int[count];
		for(int j = 0; j < count; j++) {
			int min = Integer.MAX_VALUE;
			int idx = -1;
			for (int i : localMinXs) {
				if(costLine[i] < min) {
					min = costLine[i];
					idx = i;
				}
			}
			costLine[idx] = Integer.MAX_VALUE;
			locMinXs[j] = idx;
			localMinXs.remove(Integer.valueOf(idx));
		}
		
		System.out.println(Arrays.toString(locMinXs));
		
//		int min = Integer.MAX_VALUE;
//		int idx = -1;
//		for (int i = 0; i < w; i++) {
//			if(costLine[i] < min) {
//				min = costLine[i];
//				idx = i;
//			}
//		}
//		localMinXs.add(idx);

		List<List<Point>> optimalPaths = new ArrayList<>();
		for(int i : locMinXs) {
			List<Point> path = new ArrayList<>();
			//Point p = new Point(i, yc);
			//path.add(p);
			
			int x = i;
			for(int j = yc; j < (h-1); j++) {
				x = source[x][j];
				Point p2 = new Point(x, j+1);
				path.add(p2);
			}
			
			x = i;
			source[x][yc] = sourceLine[i];
			for(int j = yc; j > 0; j--) {
				x = source[x][j];
				Point p2 = new Point(x, j-1);
				path.add(p2);
			}
			
			optimalPaths.add(path);
		}
		
		return optimalPaths;
	}

	private static int ci(Point p, BinaryImage image, int[] di) {
		if(image.getValueAt(p.x, p.y)) {
			return 2;
		}
		
		if((p.x+1) < image.getWidth() && (image.getValueAt(p.x+1, p.y))) {
			return -5;
		} else {
			return 0;
		}
	}

	private static int cs(int delta) {
		return Math.abs(delta);
	}

}
