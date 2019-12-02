package game_manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import game_map.GameMap;
import game_map.Tile;
import units.City;
import units.MobileUnit;
import units.StaticUnit;
import units.Unit;
import units.Worker;

public class GameManager {
	private int numPlayers;
	private Player[] players;
	private GameMap omnimap;
	private int currentUnitCounter = 0;
	private int turnCounter = 0;
	private PriorityQueue<GameMap>[] playerKnownUpdateQueues;
	
	private ArrayList<OutstandingOrder> outstandingOrders = new ArrayList<OutstandingOrder>(); 
	//outstanding as in, still needs to be done
	
	final int COMMUNICATION_SPEED = 5; 
	//If player is [0, 5) units away, moves conveyed instantly, [5, 10), takes 1 turn, so on
	
	public GameManager(int rows, int cols, int numPlayers) {
		omnimap = new GameMap(rows, cols);
		omnimap.generate();
		
		this.numPlayers = numPlayers;
		
		players = new Player[numPlayers];
		playerKnownUpdateQueues = new PriorityQueue[numPlayers];
		
		for (int i = 0; i < numPlayers; i++) {
			players[i] = new Player(100000, 100, 100);
			
			//The maps with the earliest times get updated first
			playerKnownUpdateQueues[i] = new PriorityQueue<GameMap>((a, b) -> a.getUpdateTime() - b.getUpdateTime());
		}
		
		for (int i = 0; i < numPlayers; i++) {
			//Randomly place capitals
			
			int capitalX = (int) (Math.random() * rows);
			int capitalY = (int) (Math.random() * cols);
			
			while (omnimap.getTerrain()[capitalX][capitalY] == Tile.BLOCKED || 
					omnimap.getStaticUnits()[capitalX][capitalY] != null) {
				//regenerate location
				capitalX = (int) (Math.random() * rows);
				capitalY = (int) (Math.random() * cols);
			}
			
			omnimap.getStaticUnits()[capitalX][capitalY] = 
					new City(i, currentUnitCounter++, capitalX, capitalY, omnimap.slice(capitalX, capitalY, 5, -1));
			omnimap.getMobileUnits()[capitalX][capitalY] = 
					new Worker(i, currentUnitCounter++, capitalX, capitalY, omnimap.slice(capitalX, capitalY, 5, -1));
			players[i].setKnown(omnimap.slice(capitalX, capitalY, 5, -1));
		}
	}
	
	public void addOutstandingOrder(OutstandingOrder o) {
		outstandingOrders.add(o);
	}
	
	public int getTurnCounter() {
		return turnCounter;
	}

	public void setTurnCounter(int turnCounter) {
		this.turnCounter = turnCounter;
	}
	
	public int getLag(Unit us) {
		int shortestDistance = Integer.MAX_VALUE;
		
		for (int i = 0; i < omnimap.getR(); i++) {
			for (int j = 0; j < omnimap.getC(); j++) {
				Unit temp = omnimap.getStaticUnits()[i][j];
				
				if (temp != null && temp.isValid() && temp.getTeam() == us.getTeam() && temp instanceof City) {
					int dx = us.getX() - temp.getX();
					int dy = us.getY() - temp.getY();
					shortestDistance = Math.min(shortestDistance, dx * dx + dy * dy);
				}
			}
		}
		int backTurns = (int) Math.floor(Math.sqrt(shortestDistance) / COMMUNICATION_SPEED);
		
		return backTurns;
	}

