package battleship.client.support;

import java.io.DataInputStream;
import java.util.Random;

import battleship.client.movers.GeneratesMoveProvider;
import battleship.client.movers.MoveProvider;
import battleship.client.movers.MoveProviderGenerators;
import battleship.client.util.MessageGenerator;
import battleship.client.viz.GameViz;
import battleship.game.Board;
import battleship.game.Coordinate;
import battleship.game.Ship;

public class DummyPlayer extends ClientSetup implements MessageSender {


	private static final double MESSAGEPROBABILITY = 0.0250;

	final private int snoozeTime;
	private GeneratesMoveProvider gmp;

	private GameViz gv;

	/**
	 * Private constructor, factory methods below generate actual players.
	 *   The factory methods are named so that they generate the appropriate
	 *   kind of player.  Also, using a factory method, the gmp instance variable
	 *   can be configured before the DummyPlayer instance is made available
	 *   for use.
	 *   
	 * @param name User's name, helps distinguish play actions 
	 * @param serverIP
	 * @param port
	 * @param snoozeTime time in ms to sleep before making a move
	 * @param gmp a "future" that generates a MoveProvider
	 */
	private DummyPlayer(
			String name, 
			String serverIP, 
			int port, 
			int snoozeTime
			) {
		super(name, serverIP, port);
		this.snoozeTime = snoozeTime;
	}

	/**
	 * A player who moves randomly
	 * @param name
	 * @param serverIP
	 * @param port
	 * @param snoozeTime
	 * @return
	 */
	public static DummyPlayer genRandomPlayer(
			String name,
			String serverIP,
			int port,
			int snoozeTime
			) {
		DummyPlayer ans = new DummyPlayer(name, serverIP, port, snoozeTime);
		ans.gmp = MoveProviderGenerators.genRandomMover();
		return ans;
	}

	/**
	 * A player who moves methodically, sweeping across and down the board.
	 * @param name
	 * @param serverIP
	 * @param port
	 * @param snoozeTime
	 * @return
	 */
	public static DummyPlayer genMethodicalPlayer(
			String name,
			String serverIP,
			int port,
			int snoozeTime
			) {
		DummyPlayer ans = new DummyPlayer(name, serverIP, port, snoozeTime);
		ans.gmp = MoveProviderGenerators.genMethodicalMover();
		return ans;
	}

	/**
	 * An interactive player.  You move by clicking the squares.
	 * @param name
	 * @param serverIP
	 * @param port
	 * @return
	 */
	public static DummyPlayer genInteractivePlayer(
			String name,
			String serverIP,
			int port
			) {

		final DummyPlayer ans = new DummyPlayer(name, serverIP, port, 0);
		ans.gmp = MoveProviderGenerators.genInteractiveMover(ans);
		return ans;
	}

