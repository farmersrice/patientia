package orders;

import actions.Action;
import actions.BuildFarmAction;
import units.StaticUnit;
import units.Unit;

public class BuildFarmOrder extends Order {

	
	public boolean isComplete(Unit us) {
		StaticUnit staticUnit = us.getKnown().getStaticUnits()[us.getX()][us.getY()];
		return super.isComplete(us) || (staticUnit != null && staticUnit.isValid());
	}
	
	@Override
	public Action execute(Unit us) {
		// TODO Auto-generated method stub
		return new BuildFarmAction();
	}

	public String toString() {
		return "Build farm";
	}
}