	public void turn() {
		ArrayList<Unit> order = new ArrayList<Unit>();
		
		Unit[][] units1 = omnimap.getMobileUnits();
		Unit[][] units2 = omnimap.getStaticUnits();
		
		for (int i = 0; i < omnimap.getR(); i++) {
			for (int j = 0; j < omnimap.getC(); j++) {
				if (units1[i][j] != null && units1[i][j].isValid()) {
					order.add(units1[i][j]);
				}
				if (units2[i][j] != null && units2[i][j].isValid()) {
					order.add(units2[i][j]);
				}
			}
		}
		
		Collections.shuffle(order);
		
		for (Unit u : order) {
			if (!u.isValid()) continue;
			
			//Check the distance to the nearest city, and update with latest available map of the player
			
			//How long does this take in total? Number of units squared, which should be OK, even if people have like 2000 units
			//it should take < 1 second
			
			int shortestDistance = Integer.MAX_VALUE;
			
			for (Unit v : order) { //don't use getLag here since probably less units than tiles
				if (!v.isValid() || v.getTeam() != u.getTeam() || !(v instanceof City)) continue;
				
				//We know it's one of our cities
				int dx = u.getX() - v.getX();
				int dy = u.getY() - v.getY();
				shortestDistance = Math.min(shortestDistance, dx * dx + dy * dy);
			}
			
			int backTurns = (int) Math.floor(Math.sqrt(shortestDistance) / COMMUNICATION_SPEED);
			
			//Check if we can update any outstanding orders
			for (OutstandingOrder o : outstandingOrders) {
				if (o.getTimeIssued() + backTurns <= turnCounter && o.getTarget().getId() == u.getId()) {
					//Add it
					if (o.isSet()) {
						u.setOrder(o.getOrder());
					} else {
						u.addOrder(o.getOrder());
					}
					
					System.out.println("processed order " + o.getTarget().getId() + " " + o.getOrder().toString());
					o.setDone(true);
				}
			}
		}
		
		ArrayList<OutstandingOrder> filtered = new ArrayList<OutstandingOrder>();
		for (OutstandingOrder o : outstandingOrders) {
			if (!o.isDone() && o.getTarget().isValid()) {
				filtered.add(o);
			} else {
				System.out.println("Deleting order, irrelevant " + o.getTarget().getId() + " " + o.getOrder().toString());
				System.out.println("validity " + o.getTarget().isValid());
			}
		}
		outstandingOrders = filtered;
		
		
		System.out.println("turn " + turnCounter);
		for (Unit u : order) {
			if (u.isValid()) {
				u.processPassiveEffects(this);
				System.out.println("processing action " + u.getAction().getClass().getName());
				u.getAction().execute(u, this);
			}
		}
		
		
		//Re-update visual knowledge for display purposes
		for (Unit u : order) {
			if (!u.isValid()) continue;
			
			u.getKnown().updateKnowledge(omnimap.slice(u.getX(), u.getY(), 2, turnCounter));
			
			int shortestDistance = Integer.MAX_VALUE;
			
			for (Unit v : order) { //don't use getLag here since probably less units than tiles
				if (!v.isValid() || v.getTeam() != u.getTeam() || !(v instanceof City)) continue;
				
				//We know it's one of our cities
				int dx = u.getX() - v.getX();
				int dy = u.getY() - v.getY();
				shortestDistance = Math.min(shortestDistance, dx * dx + dy * dy);
			}
			
			int backTurns = (int) Math.floor(Math.sqrt(shortestDistance) / COMMUNICATION_SPEED);
			
			u.getKnown().updateKnowledge(players[u.getTeam()].getPrevKnown(backTurns));
			
			
			//Update the player by adding our map to the player update queue
			
			GameMap uKnownClone = u.getKnown().clone();
			uKnownClone.setUpdateTime(turnCounter + backTurns);
			playerKnownUpdateQueues[u.getTeam()].add(uKnownClone);
		}
		
		//Update all players' knowledge AGAIN in case we added some with zero delay in the unit map updating
		for (int i = 0; i < numPlayers; i++) {
			while (playerKnownUpdateQueues[i].size() > 0 && playerKnownUpdateQueues[i].peek().getUpdateTime() <= turnCounter) {
				players[i].getKnown().updateKnowledge(playerKnownUpdateQueues[i].poll());
			}
			players[i].addKnown(); //Shift the turn-based knowledge
		}
		
		
		turnCounter++;
	}
	
	public ArrayList<OutstandingOrder> getOutstandingOrders() {
		return outstandingOrders;
	}

	public void setOutstandingOrders(ArrayList<OutstandingOrder> outstandingOrders) {
		this.outstandingOrders = outstandingOrders;
	}

	private Unit findUnit(String s) {
		int id = 0;
		
		try {
			id = Integer.parseInt(s.split(" ")[0]);
		} catch (Exception e) {
			return null;
		}
		
		GameMap known = omnimap;
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		StaticUnit[][] staticUnits = known.getStaticUnits();
		
		for (int i = 0; i < known.getR(); i++) {
			for (int j = 0; j < known.getC(); j++) {
				if (mobileUnits[i][j] != null && mobileUnits[i][j].isValid() && mobileUnits[i][j].getId() == id) {
					//us = mobileUnits[i][j];
					return mobileUnits[i][j];
				}
				
				if (staticUnits[i][j] != null && staticUnits[i][j].isValid() && staticUnits[i][j].getId() == id) {
					//us = staticUnits[i][j];
					return staticUnits[i][j];
				}
			}
		}
		return null;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	public Player[] getPlayers() {
		return players;
	}

	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public GameMap getOmnimap() {
		return omnimap;
	}

	public void setOmnimap(GameMap omnimap) {
		this.omnimap = omnimap;
	}

	public int getCurrentUnitCounter() {
		return currentUnitCounter;
	}

	public void setCurrentUnitCounter(int currentUnitCounter) {
		this.currentUnitCounter = currentUnitCounter;
	}
	
	
}
