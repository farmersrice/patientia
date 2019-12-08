package ai;

import java.util.ArrayList;

import game_manager.OutstandingOrder;
import game_manager.Player;
import game_manager.ResourceDelta;
import game_map.GameMap;
import orders.TogglePopulationControlsOrder;
import units.City;
import units.Unit;

public class FirstAI implements AIInterface {

	//private double buildWeightCity = 
	
	
	private ArrayList<Unit> getUnits(Player us) {
		GameMap known = us.getKnown();
		
		ArrayList<Unit> answer = new ArrayList<Unit>();
		for (int i = 0; i < known.getR(); i++) {
			for (int j = 0; j < known.getC(); j++) {
				if (known.getMobileUnits()[i][j] != null) {
					answer.add(known.getMobileUnits()[i][j]);
				}
				if (known.getStaticUnits()[i][j] != null) {
					answer.add(known.getStaticUnits()[i][j]);
				}
			}
		}
		return answer;
	}
	
	@Override
	public ArrayList<OutstandingOrder> think(Player us, int time) {
		
		//Compute deltas
		
		ResourceDelta delta = us.getExpectedDelta();

		ArrayList<Unit> units = getUnits(us);
		
		ArrayList<OutstandingOrder> answer = new ArrayList<OutstandingOrder>();
		
		for (Unit u : units) {
			if (u instanceof City) {
				//Once any city is over 20 pops, stop growth
				City city = (City) u;
				if (city.getPopulation() > 20) {
					//Check if it's not enacted
					if (!city.isPopulationControlsEnacted()) {
						answer.add(new OutstandingOrder(city, time, new TogglePopulationControlsOrder(), true));
					}
				} else if (city.isPopulationControlsEnacted()) {
					answer.add(new OutstandingOrder(city, time, new TogglePopulationControlsOrder(), true));
				}
			}
		}
		
		//Check how many soldiers we have and where they are
		
		
		
		return null;
	}

}
