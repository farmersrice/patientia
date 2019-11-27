package units;

import game_manager.GameManager;
import game_manager.Player;
import game_map.GameMap;

public class Soldier extends MobileUnit {

	private double soldiers, multiplier, experience;
	
	public final int VISIBLE_RADIUS = 2;
	
	public Soldier(int team, int id, int i, int j, GameMap k, double s, double m, double e) {
		super(team, id, i, j, k);
		soldiers = s; multiplier = m; experience = e;
		// TODO Auto-generated constructor stub
	}

	public double getStrengthPerSoldier() {
		return multiplier * (1 + experience / 100);
	}
	
	public double getStrength() {
		return soldiers * getStrengthPerSoldier();
	}
	
	public void takeDamage(double opponentStrength) {
		double strengthPerSoldier = getStrengthPerSoldier();
		
		double killed = opponentStrength / strengthPerSoldier;
		
		soldiers -= killed;
		
		if (soldiers <= 0) {
			invalidate();
		}
	}
	
	public void mergeInto(Soldier other) {
		other.setExperience((experience * soldiers + other.getExperience() * other.getSoldiers()) / (soldiers + other.getSoldiers()));
		other.setSoldiers(other.getSoldiers() + soldiers);
		
		invalidate();
	}
	
	public double getSoldiers() {
		return soldiers;
	}

	public void setSoldiers(double soldiers) {
		this.soldiers = soldiers;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public double getExperience() {
		return experience;
	}

	public void setExperience(double experience) {
		this.experience = experience;
	}

	@Override
	public void processPassiveEffects(GameManager m) {
		if (getTeam() > m.getNumPlayers()) return; //we can do this in order to place unowned farms around the map, for example
		
		Player owner = m.getPlayers()[getTeam()];
		
		owner.setFood(owner.getFood() - 2 * soldiers);
		owner.setWealth(owner.getWealth() - soldiers);
		
	}
}
