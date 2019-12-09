package orders;

import actions.Action;
import actions.TogglePopulationControlsAction;
import units.Unit;

public class TogglePopulationControlsOrder extends Order {

	@Override
	public Action execute(Unit us) {
		setComplete(true);

		return new TogglePopulationControlsAction();
	}
	
	public String toString() {
		return "Toggle pop controls";
	}
	
}
