package battleship.client.movers;

import java.util.Random;

import battleship.game.Coordinate;

public class MethodicalMover implements MoveProvider {

	private BoardSweeper sweeper;
	private Random rand;

	public MethodicalMover(int numRows, int numCols) {
		this.sweeper = new BoardSweeper(numRows, numCols);
		this.rand    = new Random();
	}

	@Override
	public Coordinate getMove(int numRows, int numCols) {

		Coordinate move = sweeper.hasNext() ? sweeper.next()
				: new Coordinate(rand.nextInt(numRows), rand.nextInt(numCols));

		return move;

	}

}
