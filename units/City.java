package units;

import java.text.DecimalFormat;

import game_manager.GameManager;
import game_manager.Player;
import game_map.GameMap;

public class City extends StaticUnit {

	private boolean populationControlsEnacted = false; //stops population growth, but also gives double food upkeep and half wealth output
	private double population = 1;
	
	public boolean isPopulationControlsEnacted() {
		return populationControlsEnacted;
	}

	public void setPopulationControlsEnacted(boolean populationControlsEnacted) {
		this.populationControlsEnacted = populationControlsEnacted;
	}

	public double getPopulation() {
		return population;
	}

	public void setPopulation(double population) {
		this.population = population;
	}

	public City(int team, int id, int i, int j, GameMap k) {
		super(team, id, i, j, k);
	}

	public void processPassiveEffects(GameManager m) {
		if (!populationControlsEnacted) population *= 1.05;
		
		if (getTeam() > m.getNumPlayers()) return;
		
		Player owner = m.getPlayers()[getTeam()];
		owner.setFood(owner.getFood() - population * (populationControlsEnacted ? 2 : 1));
		owner.setWealth(owner.getWealth() + population * owner.getWealthMultiplier() * (populationControlsEnacted ? 0.5 : 1));
	}

	public String toString() {
		return "City, pop. " +  new DecimalFormat("#.##").format(population) + ", pop controls: " + populationControlsEnacted;
	}
}
