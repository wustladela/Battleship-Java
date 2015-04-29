package battleship.game;

import hawup.client.Lab4Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import lecturens2.SIngleServerQueue;

public class Server {
	final private int port;
	public int numPlayers;//keep track of how many players
	private SingleServerQueue queue;
	public boolean whoTurn;
	public Board board;
	public HitResult hr;
	public ArrayList<Socket> playerSockets;
	public Server(int port) {
		this.port = port;
		this.queue = new SingleServerQueue();
		queue.start();
		numPlayers = 0;
		whoTurn = false;//alternate between 1 and 2, so 1+whoTurn.
		playerSockets = new ArrayList<Socket>();
	}

	public void run() throws IOException{
		ServerSocket ss = new ServerSocket(port);
		while (true){
			final Socket s = ss.accept(); // accept the first connection
			playerSockets.add(s);
			queue.addToQueue(
					new Runnable() {
						public void run() {
							try {
								//these are for both players... listens for each socket from each thread
								OutputStream os = s.getOutputStream();
								InputStream is = s.getInputStream();
								DataInputStream dis = new DataInputStream(is);
								DataOutputStream dos = new DataOutputStream(os);
								//first step
								String clientName = dis.readUTF();
								String pwd = dis.readUTF();
								if(!pwd.equals("xyzzy")){
									dos.writeUTF("bye");
									s.close();
								}
								int canJoin = dis.readByte();
								//respond
								dos.writeUTF("ron");
								int playerId;
								if(canJoin>0){
									numPlayers++;
									playerId = 1 + numPlayers%2;
								}
								else{
									playerId = 0;
								}
								dos.writeByte(playerId);
								//config step for player
								if(playerId>0){
									dos.writeUTF("config");
									int numRow = 20;//as a two-byte integer?
									int numCol = 20;
									int numShips = 5;
									/**
									 * make the board
									 */
									board = new Board(numRow, numCol);
									hr = new HitResult(board.getPCS());
									
									Ship[] allShips = new Ship[numShips];
									/**
									 * create ships with arbituary coordinates
									 */
									for(int i=0; i<numShips; i++){
										Set<Coordinate> temp = new HashSet<Coordinate>(i, i+1);//just have tiny ships for now
										allShips[i] = new Ship(temp);
										boolean placeShips = board.placeShip(allShips[i]);
										if(placeShips==false){
											dos.writeUTF("bye");
											s.close();
										}
									}

									dos.writeShort(numRow);
									dos.writeShort(numCol);
									dos.writeByte(numShips);
									for(int i=0; i<numShips; i++){
										dos.writeByte(1);//also just length of ship!
									}

									//get next row about where player places the ship
									int[] shipOrient = new int[numShips];
									Coordinate[] shipLocations = new Coordinate[numShips];

									for(int i=0; i<numShips; i++){
										shipOrient[i] = dis.readByte();
										int tempRow = dis.readShort();
										int tempCol = dis.readShort();
										Coordinate temp = new Coordinate(tempRow, tempCol);
										shipLocations[i] = temp;
									}
									dos.writeUTF("ok");

									//From here, the server sends all messages to both clients.
									/**
									 * player turn and etc
									 * check for player turn!
									 */
									dos.writeUTF("PlayerTurn");
									int myTurn = (whoTurn) ? 1 : 0;
									dos.writeByte(myTurn+1);
									System.out.println("myTurn = "+myTurn);
									whoTurn=!whoTurn;

									/**
									 * how to check for socket identity: listen for threads
									 * if move exists then do stuff
									 * if message exists then do stuff
									 * if not, ...
									 */

									System.out.println("prepare to do this!");
//									InputStream firstP = playerSockets.get(0).getInputStream();
//									InputStream secondP = playerSockets.get(1).getInputStream();
//									DataInputStream dis01 = new DataInputStream(firstP);
//									System.out.println("made it here. can I have multiple getInputStream? dis01 = "+dis01);
//									DataInputStream dis02 = new DataInputStream(secondP);
									String from1 = dis.readUTF();
									if(from1==null){
										System.out.println("this is null");
									}
									System.out.println("from1 = "+from1);
									String from2 = dis.readUTF();
									int fireRow;
									int fireCol;
									if(from1!=null && from1.equals("move")){
										System.out.println("made it to 131");	
										if(myTurn==0){
											fireRow = dis.readShort();
											fireCol = dis.readShort();

											dos.writeUTF("PlayerFires");
											dos.writeByte(1);
											dos.writeShort(fireRow);
											dos.writeShort(fireCol);
											/**
											 * The string hit or miss
The 											string sunk or notsunk
The 											string win or nowin
											 */

										}
										//not your turn. just listen and do nothing
										else{
											if(from1!=null && from1.equals("move")){
												//just listen
												fireRow = dis.readShort();
												fireCol = dis.readShort();
											}
											if(from1!=null && from1.equals("message")){
												
												queue.addToQueue(new Runnable(){
													//process message
													@Override
													public void run() {
														try{
															InputStream firstP = playerSockets.get(0).getInputStream();
															InputStream secondP = playerSockets.get(1).getInputStream();
															DataInputStream dis01 = new DataInputStream(firstP);
															DataInputStream dis02 = new DataInputStream(secondP);
															String content = dis01.readUTF();
															dos.writeUTF("broadcast");
															dos.writeUTF("player2");//name of player...
															dos.writeUTF(content);
														}catch(Throwable t){
															throw new Error("saw error " + t);
														}
													}
												});
											}	
										}
									}
									if(from2!=null && from2.equals("move")){
										if(myTurn==1){
											fireRow = dis.readShort();
											fireCol = dis.readShort();

											dos.writeUTF("PlayerFires");
											dos.writeByte(1);
											dos.writeShort(fireRow);
											dos.writeShort(fireCol);
											/**
											 * The string hit or miss
 											   the string sunk or notsunk
 											   the string win or nowin
											 */

										}
										//not your turn. just listen and do nothing
										else{
											if(from2!=null && from2.equals("move")){
												//just listen
												fireRow = dis.readShort();
												fireCol = dis.readShort();
											}
											if(from2!=null && from2.equals("message")){
												//new thread!
												queue.addToQueue(new Runnable(){
													//process message
													@Override
													public void run() {
														try{
															InputStream firstP = playerSockets.get(0).getInputStream();
															InputStream secondP = playerSockets.get(1).getInputStream();
															DataInputStream dis01 = new DataInputStream(firstP);
															DataInputStream dis02 = new DataInputStream(secondP);
															String content = dis02.readUTF();
															dos.writeUTF("broadcast");
															dos.writeUTF("player2");//name of player...
															dos.writeUTF(content);
														}catch(Throwable t){
															throw new Error("saw error " + t);
														}
													}
												});
												
											}
										}

									}
								}
								//observers. Not needed
								else{

								}

							} catch(Throwable t) {
								throw new Error("saw error " + t);
							}
						}
					});
			
			
			//
//			queue.addToQueue(new Runnable(){
//
//				@Override
//				public void run() {
//					// FIXME Auto-generated method stub
//					/**
//					 * player turn and etc
//					 * check for player turn!
//					 */
//					try{
//						
//						InputStream firstP = playerSockets.get(0).getInputStream();
//						InputStream secondP = playerSockets.get(1).getInputStream();
//						DataInputStream dis01 = new DataInputStream(firstP);
//						DataInputStream dis02 = new DataInputStream(secondP);
//						OutputStream out01 = playerSockets.get(0).getOutputStream();
//						OutputStream out02 = playerSockets.get(1).getOutputStream();
//						DataOutputStream dos01 = new DataOutputStream(out01);
//						DataOutputStream dos02 = new DataOutputStream(out02);
//						dos01.writeUTF("PlayerTurn");
//						dos02.writeUTF("PlayerTurn");
//						int myTurn = (whoTurn) ? 1 : 0;
//						dos01.writeByte(myTurn+1);
//						dos02.writeByte(myTurn+1);
//						System.out.println("myTurn = "+myTurn);
//						whoTurn=!whoTurn;
//
//						/**
//						 * how to check for socket identity: listen for threads
//						 * if move exists then do stuff
//						 * if message exists then do stuff
//						 * if not, ...
//						 */
//						
//						System.out.println("here 283");
//						String from1 = dis01.readUTF();
//						if(from1==null){
//							System.out.println("here 286");
//							System.out.println("this is null");
//						}
//						System.out.println("from1 = "+from1);
//						String from2 = dis02.readUTF();
//						int fireRow;
//						int fireCol;
//						if(from1!=null && from1.equals("move")){
//							System.out.println("made it to 131");	
//							if(myTurn==0){
//								fireRow = dis01.readShort();
//								fireCol = dis01.readShort();
//								dos01.writeUTF("PlayerFires");
//								dos02.writeUTF("PlayerFires");
//								dos01.writeByte(1);
//								dos02.writeByte(1);
//								dos01.writeShort(fireRow);
//								dos02.writeShort(fireRow);
//								dos01.writeShort(fireCol);
//								dos02.writeShort(fireCol);
//								String resultHit = hr.isHit();
//								String resultSunk = hr.isSunk();
//								String resultWin = hr.isWin();
//								dos01.writeUTF(resultHit);
//								dos01.writeUTF(resultSunk);
//								dos01.writeUTF(resultWin);
//								dos02.writeUTF(resultHit);
//								dos02.writeUTF(resultSunk);
//								dos02.writeUTF(resultWin);
//								/**
//								 * The string hit or miss
//	The 											string sunk or notsunk
//	The 											string win or nowin
//								 */
//
//							}
//							//not your turn. just listen and do nothing
//							else{
//								if(from1!=null && from1.equals("move")){
//									//just listen
//									fireRow = dis01.readShort();
//									fireCol = dis01.readShort();
//								}
//								if(from1!=null && from1.equals("message")){
//									
//									queue.addToQueue(new Runnable(){
//										//process message
//										@Override
//										public void run() {
//											try{
//												InputStream firstP = playerSockets.get(0).getInputStream();
//												InputStream secondP = playerSockets.get(1).getInputStream();
//												DataInputStream dis01 = new DataInputStream(firstP);
//												DataInputStream dis02 = new DataInputStream(secondP);
//												String content = dis01.readUTF();
//												dos01.writeUTF("broadcast");
//												dos02.writeUTF("broadcast");
//												dos01.writeUTF("player2");//name of player...
//												dos02.writeUTF("player2");
//												dos01.writeUTF(content);
//												dos02.writeUTF(content);
//											}catch(Throwable t){
//												throw new Error("saw error " + t);
//											}
//										}
//									});
//								}	
//							}
//						}
//						if(from2!=null && from2.equals("move")){
//							if(myTurn==1){
//								fireRow = dis01.readShort();
//								fireCol = dis01.readShort();
//
//								dos01.writeUTF("PlayerFires");
//								dos02.writeUTF("PlayerFires");
//								dos01.writeByte(1);
//								dos02.writeByte(1);
//								dos01.writeShort(fireRow);
//								dos02.writeShort(fireRow);
//								dos01.writeShort(fireCol);
//								dos02.writeShort(fireCol);
//								/**
//								 * The string hit or miss
//									   the string sunk or notsunk
//									   the string win or nowin
//								 */
//
//							}
//							//not your turn. just listen and do nothing
//							else{
//								if(from2!=null && from2.equals("move")){
//									//just listen
//									fireRow = dis02.readShort();
//									fireCol = dis02.readShort();
//								}
//								if(from2!=null && from2.equals("message")){
//									//new thread!
//									queue.addToQueue(new Runnable(){
//										//process message
//										@Override
//										public void run() {
//											try{
//												InputStream firstP = playerSockets.get(0).getInputStream();
//												InputStream secondP = playerSockets.get(1).getInputStream();
//												DataInputStream dis01 = new DataInputStream(firstP);
//												DataInputStream dis02 = new DataInputStream(secondP);
//												String content = dis02.readUTF();
//												dos01.writeUTF("broadcast");
//												dos02.writeUTF("broadcast");
//												dos01.writeUTF("player2");//name of player...
//												dos02.writeUTF("player2");
//												dos01.writeUTF(content);
//												dos02.writeUTF(content);
//											}catch(Throwable t){
//												throw new Error("saw error " + t);
//											}
//										}
//									});
//									
//								}
//							}
//
//						}
//					}catch(Throwable t){
//						throw new Error("saw error " + t);
//					}
//					
//					
//				}
//				
//			});
			//
			queue.addToQueue(new Runnable(){
				@Override
				public void run() {
					// FIXME Auto-generated method stub

				}

			});
		}
	}

	public static void main(String[] args) throws IOException {
		Server server = new Server(3001);
		server.run();
	}

}
