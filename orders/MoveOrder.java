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
	
	public boolean isComplete(Unit us) {
		
		
		boolean curComplete = super.isComplete(us) && us.getX() == tx && us.getY() == ty;
		
		if (curComplete) return true;
		
		int[] nextStep = Algorithms.moveTowards(us.getKnown(), us.getX(), us.getY(), tx, ty, us);
		if (nextStep[0] == -1) curComplete = true;

		setComplete(curComplete); //so we don't spam compute moveTowards
		return curComplete;
	}

	@Override
	public Action execute(Unit us) {
		
		if (us.getX() == tx && us.getY() == ty) {
			setComplete(true);
			return new DoNothingAction();
		}
		
		int[] nextStep = Algorithms.moveTowards(us.getKnown(), us.getX(), us.getY(), tx, ty, us);
		return new MoveAction(nextStep[0], nextStep[1]);
	}
	
	@Override
	public int expectedCompletionTime(Unit us) {
		return (Algorithms.kingBFS(us.getKnown(), tx, ty, us)).getDist()[us.getX()][us.getY()];
	}

	public int getTx() {
		return tx;
	}


	public int getTy() {
		return ty;
	}

	public String toString() {
		return "Move to " + tx + " " + ty;
	}

}
