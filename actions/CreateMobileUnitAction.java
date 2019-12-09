package actions;

import game_manager.GameManager;
import game_map.GameMap;
import units.City;
import units.MobileUnit;
import units.Unit;

public abstract class CreateMobileUnitAction extends ActionWithCost {

	private double requiredPopulation = 0;

	public boolean validate(Unit us, GameManager m) {
		// TODO Auto-generated method stub
		if (!super.validate(us, m)) return false;
		
		GameMap known = m.getOmnimap();
		
		if (!(us instanceof City)) return false; //Only cities can spawn soldiers
		
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		
		int x = us.getX(); int y = us.getY();
		MobileUnit occupant = mobileUnits[x][y];
		
		if (occupant != null && occupant.isValid()) return false; //can't spawn if something's already there
		
		if (((City)us).getPopulation() < requiredPopulation) return false;
		
		return true;
	}
	
	@Override
	public void execute(Unit us, GameManager m) {
		if (!validate(us, m)) return;
		super.execute(us, m);
		((City)us).setPopulation(((City)us).getPopulation() - requiredPopulation);
	}

	public double getRequiredPopulation() {
		return requiredPopulation;
	}

	public void setRequiredPopulation(double requiredPopulation) {
		this.requiredPopulation = requiredPopulation;
	}
}
