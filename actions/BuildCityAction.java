package actions;

import game_manager.GameManager;
import game_map.GameMap;
import units.City;
import units.Unit;

public class BuildCityAction extends BuildStaticUnitAction {

	public BuildCityAction() {
		setRequiredFood(10);
		setRequiredMinerals(50);
		setRequiredWealth(50);
	}
	

	@Override
	public void execute(Unit us, GameManager m) {
		if (!super.validate(us, m)) return;
		super.execute(us, m);
		
		GameMap known = m.getOmnimap();
		known.getStaticUnits()[us.getX()][us.getY()] = 
				new City(us.getTeam(), m.getCurrentUnitCounter(), us.getX(), us.getY(), known);
	}

}
