package battleship.client.viz;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import battleship.game.Coordinate;
import battleship.game.HitProcessor;
import battleship.game.Ship;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SquareViz extends JPanel {

	private static Border bevelBorder = BorderFactory.createRaisedBevelBorder();
	private static Border emptyBorder = BorderFactory.createEmptyBorder();
	final private Coordinate coord;
	final private PropertyChangeSupport pcs;

	public SquareViz(
			final Coordinate coord, 
			final PropertyChangeSupport pcs,
			final HitProcessor hp
			) {
		this.coord = coord;
		this.pcs   = pcs;
		this.setBackground(Color.GRAY);
		
		reactAll("shipplaced", Color.GREEN);
		reactAll("shipsunk", Color.BLUE);

		pcs.addPropertyChangeListener(
				"hit",
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						Coordinate c = (Coordinate) evt.getNewValue();

						if (c.equals(coord)) {
							setBackground(Color.RED);
						}


					}
				}
				);
		
		pcs.addPropertyChangeListener(
				"miss",
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						Coordinate c = (Coordinate) evt.getNewValue();
						
						if (c.equals(coord)) {
							setBackground(Color.PINK);
						}
					}
				}
				);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (hp != null)
					hp.processHit(coord);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				SquareViz.this.setBorder(bevelBorder);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				SquareViz.this.setBorder(emptyBorder);
			}
		});

	}

	/**
	 * Screen a Ship message to see if it affects this square, and if so,
	 *   change to the specified color
	 * @param message the PCS message of interest
	 * @param color the Color to become if this Square represents one of the Ship's coordinates
	 */
	private void reactAll(String message, final Color color) {
		pcs.addPropertyChangeListener(message, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Ship s = (Ship) evt.getNewValue();
				for (Coordinate c : s.getCoordinates()) {
					if (coord.equals(c)) {
						setBackground(color);
					}
				}

			} });
	}

}


