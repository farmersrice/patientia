package actions;

import game_manager.GameManager;
import game_map.GameMap;
import units.MobileUnit;
import units.Soldier;
import units.Unit;

public class CreateSoldierAction extends CreateMobileUnitAction {

	double numSoldiers;
	
	public CreateSoldierAction(double numSoldiers) {
		this.numSoldiers = numSoldiers;
		setRequiredWealth(numSoldiers * 5);
		setRequiredMinerals(numSoldiers * 5);
		setRequiredPopulation(numSoldiers);
	}

	@Override
	public void execute(Unit us, GameManager m) {
		if (!validate(us, m)) return;
		super.execute(us, m);
		
		
		GameMap known = m.getOmnimap();
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		
		int x = us.getX(); int y = us.getY();
		
		mobileUnits[x][y] = new Soldier(us.getTeam(), m.getCurrentUnitCounter(), x, y, us.getKnown(),
				numSoldiers, m.getPlayers()[us.getTeam()].getCombatMultiplier(), 0);
		m.setCurrentUnitCounter(m.getCurrentUnitCounter() + 1);
	}

}
