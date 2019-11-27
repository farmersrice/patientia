package actions;

import game_manager.GameManager;
import game_map.GameMap;
import units.MobileUnit;
import units.StaticUnit;
import units.Unit;

public abstract class Action {
	boolean valid = true;
	
	public boolean validate(Unit us, GameManager m) {
		return valid && us != null;
	}
	
	public abstract void execute(Unit us, GameManager m);
	
}