	@Override
	public void processInput(DataInputStream dis) {
		try {
			//
			// Log in to the server
			//
			out.writeUTF(name);
			out.writeUTF("xyzzy");
			//
			// I want to play, not observe
			//
			out.writeByte(1);
			//
			// Server's response
			String serverName = dis.readUTF();
			ps.println("Server " + serverName);
			int playerNum = dis.readByte();
			ps.println(this + " is player " + playerNum);
			//
			// Get the config message and set up my board
			//
			if (!dis.readUTF().equals("config")){
				System.out.println("not config!");
				throw new Error("Expected config");
			}
			int numRows = dis.readShort();
			int numCols = dis.readShort();
			int numShips = dis.readByte();
			//
			// Set up my board, randomly
			//
			Ship[] ships = new Ship[numShips];
			int at=0;
			Random r = new Random();
			Board board = new Board(numRows, numCols);
			gv = new GameViz(board, name, this);
			gv.setVisible(true);
			for (int i=0; i < numShips; ++i) {
				int len = dis.readByte();
				//
				// The ship is anchored at the board's diagonal
				//
				Coordinate c = new Coordinate(at,at);
				boolean isHoriz = r.nextBoolean();
				Ship s = isHoriz ? Ship.genHorizontalShip(c, len)
						: Ship.genVerticalShip(c, len);
				board.placeShip(s);
				out.writeByte(isHoriz ? 0 : 1);
				out.writeShort(c.row);
				out.writeShort(c.col);
				at = at + r.nextInt(2)+1;
			}
			if (!dis.readUTF().equals("ok"))
				throw new Error("Expected OK");

			//
			// Automatic message generating service
			//
			MessageGenerator mg = new MessageGenerator(5);
			//
			// Now that we know the size of the board, we can invoke
			//   the "future" we held since construction to give us
			//   the move generator for this player
			//
			MoveProvider mp = gmp.generateMoveProvider(numRows, numCols);

			//
			// Game is afoot.  A trace of the activity is sent to the
			//     transcript window (using PrintStream ps).
			// This look runs forever so that even after a winner has been
			//     declared, messages can be received from the server.
			//
			while (true) {
				String s = dis.readUTF();
				ps.println("Player " + playerNum + " received \"" + s + "\"");
				if (s.equals("PlayerTurn")) {
					int who = dis.readByte();
					ps.println("   for player " + who);
					if (who == playerNum) {
						//
						// Process the move in a different thread
						//   so that we don't block here and
						//   can continue receiving messages
						//
						new Thread() {
							public void run() {
								try {
									gv.setPrompts(true);     // indicate move is needed
									snooze(snoozeTime);      // sleep as specified to slow things down
									Coordinate move = mp.getMove(numRows, numCols);
									synchronized(DummyPlayer.this) {
										out.writeUTF("move");    // send move to server
										out.writeShort(move.row);
										out.writeShort(move.col);
									}

									gv.setPrompts(false);    // indicate move is over
								} catch(Throwable t) {
									t.printStackTrace();
									throw new Error("prob " + t);
								}
							}
						}.start();
					}
				}
				else if (s.equals("PlayerFires")) {
					int id=dis.readByte();
					ps.println("   for player " + id);
					int row = dis.readShort();
					int col = dis.readShort();
					//
					// Update my display to show what my opponent did to me
					//
					if (id != playerNum) {
						board.processHit(row, col);
					}
					String hitOrMiss = dis.readUTF();
					String sunkOrNot = dis.readUTF();
					String winOrNot  = dis.readUTF();
					//
					// Server keeps track of what actually happened
					//    so react based on what it says
					//
					if (id != playerNum) {
						if (hitOrMiss.equals("hit")) {
							sendMessage("You got me at (" + row + "," + col + ")");
						}
						if (sunkOrNot.equals("sunk")) {
							sendMessage("YOU SUNK MY BATTLESHIP!");
						}
						if (winOrNot.equals("win")) {
							sendMessage("\nYou win!\n");
						}
					}
				}
				else if (s.equals("broadcast"))  {
					//
					// Asynchronous message, broadcast to everybody
					//
					String from = dis.readUTF();
					ps.println("   from player " + from);
					String message = dis.readUTF();

					//
					// To keep down clutter, display the message only if I didn't
					//    send it
					//
					if (!from.equals(name))
						gv.showMessage("From " + from + ":\n   " + message);
				}
				else 
					throw new Error("Bad message " + s);
				//
				// With some probability, send the server a
				//   random message to be broadcast to everybody
				//
				if (r.nextDouble() < MESSAGEPROBABILITY) {
					String text = mg.nextMessage();
					sendMessage(text);
				}

			}


		} catch(Throwable t) {
			t.printStackTrace();
			throw new Error("Dummy aborts " + t);
		}

	}

	//
	// The lock here prevents messages from being interleaved.
	// One message is sent out completely before another can be
	//   started.
	// It may appear that such interleaving is not possible if you look
	//   only at this code.
	// But the GameViz can generate a message on behalf of the player too,
	//   at the press of a button on the visualization.   Although it would
	//   be unlikely, we don't want those messages to be
	//   interleaved with messages from
	//   the play action above.
	// 
	public synchronized void sendMessage(String text) {
		try {
			out.writeUTF("message");
			out.writeUTF(text);
		} catch(Throwable t) {
			throw new Error("bad send message " + t);
		}
	}

	public String toString() {
		return "Dummy Player " + name;
	}

	public GameViz getGameViz() {
		return this.gv;
	}

	public static void snooze(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}
	}

}
