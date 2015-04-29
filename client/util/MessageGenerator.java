package battleship.client.util;

import java.util.Random;

/**
 * Generates a random message from a stock of words, ending with
 *   some random punctuation.
 * @author roncytron
 *
 */
public class MessageGenerator {
	private static String[] words = new String[] {
		"computer", "science", "fun", "SpongeBob", "Plankton",
		"durian", "eats", "programs", "threads", "synchronized",
		"best class ever", "Let's go Blues", "Have a great summer"
	};
	
	private static String[] punctuation = new String[] {
		"!", ".", "?"
	};
	
	private int maxLength;
	private Random rand;
	public MessageGenerator(int maxLength) {
		this.maxLength = maxLength;
		this.rand      = new Random();
	}
	
	public String nextMessage() {
		int length = rand.nextInt(maxLength) + 1;
		String ans = "";
		for (int i=0; i < length; ++i) {
			if (i != 0) 
				ans = ans + " ";
			ans += words[rand.nextInt(words.length)];
		}
		ans += punctuation[rand.nextInt(punctuation.length)];
		return ans;
	}

}
