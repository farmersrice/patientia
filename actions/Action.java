package actions;

import game_manager.GameManager;
import units.Unit;

public abstract class Action {
	boolean valid = true;
	
	public boolean validate(Unit us, GameManager m) {
		return valid && us != null && us.isValid();
	}
	
	public abstract void execute(Unit us, GameManager m);
	
}
