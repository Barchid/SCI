package hunter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import core.Environment;

public class Dijkstra {
	private Environment environment;
	private Map<Point, Integer> distances; // map that contains the distances

	public Dijkstra(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Launches BFS from the coordinates in parameter as the starting point
	 * 
	 * @param x
	 * @param y
	 */
	public void breadthFirstSearch(int x, int y) {
		LinkedList<Point> frontier = new LinkedList<Point>();
		frontier.push(new Point(x, y));
		this.distances = new HashMap<Point, Integer>();
		this.distances.put(new Point(x, y), 0);

		// WHILE [there is still someone to copute distance]
		while (!frontier.isEmpty()) {
			Point current = frontier.removeFirst();
			List<Point> neighborhood = this.getNeighborhood(current);
			for (Point neighbor : neighborhood) {
				if (!this.distances.containsKey(neighbor)) {
					frontier.addLast(neighbor);
					this.distances.put(neighbor, 1 + this.distances.get(current));
				}
			}
//			this.dumpDistanceMap();
//			System.out.println("\n\n");
		}
	}

	/**
	 * Used only for debugging purpose
	 */
	@SuppressWarnings("unused")
	private void dumpDistanceMap() {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < this.environment.getWidth(); x++) {
			sb.append("\n");
			for (int y = 0; y < this.environment.getHeight(); y++) {
				sb.append("|");
				Point p = new Point(x, y);
				if (this.distances.containsKey(p)) {
					int n = this.distances.get(p);
					if (n % 100 != n) {
						sb.append(n);
					} else if (n % 10 != n) {
						sb.append(" " + n);
					} else {
						sb.append("  " + n);
					}
				} else {
					sb.append("  X");
				}
			}
		}

		System.out.println(sb.toString());
	}

	public List<Point> getNeighborhood(Point point) {
		List<Point> neighborhood = new ArrayList<Point>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// disable diagonals
				if(i != 0 && j != 0) {
					continue;
				}
				int x = point.x + i;
				int y = point.y + j;

				if (this.environment.isOutOfBound(x, y) && !this.environment.isTorus()) {
					continue;
				}

				if (this.environment.isOutOfBound(x, y) && this.environment.isTorus()) {
					if (x < 0) {
						x = this.environment.getWidth() - 1;
					}

					if (x >= this.environment.getWidth()) {
						x = 0;
					}

					if (y < 0) {
						y = this.environment.getHeight() - 1;
					}

					if (y >= this.environment.getHeight()) {
						y = 0;
					}
				}

				if (this.environment.getCell(x, y) != null) {
					continue;
				}

				neighborhood.add(new Point(x, y));
			}
		}
		return neighborhood;
	}

	public Point getBestNeighbor(Point point) {
		List<Point> neighborhood = this.getNeighborhood(point);
		if (neighborhood.isEmpty()) {
			return null;
		}

		Point best = neighborhood.get(0);
		for (Point neighbor : neighborhood) {
			if (this.distances.get(neighbor) == null) {
				continue;
			}

			if (this.distances.get(best) == null) {
				best = neighbor;
				continue;
			}
			
			if (this.distances.get(best).intValue() > this.distances.get(neighbor).intValue()) {
				best = neighbor;
			}
		}

		return best;
	}
	
	public Point getWorstNeighbor(Point point) {
		List<Point> neighborhood = this.getNeighborhood(point);
		if (neighborhood.isEmpty()) {
			return null;
		}

		Point best = neighborhood.get(0);
		for (Point neighbor : neighborhood) {
			if (this.distances.get(neighbor) == null) {
				continue;
			}

			if (this.distances.get(best) == null) {
				best = neighbor;
				continue;
			}
			
			if (this.distances.get(best).intValue() < this.distances.get(neighbor).intValue()) {
				best = neighbor;
			}
		}

		return best;
	}
}
