package battleship.client.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class StreamsAndTranscriptViz extends JFrame {

	private JPanel contentPane;
	private JPanel streamPs, trans;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StreamsAndTranscriptViz frame = new StreamsAndTranscriptViz();
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public StreamsAndTranscriptViz() {
		this("demo", new JLabel("stream 1"), new JLabel("stream 2"), new JLabel("transcript"));
	}

	/**
	 * Create the frame.
	 */
	public StreamsAndTranscriptViz(String title, Component s1, Component s2, Component t) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 2, 5, 5));
		streamPs = new JPanel();
		trans    = new JPanel();
		streamPs.setLayout(new GridLayout(1,2,0,0));
		contentPane.add(streamPs);
		trans.setLayout(new GridLayout(1,1,0,0));
		contentPane.add(trans);
		setTitle(title);
		streamPs.add(s1);
		streamPs.add(s2);
		trans.add(t);
	}

}
