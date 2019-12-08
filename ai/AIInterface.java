package ai;

import java.util.ArrayList;

import game_manager.OutstandingOrder;
import game_manager.Player;

public interface AIInterface {
	
	public ArrayList<OutstandingOrder> think(Player us, int time);
}
