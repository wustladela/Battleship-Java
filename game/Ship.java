package battleship.game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Ship {
	
	final private Set<Coordinate> locations;
	private int numHits;
	
	/**
	 * Internal constructor
	 * @param locations
	 */
	Ship(Set<Coordinate> locations) {
		this.locations = locations;
		this.numHits  = 0;
	}
	
	public static Ship generateShipUsingIterator(Iterator<Coordinate> iter, int length) {
		HashSet<Coordinate> coords = new HashSet<Coordinate>();
		for (int i=0; i < length; ++i) {
			coords.add(iter.next());
		}
		return new Ship(coords);
	}
	
	/**
	 * Factory method to generate a horizontal ship
	 * @param leftCoord the leftmost coordinate
	 * @param length number of coordinates occupied by this ship
	 * @return the specified ship
	 */
	public static Ship genHorizontalShip(Coordinate leftCoord, int length) {
		return Ship.generateShipUsingIterator(leftCoord.goRight(), length);
	}
	
	/**
	 * Factory method to generate a vertical ship
	 * @param topCoord the topmost coordinate
	 * @param length the number of coordinates occupied by this ship
	 * @return the specified ship
	 */
	public static Ship genVerticalShip(Coordinate topCoord, int length) {
		return Ship.generateShipUsingIterator(topCoord.goDown(), length);		
	}

	/**
	 * Return a copy of the set of this Ship's coordinates
	 *   A copy is made so that the caller cannot affect 
	 *   the set maintained in this object.
	 * @return a copy of this Ship's Coordinates
	 */
	public Set<Coordinate> getCoordinates() {
		Set<Coordinate> copy = new HashSet<Coordinate>();
		copy.addAll(this.locations);
		return copy;  // so that my set cannot be modified
	}
		
	/**
	 * How many more hits can this ship take until it is sunk?
	 * @return number of hits left until the ship sinks
	 */
	public int shotsUntilSunk() {
		return this.locations.size() - this.numHits;
	}
	/**
	 * Record a hit on this ship
	 * @return true iff this hit caused the ship to sink
	 */
	public boolean takeHit() {
		this.numHits = this.numHits + 1;
		if (this.numHits > locations.size())
			throw new Error("Too many hits on ship " + this);
		
		if (this.numHits == locations.size()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Ship [location=" + locations + "]";
	}

	public static void main(String[] args) {
		Coordinate one = new Coordinate(4,5);
		Ship s = Ship.genHorizontalShip(one, 3);
		System.out.println("Got ship " + s);
		// mu hahahaha evil about to happen
		Set<Coordinate> igotyou = s.getCoordinates();
		// author never expects me to:
		igotyou.clear();
		// but this is OK now because igotyou is a copy of the
		//    original set
		System.out.println("Ship is now " + s);
	}

}
