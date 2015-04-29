package battleship.client.movers;

import java.util.Random;

import battleship.game.Coordinate;

public class RandomMover implements MoveProvider {

	private Random rand;

	public RandomMover(int numRows, int numCols) {
		this.rand    = new Random();
	}

	@Override
	public Coordinate getMove(int numRows, int numCols) {

		Coordinate move = new Coordinate(rand.nextInt(numRows), rand.nextInt(numCols));

		return move;

	}

}
