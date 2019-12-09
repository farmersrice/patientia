package orders;

import actions.Action;
import actions.DoNothingAction;
import actions.MoveAction;
import game_map.GameMap;
import game_map.Tile;
import units.MobileUnit;
import units.Soldier;
import units.StaticUnit;
import units.Unit;
import utilities.Algorithms;

public class MoveOrder extends Order {
	
	private int tx, ty;
	
	public MoveOrder(int tx, int ty) {
		this.tx = tx; this.ty = ty;
	}
	
	public boolean isComplete(Unit us) {
		boolean curComplete = super.isComplete(us) && us.getX() == tx && us.getY() == ty;
		
		if (curComplete) return true;
		
		int[] nextStep = Algorithms.moveTowards(us.getKnown(), us.getX(), us.getY(), tx, ty, us);
		if (nextStep[0] == -1) curComplete = true;
		
		if (nextStep[0] != -1) {
			
			//Copy logic from the MoveAction since we aren't allowed to know about the omnipresent game map
			
			GameMap known = us.getKnown();
			
			if (us instanceof StaticUnit) curComplete = true; //we cant move lmao
			if (!Algorithms.isValidCoordinate(nextStep[0], nextStep[1], known.getR(), known.getC())) curComplete = true;
			
			MobileUnit[][] mobileUnits = known.getMobileUnits();
			
			//if (mobileUnits[tx][ty] != null && mobileUnits[tx][ty].getId() != us.getId()) return false;
			
			MobileUnit occupant = mobileUnits[nextStep[0]][nextStep[1]];
			
			if (!(Algorithms.isNeighborKing(us.getX(), us.getY(), nextStep[0], nextStep[1]) && 
					known.getTerrain()[nextStep[0]][nextStep[1]] != Tile.BLOCKED 
					&& (occupant == null || !occupant.isValid() || (us instanceof Soldier && occupant.getTeam() != us.getTeam()))))
					curComplete = true;
		}
		
		setComplete(curComplete); //so we don't spam compute moveTowards
		return curComplete;
	}

	@Override
	public Action execute(Unit us) {
		
		if (us.getX() == tx && us.getY() == ty) {
			setComplete(true);
			return new DoNothingAction();
		}
		
		int[] nextStep = Algorithms.moveTowards(us.getKnown(), us.getX(), us.getY(), tx, ty, us);
		return new MoveAction(nextStep[0], nextStep[1]);
	}
	
	@Override
	public int expectedCompletionTime(Unit us) {
		return (Algorithms.kingBFS(us.getKnown(), tx, ty, us)).getDist()[us.getX()][us.getY()];
	}

	public int getTx() {
		return tx;
	}


	public int getTy() {
		return ty;
	}

	public String toString() {
		return "Move to " + tx + " " + ty;
	}

}
