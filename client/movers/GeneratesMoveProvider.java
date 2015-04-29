package battleship.client.movers;

public interface GeneratesMoveProvider {

	public MoveProvider generateMoveProvider(int numRows, int NumCols);
	
}
