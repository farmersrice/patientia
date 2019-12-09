package orders;

import actions.Action;
import actions.DoNothingAction;
import actions.MoveAction;
import game_map.GameMap;
import units.Unit;
import utilities.Algorithms;

public abstract class BuildOrder extends Order {

	abstract boolean isSuitableTile(Unit us, int i, int j);
	
	public int[] findClosestSuitableTile(Unit us) {
		GameMap known = us.getKnown();


		int distance = 1000000000;
		int closestX = -1;
		int closestY = -1;
		
		for (int i = 0; i < known.getR(); i++) {
			for (int j = 0; j < known.getC(); j++) {
				if (isSuitableTile(us, i, j)) {
					//It's a suitable tile
					
					int dx = us.getX() - i;
					int dy = us.getY() - j;
					
					int thisDistance = Math.max(Math.abs(dx), Math.abs(dy));
					
					if (thisDistance < distance) {
						distance = thisDistance;
						closestX = i;
						closestY = j;
					}
				}
			}
		}
		
		return new int[] {closestX, closestY};
	}

	@Override
	public Action execute(Unit us) {
		//Locate nearest suitable tile
		
		int[] closest = findClosestSuitableTile(us);
		int closestX = closest[0];
		int closestY = closest[1];
		
		if (closestX == -1) {
			setComplete(true);
			return new DoNothingAction();
		}
		
		if (closestX == us.getX() && closestY == us.getY()) {
			setComplete(true);
			return null; //moderately bad placeholder for doing the specific action we need
		}
		
		//Move towards it
		int[] nextStep = Algorithms.moveTowards(us.getKnown(), us.getX(), us.getY(), closestX, closestY, us);
		return new MoveAction(nextStep[0], nextStep[1]);
	}
}
