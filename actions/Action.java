package actions;

import game_manager.GameManager;
import game_map.GameMap;
import units.MobileUnit;
import units.StaticUnit;
import units.Unit;

public abstract class Action {
	Unit us;
	boolean valid = true;
	
	public Action(Unit u) { us = u; }
	
	private Unit findUnit(String s, GameManager m) {
		int id = 0;
		
		try {
			id = Integer.parseInt(s.split(" ")[0]);
		} catch (Exception e) {
			valid = false;
			return null;
		}
		
		GameMap known = m.getOmnimap();
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		StaticUnit[][] staticUnits = known.getStaticUnits();
		
		for (int i = 0; i < known.getR(); i++) {
			for (int j = 0; j < known.getC(); j++) {
				if (mobileUnits[i][j] != null && mobileUnits[i][j].isValid() && mobileUnits[i][j].getId() == id) {
					//us = mobileUnits[i][j];
					return mobileUnits[i][j];
				}
				
				if (staticUnits[i][j] != null && staticUnits[i][j].isValid() && staticUnits[i][j].getId() == id) {
					//us = staticUnits[i][j];
					return staticUnits[i][j];
				}
			}
		}
		
		valid = false;
		
		return null;
	}
	public Action(String s, GameManager m) {
		//Look for the unit with the same ID as specified in the string
		
		us = findUnit(s, m);
	}
	
	public boolean validate(GameManager m) {
		return valid && us != null;
	}
	
	public abstract void execute(GameManager m);
	
}
