package orders;

import actions.Action;
import units.Unit;

public abstract class Order {
	public abstract Action execute(Unit us);
}
