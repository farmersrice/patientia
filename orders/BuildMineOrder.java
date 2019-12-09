package orders;

import actions.Action;
import actions.BuildMineAction;
import game_map.GameMap;
import game_map.Tile;
import units.Unit;

public class BuildMineOrder extends BuildOrder {
	
	@Override
	boolean isSuitableTile(Unit us, int i, int j) {
		GameMap known = us.getKnown();
		Unit mobileOccupant = known.getMobileUnits()[i][j];
		Unit staticOccupant = known.getStaticUnits()[i][j];
		
		return known.getTerrain()[i][j] == Tile.MINERALS && 
				(mobileOccupant == null || !mobileOccupant.isValid() || mobileOccupant.getId() == us.getId()) &&
				(staticOccupant == null || !staticOccupant.isValid());
	}
	
	@Override
	public Action execute(Unit us) {
		Action res = super.execute(us);
		
		if (res == null) {
			res = new BuildMineAction();
		}
		
		return res;
	}

	public String toString() {
		return "Build mine";
	}
}
