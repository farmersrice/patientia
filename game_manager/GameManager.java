package game_manager;

import game_map.GameMap;

public class GameManager {
	private int numPlayers;
	private Player[] players;
	private GameMap omnimap;
	private int currentUnitCounter = 0;
	
	GameManager(int rows, int cols, int numPlayers) {
		omnimap = new GameMap(rows, cols);
		omnimap.generate();
		
		this.numPlayers = numPlayers;
		
		players = new Player[numPlayers];
		
		players[0] = new Player(100, 100, 100);
	}
	
	public void turn() {
		
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
