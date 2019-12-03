
package game_manager;

public class ResourceDelta { //small helper class to manage change in resources on each turn
	
	private double food, minerals, wealth;
	
	public ResourceDelta(double f, double m, double w) {
		food = f; minerals = m; wealth = w;
	}
	
	public ResourceDelta add(ResourceDelta other) {
		food += other.food; minerals += other.minerals; wealth += other.wealth;
		return this;
		//return new ResourceDelta(food + other.food, minerals + other.minerals, wealth + other.wealth);
	}
	
	public void apply(Player p) {
		p.setFood(p.getFood() + food);
		p.setMinerals(p.getMinerals() + minerals);
		p.setWealth(p.getWealth() +		 wealth);
	}

	public double getFood() {
		return food;
	}

	public void setFood(double food) {
		this.food = food;
	}

	public double getMinerals() {
		return minerals;
	}

	public void setMinerals(double minerals) {
		this.minerals = minerals;
	}

	public double getWealth() {
		return wealth;
	}

	public void setWealth(double wealth) {
		this.wealth = wealth;
	}
}
