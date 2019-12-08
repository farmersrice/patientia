package ai;

import java.util.ArrayList;

import game_manager.OutstandingOrder;
import game_manager.Player;
import game_manager.ResourceDelta;

public class FirstAI implements AIInterface {

	@Override
	public ArrayList<OutstandingOrder> think(Player us) {
		
		//Compute deltas
		
		ResourceDelta delta = us.getExpectedDelta();
		
		//Wait until all 
		
		return null;
	}

}
