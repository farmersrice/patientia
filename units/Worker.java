package units;

import game_manager.GameManager;
import game_manager.Player;
import game_manager.ResourceDelta;
import game_map.GameMap;

public class Worker extends MobileUnit {

	public Worker(int team, int id, int i, int j, GameMap k) {
		super(team, id, i, j, k);
	}
	
	public String toString() {
		return "Worker";
	}

	@Override
	public ResourceDelta getResourceDelta(Player owner) {
		return new ResourceDelta(-1, 0, -1);
	}
}
