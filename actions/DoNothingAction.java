package actions;

import game_manager.GameManager;
import units.Unit;

public class DoNothingAction extends Action {

	public DoNothingAction(Unit u) {
		super(u);
	}
	
	public DoNothingAction(String s, GameManager m) {
		super(s, m);
	}

	@Override
	public boolean validate(GameManager m) {
		return true;
	}

	@Override
	public void execute(GameManager m) {
	}

}
