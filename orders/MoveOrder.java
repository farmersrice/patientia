package orders;

import actions.Action;
import actions.DoNothingAction;
import actions.MoveAction;
import units.Unit;
import utilities.Algorithms;

public class MoveOrder extends Order {
	
	private int tx, ty;
	
	public MoveOrder(int tx, int ty) {
		this.tx = tx; this.ty = ty;
	}

	@Override
	public Action execute(Unit us) {
		//Check for completion first
		
		if (us.getX() == tx && us.getY() == ty) {
			setComplete(true);
			return new DoNothingAction();
		}
		
		int[] nextStep = Algorithms.moveTowards(us.getKnown(), us.getX(), us.getY(), tx, ty, us);
		return new MoveAction(nextStep[0], nextStep[1]);
	}

}
