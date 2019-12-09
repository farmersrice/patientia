package ai;

import java.util.ArrayList;

import game_manager.OutstandingOrder;
import game_manager.Player;
import game_manager.ResourceDelta;
import game_map.GameMap;
import orders.AttackOrder;
import orders.BuildCityOrder;
import orders.BuildFarmOrder;
import orders.BuildMineOrder;
import orders.CreateSoldierOrder;
import orders.CreateWorkerOrder;
import orders.MoveOrder;
import orders.TogglePopulationControlsOrder;
import units.City;
import units.Farm;
import units.Mine;
import units.MobileUnit;
import units.Soldier;
import units.Unit;
import units.Worker;

public class FirstAI implements AIInterface {

	//private double buildWeightCity = 
	
	
	private ArrayList<Unit> getUnits(Player us) {
		GameMap known = us.getKnown();
		
		ArrayList<Unit> answer = new ArrayList<Unit>();
		for (int i = 0; i < known.getR(); i++) {
			for (int j = 0; j < known.getC(); j++) {
				if (known.getMobileUnits()[i][j] != null && known.getMobileUnits()[i][j].getTeam() == us.getTeam()) {
					answer.add(known.getMobileUnits()[i][j]);
				}
				if (known.getStaticUnits()[i][j] != null && known.getStaticUnits()[i][j].getTeam() == us.getTeam()) {
					answer.add(known.getStaticUnits()[i][j]);
				}
			}
		}
		return answer;
	}
	
	private int populationCap = 5;
	
	private int distanceToNearestCity(Player us, int px, int py) {
		GameMap known = us.getKnown();
		
		int distance = 1000000000;
		
		for (int i = 0; i < known.getR(); i++) {
			for (int j = 0; j < known.getC(); j++) {
				Unit occupant = known.getStaticUnits()[i][j];
				
				int dx = px - i;
				int dy = py - j;
				
				if (occupant != null && occupant instanceof City && occupant.getTeam() == us.getTeam()) {
					distance = Math.min(distance, dx * dx + dy * dy);
				}
			}
		}
		return distance;
	}
	
