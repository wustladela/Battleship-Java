package battleship.client.viz;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import battleship.client.movers.MoveProvider;
import battleship.client.support.PromptsUser;
import battleship.client.util.Wrappers;
import battleship.game.Board;
import battleship.game.Coordinate;
import battleship.game.HitProcessor;

/**
 * A Panel for visualizing the game
 * @author roncytron
 *
 */
public class BoardViz extends JPanel implements MoveProvider {

	private JPanel contentPane;
	private Coordinate lastClickedCoord;
	final private PromptsUser prompter;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BoardViz frame = new BoardViz();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public BoardViz() {
		this(new Board(5,7),"title", new PromptsUser() {

			@Override
			public void setPrompts(boolean userMustRespond) {
				
			} });
	}
	/**
	 * Create the frame.
	 */
	public BoardViz(Board b, String title, PromptsUser prompter) {
		this.prompter = prompter;
		setBounds(100, 100, 450, 300);
		contentPane = this;
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridLayout(b.getNumRows(), b.getNumCols(), 3, 3));
		
		final HitProcessor hp = new HitProcessor() {

			@Override
			public boolean processHit(Coordinate coord) {
				lastClickedCoord = coord;
				Wrappers.notifyAll(BoardViz.this);
				return true;
			}
			
		};
		
		
		for (int r=0; r < b.getNumRows(); ++r) {
			for (int c =0; c < b.getNumCols(); ++c) {
				contentPane.add(new SquareViz(new Coordinate(r,c), b.getPCS(), hp));
			}
		}
	}

	@Override
	public Coordinate getMove(int numRows, int numCols) {
		lastClickedCoord = null;
		prompter.setPrompts(true);
		while (lastClickedCoord == null) {
			Wrappers.wait(this);
		}
		prompter.setPrompts(false);
		return lastClickedCoord;
	}

}
