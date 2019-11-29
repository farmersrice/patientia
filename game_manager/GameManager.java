package game_manager;

import java.util.ArrayList;
import java.util.Collections;

import game_map.GameMap;
import game_map.Tile;
import orders.MoveOrder;
import units.MobileUnit;
import units.StaticUnit;
import units.Unit;
import units.Worker;

public class GameManager {
	private int numPlayers;
	private Player[] players;
	private GameMap omnimap;
	private int currentUnitCounter = 0;
	private int turnCounter = 0;
	
	public GameManager(int rows, int cols, int numPlayers) {
		omnimap = new GameMap(rows, cols);
		omnimap.generate();
		
		this.numPlayers = numPlayers;
		
		players = new Player[numPlayers];
		
		players[0] = new Player(100, 100, 100);
		
		int px = -1;
		int py = -1;
		outer:
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (omnimap.getTerrain()[i][j] == Tile.CLEAR) {
					//Put our unit here
					omnimap.getMobileUnits()[i][j] = new Worker(0, currentUnitCounter++, i, j, omnimap.slice(i, j, 2, turnCounter));
					px = i; py = j;
					break outer;
				}
			}
		}
		
		outer2:
		for (int i = rows - 1; i >= 0; i--) {
			for (int j = cols - 1; j >= 0; j--) {
				if (omnimap.getTerrain()[i][j] == Tile.CLEAR) {
					omnimap.getMobileUnits()[px][py].addOrder(new MoveOrder(i, j));
					System.out.println("starting is " + px + " " + py + " end is " + i + " " + j);
					break outer2;
				}
			}
		}
		
		
	}
	
	public int getTurnCounter() {
		return turnCounter;
	}

	public void setTurnCounter(int turnCounter) {
		this.turnCounter = turnCounter;
	}

	public void turn() {
		ArrayList<Unit> order = new ArrayList<Unit>();
		
		Unit[][] units1 = omnimap.getMobileUnits();
		Unit[][] units2 = omnimap.getStaticUnits();
		
		for (int i = 0; i < omnimap.getR(); i++) {
			for (int j = 0; j < omnimap.getC(); j++) {
				if (units1[i][j] != null && units1[i][j].isValid()) {
					order.add(units1[i][j]);
				}
				if (units2[i][j] != null && units2[i][j].isValid()) {
					order.add(units2[i][j]);
				}
			}
		}
		
		Collections.shuffle(order);
		
		for (Unit u : order) {
			u.getKnown().updateKnowledge(omnimap.slice(u.getX(), u.getY(), 2, turnCounter));
		}
		
		//In future we want to sync knowledge between soldiers, cities, etc.
		
		System.out.println("turn " + turnCounter);
		for (Unit u : order) {
			if (u.isValid()) {
				u.processPassiveEffects(this);
				u.getAction().execute(u, this);
			}
		}
		
		turnCounter++;
	}
	
	private Unit findUnit(String s) {
		int id = 0;
		
		try {
			id = Integer.parseInt(s.split(" ")[0]);
		} catch (Exception e) {
			return null;
		}
		
		GameMap known = omnimap;
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		StaticUnit[][] staticUnits = known.getStaticUnits();
		
		for (int i = 0; i < known.getR(); i++) {
			for (int j = 0; j < known.getC(); j++) {
				if (mobileUnits[i][j] != null && mobileUnits[i][j].isValid() && mobileUnits[i][j].getId() == id) {
					//us = mobileUnits[i][j];
					return mobileUnits[i][j];
				}
				
				if (staticUnits[i][j] != null && staticUnits[i][j].isValid() && staticUnits[i][j].getId() == id) {
					//us = staticUnits[i][j];
					return staticUnits[i][j];
				}
			}
		}
		return null;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	public Player[] getPlayers() {
		return players;
	}

	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public GameMap getOmnimap() {
		return omnimap;
	}

	public void setOmnimap(GameMap omnimap) {
		this.omnimap = omnimap;
	}

	public int getCurrentUnitCounter() {
		return currentUnitCounter;
	}

	public void setCurrentUnitCounter(int currentUnitCounter) {
		this.currentUnitCounter = currentUnitCounter;
	}
	
	
}
