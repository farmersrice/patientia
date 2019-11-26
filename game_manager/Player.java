package game_manager;

import game_map.GameMap;

public class Player {
	GameMap known;
	
	private double food, minerals, wealth;
	
	public Player(double f, double m, double w) {
		food = f; minerals = m; wealth = w;
	}
}
