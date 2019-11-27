package actions;

import game_manager.GameManager;
import game_map.GameMap;
import units.MobileUnit;
import units.Unit;
import units.Worker;

public class CreateWorkerAction extends CreateMobileUnitAction {

	public CreateWorkerAction() {
		setRequiredWealth(2);
		setRequiredMinerals(1);
		setRequiredPopulation(1);
	}

	@Override
	public void execute(Unit us, GameManager m) {
		if (!validate(us, m)) return;
		super.execute(us, m);
		
		
		GameMap known = m.getOmnimap();
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		
		int x = us.getX(); int y = us.getY();
		
		mobileUnits[x][y] = new Worker(us.getTeam(), m.getCurrentUnitCounter(), x, y, us.getKnown());
		m.setCurrentUnitCounter(m.getCurrentUnitCounter() + 1);
	}

}
