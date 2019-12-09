package actions;

import game_manager.GameManager;
import units.Unit;

public class DoNothingAction extends Action {

	@Override
	public boolean validate(Unit us, GameManager m) {
		return true;
	}

	@Override
	public void execute(Unit us, GameManager m) {
	}

}
