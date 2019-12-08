package units;

import java.text.DecimalFormat;

import game_manager.GameManager;
import game_manager.Player;
import game_manager.ResourceDelta;
import game_map.GameMap;

public class City extends StaticUnit {

	private boolean populationControlsEnacted = false; //stops population growth, but also gives double food upkeep and half wealth output
	private double population = 1;
	
	public boolean isPopulationControlsEnacted() {
		return populationControlsEnacted;
	}
	
	public void togglePopulationControls() {
		populationControlsEnacted = !populationControlsEnacted;
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
		if (getTeam() > m.getNumPlayers()) return;
	
		Player owner = m.getPlayers()[getTeam()];
		getResourceDelta(owner).apply(owner);
		
		if (!populationControlsEnacted) population *= 1.05;
	}

	public String toString() {
		return "City, pop: " +  new DecimalFormat("#.##").format(population) + ", pop controls: " + populationControlsEnacted;
	}

	@Override
	public ResourceDelta getResourceDelta(Player owner) {
		return new ResourceDelta(-population * (populationControlsEnacted ? 2 : 1), 0, population * owner.getWealthMultiplier() * (populationControlsEnacted ? 0.5 : 1));
	}
}
