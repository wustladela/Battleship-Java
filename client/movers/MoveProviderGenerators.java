package battleship.client.movers;

import battleship.client.support.DummyPlayer;
import battleship.game.Coordinate;

public class MoveProviderGenerators {

	public static GeneratesMoveProvider genMethodicalMover() {
		return new GeneratesMoveProvider() {
			@Override
			public MoveProvider generateMoveProvider(final int numRows, final int numCols) {
				return new MethodicalMover(numRows, numCols);
			}

		};
	}
	public static GeneratesMoveProvider genRandomMover() {
		return new GeneratesMoveProvider() {
			@Override
			public MoveProvider generateMoveProvider(final int numRows, final int numCols) {
				return new RandomMover(numRows, numCols);
			}

		};
	}

	public static GeneratesMoveProvider genInteractiveMover(DummyPlayer dp) {
		return new GeneratesMoveProvider() {

			@Override
			public MoveProvider generateMoveProvider(int numRows, int NumCols) {
				return new MoveProvider() {

					@Override
					public Coordinate getMove(int numRows, int numCols) {
						return dp.getGameViz().getBoardViz().getMove(numRows, NumCols);
					}

				};
			}

		};
	}
}