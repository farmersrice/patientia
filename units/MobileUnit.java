package units;

import game_map.GameMap;

public abstract class MobileUnit extends Unit {

	public MobileUnit(int team, int id, int i, int j, GameMap k) {
		super(team, id, i, j, k);
	}

}
