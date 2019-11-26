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
}
