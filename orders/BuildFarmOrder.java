package orders;

import actions.Action;
import actions.BuildFarmAction;
import game_map.GameMap;
import game_map.Tile;
import units.Unit;

public class BuildFarmOrder extends BuildOrder {
	
	@Override
	boolean isSuitableTile(Unit us, int i, int j) {
		GameMap known = us.getKnown();
		Unit mobileOccupant = known.getMobileUnits()[i][j];
		Unit staticOccupant = known.getStaticUnits()[i][j];
		
		return known.getTerrain()[i][j] != Tile.BLOCKED &&
				(mobileOccupant == null || !mobileOccupant.isValid() || mobileOccupant.getId() == us.getId()) &&
				(staticOccupant == null || !staticOccupant.isValid());
	}
	
	@Override
	public Action execute(Unit us) {
		Action res = super.execute(us);
		
		if (res == null) {
			res = new BuildFarmAction();
		}
		
		return res;
	}

	public String toString() {
		return "Build farm";
	}
}
