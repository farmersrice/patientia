package utilities;

import java.util.ArrayList;
import java.util.LinkedList;

import game_map.GameMap;
import game_map.Tile;
import units.MobileUnit;

public class Algorithms {
	public static int king_move_x[] = {0, 0, -1, 1, 1, 1, -1, -1};
	public static int king_move_y[] = {-1, 1, 0, 0, -1, 1, -1, 1};
	
	public static boolean isValidCoordinate(int x, int y, int r, int c) {
		return 0 <= x && x < r && 0 <= y && y < c;
	}
	
	public static boolean isNeighborKing(int x, int y, int tx, int ty) {
		return Math.max(Math.abs(tx - x), Math.abs(ty - y)) <= 1;
	}
	
	public static class BfsReturn {
		int[][] dist;
		int[][][] par;
		
		BfsReturn(int[][] d, int[][][] p) {
			dist = d; par = p;
		}
		
		int[][] getDist() {
			return dist;
		}
		
		int[][][] getPar() {
			return par;
		}
	}
	
	public static BfsReturn kingBFS(GameMap known, int i, int j) {
		return distanceBFS(known, i, j, false);
	}
	
	public static BfsReturn distanceBFS(GameMap known, int x, int y, boolean extendedNeighbors) {
		final int IINF = 1000000000;

		int r = known.getR(), c = known.getC();
		
		int[][] dist = new int[r][c];
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				dist[i][j] = IINF;
			}
		}
		
		int[][][] par = new int[r][c][2];


		dist[x][y] = 0;
		par[x][y] = new int[]{-1, -1};

		LinkedList<int[]> q = new LinkedList<int[]>();

		q.push(new int[]{x, y});

		Tile[][] terrain = known.getTerrain();
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		while (q.size() > 0) {
			int[] cur = q.pop();
					
			ArrayList<int[]> neighbors = new ArrayList<int[]>();

			if (!extendedNeighbors) {
				for (int i = 0; i < 8; i++) {
					int newx = cur[0] + king_move_x[i];
					int newy = cur[1] + king_move_y[i];
					
					if (isValidCoordinate(newx, newy, r, c)) {
						neighbors.add(new int[]{newx, newy});
					}
				}
			} else {
				//currently not supported, but super ez to code
			}
			
			for (int[] p : neighbors) {
				if (dist[p[0]][p[1]] < IINF) continue; //Already considered
				if (terrain[p[0]][p[1]] == Tile.BLOCKED) continue;
				if (mobileUnits[p[0]][p[1]].isValid()) continue;
				

				dist[p[0]][p[1]] = dist[cur[0]][cur[1]] + 1;
				par[p[0]][p[1]] = cur;
				q.push(p);
			}
		}

		return new BfsReturn(dist, par);
	}
	
	public static int[] moveTowards(GameMap known, int x, int y, int tx, int ty) {
		BfsReturn res = kingBFS(known, tx, ty);
		
		final int IINF = 1000000000;
		
		int bestDist = IINF;
		
		int[] nextStep = {0, 0};
		
		int[][] dist = res.getDist();
		
		for (int i = 0; i < 8; i++) {
			int newx = x + king_move_x[i];
			int newy = y + king_move_y[i];
			
			if (isValidCoordinate(newx, newy, known.getR(), known.getC())) {
				if (dist[newx][newy] < bestDist) {
					bestDist = dist[newx][newy];
					nextStep = new int[]{newx, newy};
				}
			}
		}
		
		return nextStep;
	}
}
