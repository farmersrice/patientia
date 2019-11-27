package orders;

import actions.Action;
import actions.MoveAction;
import units.Unit;
import utilities.Algorithms;

public class MoveOrder extends Order {
	
	private int tx, ty;
	
	public MoveOrder(Unit u, int tx, int ty) {
		super(u);
		this.tx = tx; this.ty = ty;
	}

	@Override
	public Action execute() {
		int[] nextStep = Algorithms.moveTowards(us.getKnown(), us.getX(), us.getY(), tx, ty, us);
		return new MoveAction(us, nextStep[0], nextStep[1]);
	}

}
