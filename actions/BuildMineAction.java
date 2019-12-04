package actions;

import game_manager.GameManager;
import game_map.GameMap;
import game_map.Tile;
import units.Mine;
import units.Unit;

public class BuildMineAction extends BuildStaticUnitAction {

	public BuildMineAction() {
		setRequiredMinerals(15);
	}
	

	public boolean validate(Unit us, GameManager m) {
		if (!super.validate(us, m)) return false;
		
		return us.getKnown().getTerrain()[us.getX()][us.getY()] == Tile.MINERALS;
	}
	
	@Override
	public void execute(Unit us, GameManager m) {
		if (!validate(us, m)) return;
		super.execute(us, m);
		
		GameMap known = m.getOmnimap();
		known.getStaticUnits()[us.getX()][us.getY()] = 
				new Mine(us.getTeam(), m.getCurrentUnitCounter(), us.getX(), us.getY(), us.getKnown().clone());
		m.setCurrentUnitCounter(m.getCurrentUnitCounter() + 1);
	}
}