	//it's ok to pass time since this will all be serverside when we go multiplayer
	@Override
	public ArrayList<OutstandingOrder> think(Player us, int time) { 
		
		GameMap known = us.getKnown();
		
		//Compute deltas
		
		ResourceDelta delta = us.getExpectedDelta();

		ArrayList<Unit> units = getUnits(us);
		
		ArrayList<OutstandingOrder> answer = new ArrayList<OutstandingOrder>();
		
		int numCities = 0;
		int numMines = 0, numFarms = 0, numWorkers = 0;
		for (Unit u : units) {
			if (u instanceof City) {
				numCities++;
			}
			
			if (u instanceof Mine) {
				numMines++;
			}
			
			if (u instanceof Farm) {
				numFarms++;
			}
			
			if (u instanceof Worker) {
				numWorkers++;
			}
		}
		
		int requiredMines = 3 * numCities;
		int preferredMines = 5 * numCities;
		
		int requiredAdditionalFarms = (int) (Math.ceil(-delta.getFood()));
		System.out.println("team " + us.getTeam() + " delta is " + delta.toString());
		System.out.println("resources are " + us.getFood() + ' ' + us.getMinerals() + " " + us.getWealth());
		
		
		for (Unit u : units) {
			if (u instanceof Worker) {
				//Build required mines (highest priority)

				OutstandingOrder buildMineOutstandingOrder = new OutstandingOrder(u, time, new BuildMineOrder(), true);
				OutstandingOrder buildFarmOutstandingOrder = new OutstandingOrder(u, time, new BuildFarmOrder(), true);
				
				if (numMines < requiredMines) {
					//Build mines
					answer.add(buildMineOutstandingOrder);
					numMines++;
				} else if (requiredAdditionalFarms > 0) {
					answer.add(buildFarmOutstandingOrder);
					requiredAdditionalFarms--;
				} else if (numMines < preferredMines) {
					answer.add(buildMineOutstandingOrder);
					numMines++;
				} else if (requiredAdditionalFarms <= 0) {
					//Build a city!!!!!!!!!!
					
					//Pick random location until it's within 15 to 25 tiles from the nearest city we have
					
					int[] plannedLocation = {-1, -1};
					
					for (int i = 0; i < 50; i++) {
						int px = (int) (Math.random() * known.getR());
						int py = (int) (Math.random() * known.getC());
						
						if (i == 0) {
							//First, try our location
							px = u.getX();
							py = u.getY();
						}
						
						int distance = distanceToNearestCity(us, px, py);
						
						if (15 * 15 <= distance && distance <= 25 * 25) {
							plannedLocation[0] = px;
							plannedLocation[1] = py;
							break;
						}
					}
					
					if (plannedLocation[0] != -1) {
						
						//Build this bad boy
						
						OutstandingOrder moveOutstandingOrder = new OutstandingOrder(u, time, 
								new MoveOrder(plannedLocation[0], plannedLocation[1]), false);
						OutstandingOrder buildCityOutstandingOrder = new OutstandingOrder(u, time, 
								new BuildCityOrder(), false);
						
						answer.add(moveOutstandingOrder);
						answer.add(buildCityOutstandingOrder);
					}
				} else {
					//Move randomly
					int px = (int) (Math.random() * known.getR());
					int py = (int) (Math.random() * known.getC());
					answer.add(new OutstandingOrder(u, time, new MoveOrder(px, py), false));
				}
			}
			
			if (u instanceof City) {
				//If we have fewer workers than cities, build another worker
				
				if (numWorkers < numCities) {
					answer.add(new OutstandingOrder(u, time, new CreateWorkerOrder(), true));
				}

				City city = (City) u;
				
				if (city.getPopulation() > populationCap) {
					//BUILD THAT ARMY
					
					answer.add(new OutstandingOrder(u, time, new CreateSoldierOrder(), true));
				}
			}
			
			if (u instanceof Soldier) {
				//Find enemy units on map, then attack them
				
				int distance = 1000000000;
				int tx = -1;
				int ty = -1;
				Unit target = null;
				
				for (int i = 0; i < known.getR(); i++) {
					for (int j = 0; j < known.getC(); j++) {
						Unit occupant = known.getMobileUnits()[i][j];
						
						int dx = u.getX() - i;
						int dy = u.getY() - j;
						
						int thisDist = Math.max(Math.abs(dx), Math.abs(dy));
						if (occupant != null && occupant.getTeam() != us.getTeam()) {
							if (thisDist < distance) {
								distance = thisDist;
								tx = i; ty = j;
								target = occupant;
							}
						}
						
						occupant = known.getStaticUnits()[i][j];
						if (occupant != null && occupant.getTeam() != us.getTeam()) {
							if (thisDist < distance) {
								distance = thisDist;
								tx = i; ty = j;
								target = occupant;
							}
						}
					}
				}
				
				if (target == null) {
					//none found, then move randomly
					int px = (int) (Math.random() * known.getR());
					int py = (int) (Math.random() * known.getC());
					answer.add(new OutstandingOrder(u, time, new MoveOrder(px, py), false));
				} else {
					//attack!!!!!!!!!!!!!
					
					if (target instanceof MobileUnit) {
						answer.add(new OutstandingOrder(u, time, new AttackOrder((MobileUnit)target), true));
					} else {
						answer.add(new OutstandingOrder(u, time, new MoveOrder(tx, ty), true));
					}
					
				}
			}
		}
		

		if (delta.getWealth() < 0) {
			populationCap++; //MORE MONEY
		}
		
		populationCap = Math.max(populationCap, 5);
		
		System.out.println("pop cap is " + populationCap);
		
		for (Unit u : units) {
			if (!(u instanceof City)) continue;
			
			//Once any city is over cap pops, stop growth
			City city = (City) u;
			if (city.getPopulation() > populationCap) {
				//Check if it's not enacted
				if (!city.isPopulationControlsEnacted()) {
					answer.add(new OutstandingOrder(city, time, new TogglePopulationControlsOrder(), true));
				}
			} else if (city.isPopulationControlsEnacted()) {
				answer.add(new OutstandingOrder(city, time, new TogglePopulationControlsOrder(), true));
			}
		}
		
		
		return answer;
	}

}
