package orders;

import actions.Action;
import actions.CreateSoldierAction;
import units.MobileUnit;
import units.Unit;

public class CreateSoldierOrder extends Order {

	private double soldiers;
	
	public CreateSoldierOrder(double soldiers) {
		this.soldiers = soldiers;
	}
	
	public boolean isComplete(Unit us) {
		MobileUnit mobileUnit = us.getKnown().getMobileUnits()[us.getX()][us.getY()];
		return super.isComplete(us) || (mobileUnit != null && mobileUnit.isValid());
	}
	
	@Override
	public Action execute(Unit us) {
		// TODO Auto-generated method stub
		return new CreateSoldierAction(soldiers);
	}

	public String toString() {
		return "Create " + soldiers + " soldiers";
	}
}
