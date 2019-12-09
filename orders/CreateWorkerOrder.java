package orders;

import actions.Action;
import actions.CreateWorkerAction;
import units.MobileUnit;
import units.Unit;

public class CreateWorkerOrder extends Order {
	
	public boolean isComplete(Unit us) {
		MobileUnit mobileUnit = us.getKnown().getMobileUnits()[us.getX()][us.getY()];
		return super.isComplete(us) || (mobileUnit != null && mobileUnit.isValid());
	}
	
	@Override
	public Action execute(Unit us) {
		// TODO Auto-generated method stub
		return new CreateWorkerAction();
	}

	public String toString() {
		return "Create worker";
	}
}
