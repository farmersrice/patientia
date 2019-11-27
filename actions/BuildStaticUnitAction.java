package actions;

import game_manager.GameManager;
import game_manager.Player;
import game_map.GameMap;
import units.City;
import units.MobileUnit;
import units.StaticUnit;
import units.Unit;
import units.Worker;

public abstract class BuildStaticUnitAction extends ActionWithCost {

	public boolean validate(Unit us, GameManager m) {
		if (!super.validate(us, m)) return false;
		
		GameMap known = m.getOmnimap();
		
		if (!(us instanceof Worker)) return false; //Only workers can build stuff
		
		StaticUnit[][] staticUnits = known.getStaticUnits();
		
		int x = us.getX(); int y = us.getY();
		StaticUnit occupant = staticUnits[x][y];
		
		if (occupant != null && occupant.isValid()) return false; //can't spawn if something's already there

		return true;
	}
}
