package actions;

import game_manager.GameManager;
import game_map.GameMap;
import units.Mine;
import units.Unit;

public class BuildMineAction extends BuildStaticUnitAction {

	public BuildMineAction() {
		setRequiredMinerals(15);
	}
	

	@Override
	public void execute(Unit us, GameManager m) {
		if (!super.validate(us, m)) return;
		super.execute(us, m);
		
		GameMap known = m.getOmnimap();
		known.getStaticUnits()[us.getX()][us.getY()] = 
				new Mine(us.getTeam(), m.getCurrentUnitCounter(), us.getX(), us.getY(), known);
	}
}
