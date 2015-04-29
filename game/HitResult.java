package battleship.game;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class HitResult {
	
	private boolean hit, sunk, win;
	
	public HitResult(PropertyChangeSupport pcs) {
		react(pcs, "hit", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				hit = true;
			} 
			
		});
		react(pcs, "shipsunk", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				sunk = true;
			} 
			
		});
		react(pcs, "numships", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue().equals(new Integer(0))) {
					win = true;
				}
			} 
			
		});
		
		reset();
		
	}
	
	public void reset() {
		this.hit  = false;
		this.sunk = false;
	}
	
	public String isHit() {
		return hit ? "hit" : "miss";
	}
	
	public String isSunk() {
		return sunk ? "sunk" : "notsunk";
	}
	
	public String isWin() {
		return win ? "win" : "nowin";
	}
	
	private void react(PropertyChangeSupport pcs, String name, PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(name, pcl);
	}

}
