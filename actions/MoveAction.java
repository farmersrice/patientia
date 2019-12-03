package actions;

import game_manager.GameManager;
import game_map.GameMap;
import game_map.Tile;
import units.City;
import units.MobileUnit;
import units.Soldier;
import units.StaticUnit;
import units.Unit;
import utilities.Algorithms;

public class MoveAction extends Action {

	private int tx, ty;
	
	public MoveAction(int x, int y) {
		tx = x; ty = y;
		// TODO Auto-generated constructor stub
	}
	
	public MoveAction(String s, GameManager m) {
		try {
			String[] vals = s.split(" ");
			tx = Integer.parseInt(vals[1]);
			ty = Integer.parseInt(vals[2]);
		} catch (Exception e) {
			valid = false;
		}
	}

	@Override
	public boolean validate(Unit us, GameManager m) {
		// TODO Auto-generated method stub
		if (!super.validate(us, m)) return false;
		
		GameMap known = m.getOmnimap();
		
		if (us instanceof StaticUnit) return false; //we cant move lmao
		if (!Algorithms.isValidCoordinate(tx, ty, known.getR(), known.getC())) return false;
		
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		
		//if (mobileUnits[tx][ty] != null && mobileUnits[tx][ty].getId() != us.getId()) return false;
		
		MobileUnit occupant = mobileUnits[tx][ty];
		
		return Algorithms.isNeighborKing(us.getX(), us.getY(), tx, ty) && known.getTerrain()[tx][ty] != Tile.BLOCKED 
				&& (occupant == null || !occupant.isValid() || (us instanceof Soldier && occupant.getTeam() != us.getTeam()));
		
	}

	@Override
	public void execute(Unit us, GameManager m) {
		if (!validate(us, m)) return;
		
		
		GameMap known = m.getOmnimap();
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		
		MobileUnit occupant = mobileUnits[tx][ty];
		
		if (occupant == null || !occupant.isValid() || !(occupant instanceof Soldier)) {
			known.getMobileUnits()[tx][ty] = (MobileUnit) us;
			known.getMobileUnits()[us.getX()][us.getY()] = null;
			us.setX(tx); us.setY(ty);
		} else if (occupant instanceof Soldier) {
			//Conquer them
			double strengthUs = ((Soldier)us).getStrength();
			double strengthOther = ((Soldier)occupant).getStrength();
			((Soldier)us).takeDamage(strengthOther);
			((Soldier)occupant).takeDamage(strengthUs);
			
			if (us.isValid() && !occupant.isValid()) {
				known.getMobileUnits()[tx][ty] = (MobileUnit) us;
				known.getMobileUnits()[us.getX()][us.getY()] = null;
				us.setX(tx); us.setY(ty);
			} else if (!occupant.isValid()) {
				mobileUnits[tx][ty] = null;
			}
			
			if (!us.isValid()) {
				mobileUnits[us.getX()][us.getY()] = null;
			} 
			
		}
		
		//Set the static unit to ours (conquering city, farm, mine)
		StaticUnit staticOccupant = known.getStaticUnits()[tx][ty];
		
		if (staticOccupant != null && staticOccupant.isValid() && staticOccupant.getTeam() != us.getTeam()) {
			staticOccupant.setTeam(us.getTeam());
			if (staticOccupant instanceof City) {
				//Halve the population due to conquest
				((City)staticOccupant).setPopulation(((City)staticOccupant).getPopulation() / 2);
			}
		}
	}

}
