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
	
	public MoveAction(Unit u, int x, int y) {
		super(u);
		tx = x; ty = y;
		// TODO Auto-generated constructor stub
	}
	
	public MoveAction(String s, GameManager m) {
		super(s, m);
		
		try {
			String[] vals = s.split(" ");
			tx = Integer.parseInt(vals[1]);
			ty = Integer.parseInt(vals[2]);
		} catch (Exception e) {
			valid = false;
		}
	}

	@Override
	public boolean validate(GameManager m) {
		// TODO Auto-generated method stub
		if (!super.validate(m)) return false;
		
		GameMap known = m.getOmnimap();
		
		if (us instanceof StaticUnit) return false; //we cant move lmao
		
		MobileUnit[][] mobileUnits = known.getMobileUnits();
		
		if (mobileUnits[tx][ty].getId() != us.getId()) return false;
		
		MobileUnit occupant = mobileUnits[tx][ty];
		
		return Algorithms.isNeighborKing(us.getX(), us.getY(), tx, ty) && known.getTerrain()[tx][ty] != Tile.BLOCKED 
				&& (occupant == null || !occupant.isValid() || (us instanceof Soldier && occupant.getTeam() != us.getTeam())
				|| (us instanceof Soldier && occupant instanceof Soldier && occupant.getTeam() == us.getTeam()));
		
	}

	@Override
	public void execute(GameManager m) {
		// TODO Auto-generated method stub

	}

}
