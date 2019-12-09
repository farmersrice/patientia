package actions;

import game_manager.GameManager;
import units.City;
import units.Unit;

public class TogglePopulationControlsAction extends Action {

	public boolean validate(Unit us, GameManager m) {
		super.validate(us, m);
		return us instanceof City;
	}
	
	@Override
	public void execute(Unit us, GameManager m) {
		if (!validate(us, m)) return;
		
		((City)us).togglePopulationControls();
	}

}
