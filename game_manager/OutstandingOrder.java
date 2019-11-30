package game_manager;

import orders.Order;
import units.Unit;

public class OutstandingOrder {
	private Order order;
	private int timeIssued;
	private Unit target;
	
	boolean set;
	
	public OutstandingOrder(Unit target, int timeIssued, Order order, boolean set) {
		this.target = target; this.timeIssued = timeIssued; this.order = order; this.set = set;
	}

	public boolean isSet() {
		return set;
	}

	public void setSet(boolean set) {
		this.set = set;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public int getTimeIssued() {
		return timeIssued;
	}

	public void setTimeIssued(int timeIssued) {
		this.timeIssued = timeIssued;
	}

	public Unit getTarget() {
		return target;
	}

	public void setTarget(Unit target) {
		this.target = target;
	}
	
	
}
