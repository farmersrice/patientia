package units;

import game_manager.GameManager;
import game_manager.Player;
import game_manager.ResourceDelta;
import game_map.GameMap;

public class Farm extends StaticUnit {

	public Farm(int team, int id, int i, int j, GameMap k) {
		super(team, id, i, j, k);
	}
	
	public ResourceDelta getResourceDelta(Player owner) {
		return new ResourceDelta(owner.getFoodMultiplier(), 0, 0);
	}
	
	public String toString() {
		return "Farm";
	}
}
