package battleship.game;

import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

public class Board implements HitProcessor {

	final private int numRows, numCols;
	final private PropertyChangeSupport pcs;
	final private Ship[][] board;

	// 
	// Ships could be picked up from the Board
	//   but for efficiency we'll keep track of them
	//   separately
	final private Set<Ship> ships;

	public Board(int numRows, int numCols) {
		this.numRows = numRows;
		this.numCols = numCols;
		this.pcs     = new PropertyChangeSupport(this);
		this.board   = new Ship[numRows][numCols];
		this.ships   = new HashSet<Ship>();
	}

	public int getNumShips() {
		return this.ships.size();
	}

	/**
	 * Try to put Ship s on the Board
	 * @param s
	 * @return true iff ship was successfully placed
	 */
	public boolean placeShip(Ship s) {
		int oldNumShips = this.getNumShips();
		for (Coordinate coord : s.getCoordinates()) {
			if (isOccupied(coord)) {
				pcs.firePropertyChange("badshipplace", null, s);
				return false;
			}
		}
		for (Coordinate coord : s.getCoordinates()) {
			board[coord.row][coord.col] = s;   // the ship s occupies this spot
		}
		ships.add(s);
		pcs.firePropertyChange("shipplaced", null, s);
		int newNumShips = this.getNumShips();
		pcs.firePropertyChange("numships", oldNumShips, newNumShips);
		return true;
	}

	/**
	 * This method included by popular demand during class
	 *    delegates to takeAShot(Coordinate)
	 * @param r
	 * @param c
	 * @return
	 */
	public boolean processHit(int r, int c) {
		return this.processHit(new Coordinate(r,c));
	}

	public boolean onBoard(Coordinate coord) {
		int row = coord.row;
		int col = coord.col;
		return 0 <= row && row < numRows && 0 <= col && col < numCols;
	}

	/**
	 * Evaluates the result of a shot fired at
	 *    the specified Coordinate
	 * @param coord
	 * @return true iff coordinate is on the board
	 */
	public boolean processHit(Coordinate coord) {
		if (!onBoard(coord)) {
			return false;
		}

		int numShipsBefore = this.getNumShipsLeft();
		int row = coord.row;
		int col = coord.col;

		Ship s = board[row][col];

		if (s == null) {
			pcs.firePropertyChange("miss", null, coord);
		}
		else {
			pcs.firePropertyChange("hit", null, coord);
			board[row][col] = null;
			boolean sunk = s.takeHit();
			if (sunk) {
				pcs.firePropertyChange("shipsunk", null, s);
				ships.remove(s);
			}
			int numShipsAfter = this.getNumShipsLeft();
			pcs.firePropertyChange("numships", numShipsBefore, numShipsAfter);
		}
		return true;

	}

	public boolean isOccupied(Coordinate coord) {
		return board[coord.row][coord.col] != null;
	}

	private int getNumShipsLeft() {
		return ships.size();
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public PropertyChangeSupport getPCS() {
		return pcs;
	}

	public static void main(String[] args) {
		// FIXME Auto-generated method stub

	}

}
