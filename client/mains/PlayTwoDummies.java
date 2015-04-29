package battleship.client.mains;

import battleship.client.support.DummyPlayer;

public class PlayTwoDummies {
	
	public static void main(String[] args) {
		DummyPlayer.genRandomPlayer    ("Random Shooter",     "localhost", 3001, 500).start();
		DummyPlayer.genMethodicalPlayer("Methodical Shooter", "localhost", 3001, 500).run();
	}
	
}
