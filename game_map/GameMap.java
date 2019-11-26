package game_map;

import java.util.HashMap;
import java.util.LinkedList;

import units.MobileUnit;
import units.StaticUnit;
import units.Unit;

enum Tile {
	CLEAR, MINERALS, BLOCKED, UNKNOWN, MARKER;
}

public class GameMap {
	private Tile[][] terrain;
	private MobileUnit[][] mobileUnits;
	private StaticUnit[][] staticUnits;
	
	private int[][] lastUpdated; // the last time a tile knowledge was updated
	private int r, c;

	public GameMap(int rows, int cols) {
		r = rows;
		c = cols;
		terrain = new Tile[r][c];

		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				terrain[i][j] = Tile.UNKNOWN;
			}
		}
		mobileUnits = new MobileUnit[r][c];
		staticUnits = new StaticUnit[r][c];
	}

	private int[] move_x = { 0, 0, -1, 1, 1, 1, -1, -1 };
	private int[] move_y = { -1, 1, 0, 0, -1, 1, -1, 1 };

	public void generate() {
		final int MINERALS_RADIUS = 2;
		final int BLOCKED_THRESHOLD = 5;
		final int BLOCKED_WIDTH = 1;
		
		Tile[][] marks = new Tile[r][c];
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				if (Math.random() < 0.02) {
					//Select as mountain
					marks[i][j] = Tile.BLOCKED;
				} else if (Math.random() < 1.0/30) {
					//Select as Math.mineral

					marks[i][j] = Tile.MINERALS;
				}
			}
		}
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				if (marks[i][j] == Tile.MINERALS) {
					//Generate Math.minerals around it with high probability

					for (int k = Math.max(0, i - MINERALS_RADIUS); k < Math.min(r, i + MINERALS_RADIUS + 1); k++) {
						for (int l = Math.max(0, j - MINERALS_RADIUS); l < Math.min(c, j + MINERALS_RADIUS + 1); l++) {
							if (Math.random() < 0.1) {
								terrain[k][l] = Tile.MINERALS;
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {

				//Implement this way for now because we might want to have already generated mountains 
				//influence existing ones

				if (marks[i][j] == Tile.BLOCKED) {
					//bfs

					final int IINF = 1000000000;

					int[][] dist = new int[r][c];
					int[][][] par = new int[r][c][2];

					dist[i][j] = 0;
					par[i][j] = new int[]{-1, -1};

					LinkedList<int[]> q = new LinkedList<int[]>();

					q.add(new int[]{i, j});
					//q.push({i, j});

					while (q.size() > 0) {
						int[] cur = q.pop();

						for (int k = 0; k < 8; k++) {
							int newx = cur[0] + move_x[k];
							int newy = cur[1] + move_y[k];

							if (newx < 0 || newx >= r || newy < 0 || newy >= c) continue;
							if (dist[newx][newy] < IINF) continue;

							dist[newx][newy] = dist[cur[0]][cur[1]] + 1;
							par[newx][newy] = cur;
							q.add(new int[]{newx, newy});
						}
					}

					//Draw connection in the real graph

					//Iterate in this manner so that we don't re-draw mountains twice 

					for (int k = i; k < r; k++) {
						for (int l = (k == i ? j : 0); l < c; l++) {
							if (marks[k][l] == Tile.BLOCKED && dist[k][l] <= BLOCKED_THRESHOLD) {

								//We are close enough to draw the connection

								int curx = k, cury = l;

								while (curx != -1) {

									//Mark it in the real graph
									terrain[curx][cury] = Tile.BLOCKED;

									int[] thisPar = par[curx][cury];
									curx = thisPar[0];
									cury = thisPar[1];
								}
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < BLOCKED_WIDTH; i++) {
			for (int j = 0; j < r; j++) {
				for (int k = 0; k < c; k++) {
					for (int l = 0; l < 4; l++) { //Only 4 directly adjacents (no king diagonals)
						int newx = j + move_x[l];
						int newy = k + move_y[l];

						if (newx < 0 || newx >= r || newy < 0 || newy >= c) continue;

						if (terrain[newx][newy] == Tile.BLOCKED) {
							terrain[j][k] = Tile.MARKER;
						}
					}
				}
			}

			for (int j = 0; j < r; j++) {
				for (int k = 0; k < c; k++) {
					if (terrain[j][k] == Tile.MARKER) {
						terrain[j][k] = Tile.BLOCKED;
					}
				}
			}
		}

		//Fill in remainder with clear

		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				if (terrain[i][j] == Tile.UNKNOWN) {
					terrain[i][j] = Tile.CLEAR;
				}
			}
		}
	}

	public GameMap slice(int row, int col, int visRadius, int updateTime) {
		GameMap result = new GameMap(r, c);

		for (int i = Math.max(0, row - visRadius); i <= Math.min(r - 1, row + visRadius); i++) {
			for (int j = Math.max(0, col - visRadius); j <= Math.min(c - 1, col + visRadius); j++) {
				// Later we can add a circular check or something, for now just use square xd

				result.terrain[i][j] = terrain[i][j];
				result.mobileUnits[i][j] = mobileUnits[i][j];
				result.lastUpdated[i][j] = updateTime;

			}
		}

		return result;
	}

	private static void updateUnits(Unit[][] us, Unit[][] other, int[][] usLastUpdated, int[][] otherLastUpdated) {
		HashMap<Unit, Integer> latestSeen = new HashMap<Unit, Integer>(); 
		//Stores the last time we saw a certain unit. This way we'll only keep the most recent seen.
		int r = us.length; int c = us[0].length;
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				if (us[i][j] != null && us[i][j].isValid()) {
					int thismax = Math.max(usLastUpdated[i][j], latestSeen.getOrDefault(us[i][j], 0));
					
					latestSeen.put(us[i][j], thismax);
				}
				
				if (other[i][j] != null && other[i][j].isValid()) {
					int thismax = Math.max(otherLastUpdated[i][j], latestSeen.getOrDefault(other[i][j], 0));
					
					latestSeen.put(other[i][j], thismax);
				}
			}
		}
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				if (us[i][j] != null && us[i][j].isValid() && usLastUpdated[i][j] < latestSeen.get(us[i][j])) {
					us[i][j] = null;
				}
				
				if (other[i][j] != null && other[i][j].isValid() && otherLastUpdated[i][j] == latestSeen.get(other[i][j])) {
					us[i][j] = other[i][j];
				}
			}
		}
	}

	public void updateKnowledge(GameMap other) {

		//Basically take all the info from ourselves and the other and update. So for example if we 
		//have more info, then we stay with that info, but if maybe on the other side of the map our capital
		//has learned something new, we get the goody goody. Helps with pathfinding and such.
		
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				if (terrain[i][j] == Tile.UNKNOWN && other.terrain[i][j] != Tile.UNKNOWN) {
					terrain[i][j] = other.terrain[i][j];
				}
			}
		}

		updateUnits(mobileUnits, other.mobileUnits, lastUpdated, other.lastUpdated);
		updateUnits(staticUnits, other.staticUnits, lastUpdated, other.lastUpdated);
	}

}
