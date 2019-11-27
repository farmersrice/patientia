package actions;

import game_manager.GameManager;
import game_map.GameMap;
import game_map.Tile;
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
		
		if (mobileUnits[tx][ty] != null && mobileUnits[tx][ty].getId() != us.getId()) return false;
		
		MobileUnit occupant = mobileUnits[tx][ty];
		
		return Algorithms.isNeighborKing(us.getX(), us.getY(), tx, ty) && known.getTerrain()[tx][ty] != Tile.BLOCKED 
				&& (occupant == null || !occupant.isValid() || (us instanceof Soldier && occupant.getTeam() != us.getTeam())
				|| (us instanceof Soldier && occupant instanceof Soldier && occupant.getTeam() == us.getTeam()));
		
	}

	@Override
	public void execute(Unit us, GameManager m) {
		if (!validate(us, m)) return;
		
		//Add the better logic later, moving ourseles now just a stand int
		
		m.getOmnimap().getMobileUnits()[tx][ty] = (MobileUnit) us;
		m.getOmnimap().getMobileUnits()[us.getX()][us.getY()] = null;
		
		us.setX(tx); us.setY(ty);
	}

}
