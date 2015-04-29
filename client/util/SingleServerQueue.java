package battleship.client.util;

import java.util.LinkedList;

/**
 * As presented in class, in case this is helpful to you.
 * @author roncytron
 *
 */
public class SingleServerQueue extends Thread {

	private LinkedList<Runnable> queue;

	public SingleServerQueue() {
		this.queue = new LinkedList<Runnable>();
	}

	// wait for something to show up in the queue, then run it
	public void run() {
		// 1. wait until the queue is not empty  -- start holding the lock
		// 2. pull a Runnable off the queue (still holding the lock)
		//    let go of the lock here
		// 3. Run that runnable

		Runnable r = null;
		while (true) {
			synchronized(this) {
				while (queue.isEmpty()) {
					Wrappers.wait(this);
				}
				r = queue.removeFirst();
			}

			r.run();
		}


	}

	public synchronized void addRunnableToQueue(Runnable r) {
		this.queue.addLast(r);
		Wrappers.notifyAll(this);
	}

	public static void main(String[] args) {
		SingleServerQueue sq = new SingleServerQueue();
		sq.start();
		sq.addRunnableToQueue(new Runnable() {

			@Override
			public void run() {
				System.out.println("Hello CSE132!");
				
			} } );
		sq.addRunnableToQueue(new Runnable() {

			@Override
			public void run() {
				System.out.println("Goodbye CSE132!");
				
			} } );
	}

}
