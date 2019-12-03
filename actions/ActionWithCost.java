package actions;

import game_manager.GameManager;
import game_manager.Player;
import units.Unit;

public abstract class ActionWithCost extends Action {

	private double requiredWealth = 0;
	private double requiredMinerals = 0;
	private double requiredFood = 0;

	public boolean validate(Unit us, GameManager m) {
		if (!super.validate(us, m)) return false;

		if (us.getTeam() >= m.getNumPlayers()) return false;
		Player owner = m.getPlayers()[us.getTeam()];
		
		if ((requiredWealth > 0 && owner.getWealth() < requiredWealth) || 
				(requiredMinerals > 0 && owner.getMinerals() < requiredMinerals) 
				|| (requiredFood > 0 && owner.getFood() < requiredFood)) return false;

		return true;
	}
	
	@Override
	public void execute(Unit us, GameManager m) {
		if (!validate(us, m)) return;
		
		Player owner = m.getPlayers()[us.getTeam()];
		
		owner.setWealth(owner.getWealth() - requiredWealth);
		owner.setMinerals(owner.getMinerals() - requiredMinerals);
		owner.setFood(owner.getFood() - requiredFood);
	}

	public double getRequiredWealth() {
		return requiredWealth;
	}

	public void setRequiredWealth(double requiredWealth) {
		this.requiredWealth = requiredWealth;
	}

	public double getRequiredMinerals() {
		return requiredMinerals;
	}

	public void setRequiredMinerals(double requiredMinerals) {
		this.requiredMinerals = requiredMinerals;
	}

	public double getRequiredFood() {
		return requiredFood;
	}

	public void setRequiredFood(double requiredFood) {
		this.requiredFood = requiredFood;
	}
}
