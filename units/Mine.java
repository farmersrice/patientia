package units;

import game_manager.GameManager;
import game_manager.Player;
import game_map.GameMap;

public class Mine extends StaticUnit {

	public Mine(int team, int id, int i, int j, GameMap k) {
		super(team, id, i, j, k);
	}

	@Override
	public void processStaticTurn(GameManager m) {
		if (getTeam() > m.getNumPlayers()) return; //we can do this in order to place unowned farms around the map, for example
		
		Player owner = m.getPlayers()[getTeam()];
		owner.setMinerals(owner.getMinerals() + owner.getMineralsMultiplier());
	}

}
