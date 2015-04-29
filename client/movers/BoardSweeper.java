package battleship.client.movers;

import java.util.Iterator;

import battleship.game.Coordinate;

/**
 * Generates moves for the game methodically, 
 *    sweeping across columns and down rows
 * @author roncytron
 *
 */
public class BoardSweeper implements Iterator<Coordinate>{
	
	private int row, col;
	final private int numRows, numCols;
	
	public BoardSweeper(int numRows, int numCols) {
		this.numRows = numRows;
		this.numCols = numCols;
		this.row = 0;
		this.col = 0;
	}


	@Override
	public boolean hasNext() {
		return (row < numRows);
	}

	/**
	 * Returns the next row and column as a Coordinate
	 */
	@Override
	public Coordinate next() {
		Coordinate ans = new Coordinate(row, col);
		if (row >= numRows)
			return ans;
		if (col >= numCols) {
			row = row + 1;
			col = 0;
		}
		else {
			col = col + 1;
		}
		return ans;
	}

}
