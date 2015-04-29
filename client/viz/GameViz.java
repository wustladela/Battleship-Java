package battleship.client.viz;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import battleship.client.support.MessageSender;
import battleship.client.support.PromptsUser;
import battleship.client.util.PrintStreamPanel;
import battleship.game.Board;

/**
 * The game visualizer
 * @author roncytron
 *
 */
public class GameViz extends JFrame implements PromptsUser {

	private JPanel contentPane;
	private PrintStream ps;
	private JLabel lblNewLabel;
	private BoardViz bv;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameViz frame = new GameViz();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Default constructor for WindowBuilder
	 */
	public GameViz() {
		this(new Board(5,7), "name", null);
	}

	/**
	 * The main constructor.
	 */
	public GameViz(final Board board, final String name, final MessageSender ms) {
		setTitle(name);
		bv = new BoardViz(board, name, this);
		PrintStreamPanel transcript = new PrintStreamPanel(Color.GREEN, "Messages",375, 14);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 362);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new GridLayout(1,2,5,5));
		boardPanel.setBounds(6, 6, 789, 248);
		contentPane.add(boardPanel);
		boardPanel.add(bv);
		boardPanel.add(transcript);
		this.ps = transcript.getPrintStream();
		
		final JTextArea outGoing = new JTextArea();
		outGoing.setColumns(20);
		outGoing.setBounds(6, 266, 765, 32);
		contentPane.add(outGoing);
		outGoing.setText("Type text in this box, then hit \"Send\" to broadcast your message");
		
		JButton btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ms.sendMessage(outGoing.getText());
				outGoing.setText("");
			}
		});
		btnSend.setBounds(6, 305, 117, 29);
		contentPane.add(btnSend);
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(274, 310, 103, 16);
		contentPane.add(lblNewLabel);
	}
	
	public BoardViz getBoardViz() {
		return bv;
	}
	
	public void setPrompts(boolean isOn) {
		if (isOn) {
			lblNewLabel.setText("Your Turn");
			lblNewLabel.setOpaque(true);
			lblNewLabel.setBackground(Color.RED);
		}
		else {
			lblNewLabel.setText("");
			lblNewLabel.setBackground(Color.GRAY);
		}
	}

	/**
	 * Show a message in the transcript window
	 * @param string
	 */
	public void showMessage(String string) {
		ps.println(string);
	}
}
