package orders;

import actions.Action;
import units.Unit;

public abstract class Order {
	
	private boolean complete = false; //Did we finish the order?
	
	public boolean isComplete(Unit us) {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}


	public abstract Action execute(Unit us);
	
	public int expectedCompletionTime(Unit us) {
		return 1;
	}
}
