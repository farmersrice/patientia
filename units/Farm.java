package units;

import game_manager.GameManager;
import game_manager.Player;
import game_map.GameMap;

public class Farm extends StaticUnit {

	public Farm(int team, int id, int i, int j, GameMap k) {
		super(team, id, i, j, k);
	}

	@Override
	public void processPassiveEffects(GameManager m) {
		if (getTeam() > m.getNumPlayers()) return; //we can do this in order to place unowned farms around the map, for example
		
		Player owner = m.getPlayers()[getTeam()];
		owner.setFood(owner.getFood() + owner.getFoodMultiplier());
	}
	
	public String toString() {
		return "Farm";
	}
}
