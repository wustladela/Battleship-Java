package battleship.client.support;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import battleship.client.util.DebugSockets;
import battleship.client.util.PrintStreamPanel;
import battleship.client.util.StreamsAndTranscriptViz;

/**
 * Generic Client that can talk to the server
 * An extension of this class would actually process the input received
 *   from the server, and react by sending messages on DataOutputStream out
 * @author roncytron
 *
 */
abstract public class ClientSetup extends Thread {

	final private PrintStreamPanel fromServer;
	final private PrintStreamPanel toServer;
	final private PrintStreamPanel transcript;
	final protected PrintStream ps;
	final private String serverIP;
	final private int port;
	final protected String name;
	
	//
	// Used by extensions of Client to write to the server
	//
	protected DataOutputStream out;

	/**
	 * Handles input synchronously in processInput
	 * Output is sent asynchronously by the single-server queue
	 * 
	 * This class is meant to be run in its own Thread after constructed
	 * 
	 * @param name A (hopefully unique) client name
	 * @param serverIP The IP address of the server
	 * @param port The port number
	 */
	public ClientSetup(String name, String serverIP, int port) {
		this.fromServer = new PrintStreamPanel(Color.WHITE, "From Server",170, 18);
		this.toServer   = new PrintStreamPanel(Color.YELLOW, "To Server",170, 18);
		this.transcript = new PrintStreamPanel(Color.GREEN, "Transcript",380, 18);
		this.ps       = transcript.getPrintStream();
		StreamsAndTranscriptViz viz = new StreamsAndTranscriptViz(
				"Client User: " + name,
				fromServer, toServer, transcript);
		viz.setVisible(true);
		this.serverIP = serverIP;
		this.port = port;
		this.name = name;
	}

	public abstract void processInput(DataInputStream dis);

	public void run() {
		try {
			Socket socket = new Socket(serverIP, port);
			DataInputStream in = DebugSockets.genIn(socket, fromServer);
			this.out           = DebugSockets.genOut(socket, toServer);

			processInput(in);

		} catch(Throwable t) {
			t.printStackTrace();
			throw new Error("Client problem " + t);
		}

	}

}
