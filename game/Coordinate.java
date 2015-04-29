package battleship.game;

import java.util.Iterator;

public class Coordinate {
	
	public final int row, col;
	
	public Coordinate(int r, int c) {
		this.row = r;
		this.col = c;
	}

	@Override
	public String toString() {
		return "Coordinate [row=" + row + ", col=" + col + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}
	
	/**
	 * Iterator that starts at this Coordinate and goes indefinitely to the right
	 * @return said Iterator
	 */
	public Iterator<Coordinate> goRight() {
		return new Iterator<Coordinate>() {

			private Coordinate coord = Coordinate.this;
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Coordinate next() {
				Coordinate answer = coord;
				coord = coord.right();
				return answer;
			}
			
		};
	}
	
	/**
	 * Iterator that starts at this Coordinate and goes indefinitely down
	 * @return said Iterator
	 */
	public Iterator<Coordinate> goDown() {
		return new Iterator<Coordinate>() {

			private Coordinate coord = Coordinate.this;
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Coordinate next() {
				Coordinate answer = coord;
				coord = coord.down();
				return answer;
			}
			
		};
	}

	/**
	 * 
	 * @return The coordinate to the right of this one
	 */
	public Coordinate right() {
		return new Coordinate(this.row, this.col+1);
	}
	
	/**
	 * 
	 * @return The coordinate just below this one
	 */
	public Coordinate down() {
		return new Coordinate(this.row+1, this.col);
	}
	
	

}
