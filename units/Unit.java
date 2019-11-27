package units;

import actions.Action;
import game_manager.GameManager;
import game_map.GameMap;
import orders.Order;

public abstract class Unit {
	private GameMap known;	//the GameMap that's known to this unit
	private int team;		//which player controls this unit
	private int id, x, y;
	private boolean valid = true;
	private Order currentOrder; 	//
	
	public abstract void processPassiveEffects(GameManager m);
	
	public Unit(int team, int id, int i, int j, GameMap k) {
		x = i; y = j; this.team = team; this.id = id; known = k;
	}
	
	public GameMap getKnown() {
		return known;
	}

	public boolean isValid() {
		return valid;
	}
	
	public void invalidate() {
		valid = false;
	}
	
	@Override
	public String toString() {
		return team + " " + id + " " + x + " " + y;
	}
	
	public Action getAction() {
		return currentOrder.execute(this);
	}
	
	public void setOrder(Order o) {
		currentOrder = o;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Unit)) return false;
		
		return ((Unit)o).id == id;
	}
}
