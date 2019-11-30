package game_manager;

import game_map.GameMap;

public class Player {
	private GameMap known;
	private final int PREV_STORED = 50;
	private GameMap[] prevKnown = new GameMap[PREV_STORED]; //Store the last 50 gamemaps -- hopefully nobody exceeds diameter 250 for now
	//Obviously we will calculate dynamically, but that's for later
	//This is used to sync info to the troops
	
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
	
	public GameMap getPrevKnown(int backTurns) {
		if (backTurns <= 0) return known;
		return prevKnown[backTurns - 1];
	}
	
	public void addKnown() {
		for (int i = PREV_STORED - 1; i >= 1; i--) {
			prevKnown[i] = prevKnown[i - 1];
		}
		prevKnown[0] = known.clone(); //If we didn't clone we would be stuck with everything pointing to the same map
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
