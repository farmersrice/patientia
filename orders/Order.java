package orders;

import actions.Action;
import units.Unit;

public abstract class Order {
	Unit us;
	
	public Order(Unit u) { us = u; }

	public abstract Action execute();
}
