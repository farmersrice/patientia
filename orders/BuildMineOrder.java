package orders;

import actions.Action;
import actions.BuildMineAction;
import units.StaticUnit;
import units.Unit;

public class BuildMineOrder extends Order {

	
	public boolean isComplete(Unit us) {
		StaticUnit staticUnit = us.getKnown().getStaticUnits()[us.getX()][us.getY()];
		return super.isComplete(us) || (staticUnit != null && staticUnit.isValid());
	}
	
	@Override
	public Action execute(Unit us) {
		// TODO Auto-generated method stub
		return new BuildMineAction();
	}

	public String toString() {
		return "Build mine";
	}
}
