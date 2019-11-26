package actions;

import game_manager.GameManager;
import units.Unit;

public abstract class Action {
	Unit us;
	
	public Action(Unit u) { us = u; }
	public Action(String s, GameManager m) {};
	
	public abstract boolean validate(GameManager m);
	public abstract boolean execute(GameManager m);
	
}
