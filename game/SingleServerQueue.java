package battleship.game;

import java.util.LinkedList;

public class SingleServerQueue extends Thread {

	private LinkedList<Runnable> queue;

	public SingleServerQueue() {
		this.queue = new LinkedList<Runnable>();
	}

	//
	// wait until something shows up in the queue
	//  and then run it
	//
	public void run() {
		while (true) {
			// 1. wait for something to show up
			// 2. while still holding the lock, remove something from the queue
			// 3. give up the lock
			// 4. run that thing
			Runnable thingToRun = null;
			synchronized(this) {
				while (this.queue.isEmpty()) {
					try {
						wait();
					} catch (InterruptedException e) {
						// FIXME Auto-generated catch block
						e.printStackTrace();
					}
				}
				thingToRun = this.queue.removeFirst();
			}

			thingToRun.run();
		}

	}


	public synchronized void addToQueue(Runnable r) {
		this.queue.add(r);
		notifyAll();
	}
	
	public static void main(String[] args) {
		SingleServerQueue q = new SingleServerQueue();
		// start it up
		q.start();
		
		q.addToQueue(new Runnable() { 
			public void run() {
				System.out.println("Hello");
			}
		});
		q.addToQueue(new Runnable() { 
			public void run() {
				System.out.println("GoodBye");
			}
		});
	}

}
