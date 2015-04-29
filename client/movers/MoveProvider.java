package battleship.client.movers;

import battleship.game.Coordinate;

public interface MoveProvider {
	
	public Coordinate getMove(int numRows, int numCols);

}
