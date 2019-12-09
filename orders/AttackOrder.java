package orders;

import actions.Action;
import actions.DoNothingAction;
import actions.MoveAction;
import game_map.GameMap;
import units.MobileUnit;
import units.Unit;
import utilities.Algorithms;


//Only relevant for mobile units, since static units don't move and you can just treat it as walking on them.

public class AttackOrder extends Order {

	private MobileUnit target;
	
	public AttackOrder(MobileUnit target) {
		this.target = target;
	}
	
	public int[] findTarget(Unit us) {
		GameMap known = us.getKnown();
		
		for (int i = 0; i < known.getR(); i++) {
			for (int j = 0; j < known.getC(); j++) {
				if (known.getMobileUnits()[i][j] != null && known.getMobileUnits()[i][j].getId() == target.getId()) {
					return new int[] {i, j};
				}
			}
		}
		
		return new int[] {-1, -1};
	}
	
	public boolean isComplete(Unit us) {
		if (super.isComplete(us)) return true;
		
		int[] targetCoords = findTarget(us);
		
		if (targetCoords[0] == -1) {
			setComplete(true);
			return true;
		}
		return false;
	}

	@Override
	public Action execute(Unit us) {
		int[] targetCoords = findTarget(us);
		
		if (targetCoords[0] == -1) {
			setComplete(true);
			return new DoNothingAction();
		}
		
		int[] nextStep = Algorithms.moveTowards(us.getKnown(), us.getX(), us.getY(), targetCoords[0], targetCoords[1], us);
		return new MoveAction(nextStep[0], nextStep[1]);
	}
	
	@Override
	public int expectedCompletionTime(Unit us) {
		int[] targetCoords = findTarget(us);
		return (Algorithms.kingBFS(us.getKnown(), targetCoords[0], targetCoords[1], us)).getDist()[us.getX()][us.getY()];
	}

	public String toString() {
		return "Attack unit " + target.getId();
	}

}
