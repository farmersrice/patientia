package actions;

import game_manager.GameManager;
import game_map.GameMap;
import units.Farm;
import units.Unit;

public class BuildFarmAction extends BuildStaticUnitAction {

	public BuildFarmAction() {
		setRequiredMinerals(10);
	}
	

	@Override
	public void execute(Unit us, GameManager m) {
		if (!super.validate(us, m)) return;
		super.execute(us, m);
		
		GameMap known = m.getOmnimap();
		known.getStaticUnits()[us.getX()][us.getY()] = 
				new Farm(us.getTeam(), m.getCurrentUnitCounter(), us.getX(), us.getY(), us.getKnown().clone());
	}
}
