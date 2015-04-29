package battleship.client.mains;

import battleship.client.support.DummyPlayer;

public class PlayInteractiveWithDummy {
	
	public static void main(String[] args) {
		DummyPlayer.genInteractivePlayer("Expert Student", "localhost", 3001).start();
		DummyPlayer.genMethodicalPlayer("Methodical Shooter", "localhost", 3001, 500).run();
	}
	
}
