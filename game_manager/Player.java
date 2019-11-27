package game_manager;

import game_map.GameMap;

public class Player {
	GameMap known;
	
	private double food, minerals, wealth;
	
	private double wealthMultiplier = 1, combatMultiplier = 1, mineralsMultiplier = 1, foodMultiplier = 1;
	
	boolean lackingResources = false;
	
	private void checkResources() { //If we are low on resources, apply combat/wealth penalty
		if (food < 0 || minerals < 0 || wealth < 0) {
			if (!lackingResources) {
				lackingResources = true;
				combatMultiplier *= 0.5;
				wealthMultiplier *= 0.75;
			}
		} else {
			if (lackingResources) {
				lackingResources = false;
				combatMultiplier *= 2;
				wealthMultiplier *= 1/0.75;
			}
		}
	}
	public double getMineralsMultiplier() {
		checkResources();
		return mineralsMultiplier;
	}

	public void setMineralsMultiplier(double mineralsMultiplier) {
		checkResources();
		this.mineralsMultiplier = mineralsMultiplier;
	}

	public double getFoodMultiplier() {
		checkResources();
		return foodMultiplier;
	}

	public void setFoodMultiplier(double foodMultiplier) {
		checkResources();
		this.foodMultiplier = foodMultiplier;
	}

	public Player(double f, double m, double w) {
		food = f; minerals = m; wealth = w;
	}

	public double getWealthMultiplier() {
		checkResources();
		return wealthMultiplier;
	}

	public void setWealthMultiplier(double wealthMultiplier) {
		checkResources();
		this.wealthMultiplier = wealthMultiplier;
	}

	public double getCombatMultiplier() {
		checkResources();
		return combatMultiplier;
	}

	public void setCombatMultiplier(double combatMultiplier) {
		checkResources();
		this.combatMultiplier = combatMultiplier;
	}

	public GameMap getKnown() {
		return known;
	}

	public void setKnown(GameMap known) {
		this.known = known;
	}

	public double getFood() {
		checkResources();
		return food;
	}

	public void setFood(double food) {
		this.food = food;
		checkResources();
	}

	public double getMinerals() {
		checkResources();
		return minerals;
	}

	public void setMinerals(double minerals) {
		this.minerals = minerals;
		checkResources();
	}

	public double getWealth() {
		checkResources();
		return wealth;
	}

	public void setWealth(double wealth) {
		this.wealth = wealth;
		checkResources();
	}
}
