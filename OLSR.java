import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.lang.*;

class Node extends JFrame {
	private String name;
	private String configFile;
	private String nodes;
	private int attachedNodes[];
	private Node currentNode;
	private int numberNodes, value, x, y;
	private Node previousNode;
	public String machine;
	public String portNumber;
	private String location;
	public Node allNodes[];
	private File file;
	private File fileLineCount;
	public int distances[];
	private Node nodesConnected[];

	public Node(String nodeIn) { // Constructor for nodes
		name = nodeIn;
		attachedNodes = new int[20];
		distances = new int[20];
		nodesConnected = new Node[20];
		for (int i = 0; i < 20; i++) {
			attachNewNode(0, i);
		}
	}

	public void setNodeName(String nodeName) {
		name = nodeName;
	}

	public String getNodeName() {
		return name;
	}

	public void attachNewNode(int newNode, int index) {
		attachedNodes[index] = newNode;
	}

	public int getAttachedNode(int index) {
		return attachedNodes[index];
	}

	public long checkFileModification() { // Checks to see if file has been
											// modified before sending
		return file.lastModified();
	}

	public void linkNodes(String configFile) { // Links nodes appropriately
		String link;
		Node current;
		file = new File(configFile); // Instance of file
		String line;
		BufferedReader buffer = null;

		int lineCount = 0;
		String lineLineCount;
		try {
			fileLineCount = new File(configFile); // Instance of file
			BufferedReader bufferLineCount = new BufferedReader(new FileReader(fileLineCount));
			while ((lineLineCount = bufferLineCount.readLine()) != null) { // Iterates
																			// through
																			// file
																			// line
																			// by
																			// line
				if (lineLineCount.indexOf(" ") < 0) {
					break;
				}
				lineCount++;
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("lineLineCount!: ");
		// System.out.println(lineCount);
		allNodes = new Node[lineCount];

		try {
			buffer = new BufferedReader(new FileReader(file));
			int added = 0;
			int i = 0;
			while ((line = buffer.readLine()) != null) { // Iterates through
															// file line by line
				if (line.indexOf(" ") < 0) {
					break;
				}
				Node currentNode = new Node(""); // Instance of Node class
				int index = line.indexOf(" ");
				currentNode.setNodeName(line.substring(0, index));
				line = line.replace(currentNode.getNodeName() + " ", "");
				index = line.indexOf(" ");
				currentNode.machine = line.substring(0, index);
				line = line.replace(currentNode.machine + " ", "");
				index = line.indexOf(" ");
				currentNode.portNumber = line.substring(0, index);
				line = line.replace(currentNode.portNumber + " ", "");
				// System.out.println(line);
				index = line.indexOf(" ");
				currentNode.x = Integer.parseInt(line.substring(0, index));
				line = line.replace(line.substring(0, index) + " ", "");
				currentNode.y = Integer.parseInt(line.substring(0));
				;
				allNodes[i] = currentNode;
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int b = 0; b < allNodes.length; b++) { // Calculates the distance
													// between Nodes
			if (allNodes[b] != null) {
				for (int q = 0; q < allNodes.length; q++) {
					allNodes[b].distances[q] = ((int) Math.sqrt((Math.pow(Math.abs(allNodes[b].x - allNodes[q].x), 2)
							+ Math.pow(Math.abs(allNodes[b].y - allNodes[q].y), 2))));
					if (allNodes[b].distances[q] <= 100) {
						allNodes[b].nodesConnected[q] = allNodes[q];
					} else {
						allNodes[b].nodesConnected[q] = null;
					}
				}
			}
		}

		for (int y = 0; y < allNodes.length; y++) {
			for (int z = 0; z < allNodes.length; z++) {
				if (allNodes[y] != null) {
					if (allNodes[y].nodesConnected[z] != null) {
						// System.out.println(allNodes[y].getNodeName() + "
						// Connected to: " +
						// allNodes[y].nodesConnected[z].getNodeName() + "
						// Distance: " + allNodes[y].distances[z]);
					}
				}
			}
		}

	}

	public boolean gremlinFunctionManet(int distance) { // PacketRateDrop
														// function
		Random randSend = new Random();
		int gremlin = randSend.nextInt(100) + 1;
		double results = 100 - (distance / 5);
		results = (int) results;
		// System.out.println(results);
		if (gremlin < (int) results) {
			// return true;
		}
		// if gremlin doesnt drop, then send it to the server
		else {
			// return false;
		}
		return true;

	}

	public boolean inRangeSimulate(int distance) {
		if (distance > 100) {
			return true;
		}
		if (distance == 0) {
			return true;
		} else {
			return false;
		}
	}

	public Node[] getNodesConnected() {
		return nodesConnected;
	}

	// public void setNodesConnected(Node nodesConnected[]) {
	// this.nodesConnected = nodesConnected;
	// }
}

public class OLSR extends Node {
	public OLSR() {
		super("");
	}

	private static DatagramSocket socket;
	public static long originalTimeStamp;
	public static int ip1 = 0;
	public static int ip2 = 0;
	public static int ip3 = 0;
	public static int ip4 = 0;
	public static int g = 0;
	public static Node node = new Node("");
	public static String fileName;
	public static String[][] cache_table = new String[10][10];
	public static int z1 = 0;
	public static int new_z1 = 0;
	public static int noden = 0;

	public static int[][] olsrNeighbors;
	public static int helloMsgTimeout = 0;
	public static int linkBreakPause = 0;
	public static boolean linkBreakBool = false;
	public static int[] recentNeighbors;
	public static boolean timeoutChecked = false;
	public static int[][] fastestRouteHop;

	public static void main(String[] args) throws InterruptedException, IOException {
		helloMsgTimeout = 0;
		if (args.length == 0) {
			System.out.println("Missing argument: Configuration File needed.");
		}
		fileName = args[0];
		node.linkNodes(fileName);
		originalTimeStamp = node.checkFileModification();
		// UDP_MANET client=new UDP_MANET();
		Scanner s = new Scanner(System.in);
		System.out.println("Enter Node : ");
		noden = s.nextInt();

		cache_table[0][0] = "source_addr";
		cache_table[0][1] = "-1";

		// dynamic node checking
		String d_nodename = "Node" + noden;
		int d_nodeindex = -1;
		for (int s1 = 0; s1 < node.allNodes.length; s1++) {
			if (node.allNodes[s1] != null) {
				if (node.allNodes[s1].getNodeName().equals(d_nodename)) {
					d_nodeindex = s1;
					break;
				}
			}
		}
		if (d_nodeindex == -1) {
			System.exit(0);
		}
		// dynamic node checking
		System.out.println("Current Port : " + node.allNodes[d_nodeindex].portNumber);
		int port = Integer.parseInt(node.allNodes[d_nodeindex].portNumber);

		socket = new DatagramSocket(port);

		// hello thread for updating table
		Thread hello = new Thread(new helloSend());
		hello.start();
		// hello thread for updating table
		olsrNeighbors = new int[node.allNodes.length][node.allNodes.length];
		recentNeighbors = new int[node.allNodes.length];
		 // first var is num hops, second var is next hop
		fastestRouteHop = new int[node.allNodes.length][2];
															

		while (true) {

			try {

				if (node.checkFileModification() > originalTimeStamp) {
					System.out.println("Config file has been modified.");
					originalTimeStamp = node.checkFileModification();
					node.linkNodes(fileName);
				}
				d_nodeindex = -1;
				for (int s1 = 0; s1 < node.allNodes.length; s1++) {
					if (node.allNodes[s1] != null) {
						if (node.allNodes[s1].getNodeName().equals(d_nodename)) {
							d_nodeindex = s1;
							break;
						}
					}
				}
				if (d_nodeindex == -1) {
					System.exit(0);
				}

				byte buff1[] = new byte[128];
				DatagramPacket packet = new DatagramPacket(buff1, buff1.length);

				socket.setSoTimeout(1000);

				socket.receive(packet);

				String r_p_server = new String(packet.getData()).trim();
				System.out.println("-----");
				log("Message: " + r_p_server);

				/*******************************************************************************
				 * BEGIN handle hello Messages
				 *******************************************************************************/

				if (r_p_server.contains("HH")) {
					log("Hello Message");
					int sourceNode = Integer.parseInt(
							r_p_server.substring(r_p_server.indexOf("H") + 1, r_p_server.indexOf("HH")).trim());
					// log("hello received " + r_p_server);
					recentNeighbors[sourceNode] = 1;
					if (olsrNeighbors[sourceNode][sourceNode] == 0) {
						olsrNeighbors[sourceNode][sourceNode] = 1;
						log("neighbor added: " + (sourceNode + 1));
					}
					if (!linkBreakBool) {
						fastestRouteHop[sourceNode][0] = 1;
						fastestRouteHop[sourceNode][1] = sourceNode;

					}
					int[] sourceNeighbors = new int[node.allNodes.length];
					while (r_p_server.contains("NN")) {
						int sourceNeighbor = Integer.parseInt(
								r_p_server.substring(r_p_server.indexOf("N") + 1, r_p_server.indexOf("NN")).trim());
						sourceNeighbors[sourceNeighbor] = 1;
						r_p_server = r_p_server.replace("N" + sourceNeighbor + "NN", "");
					}

					while (r_p_server.contains("Num")) {
						int destNode = Integer.parseInt(
								r_p_server.substring(r_p_server.indexOf("FF") + 2, r_p_server.indexOf("To")).trim());
						int distLength = Integer.parseInt(
								r_p_server.substring(r_p_server.indexOf("To") + 2, r_p_server.indexOf("Num")).trim());
						int nextHop = Integer.parseInt(
								r_p_server.substring(r_p_server.indexOf("Num") + 3, r_p_server.indexOf("Next")).trim());
						if (nextHop != (noden - 1) && destNode != (noden - 1) && distLength != 0
								&& fastestRouteHop[destNode][0] > (distLength + 1)) {
							log("New fastest route to " + (destNode + 1) + " through " + (sourceNode + 1) + " in "
									+ (distLength + 1) + " hops.");
							fastestRouteHop[destNode][0] = (distLength + 1);
							fastestRouteHop[destNode][1] = sourceNode;
						}
						if (nextHop != (noden - 1) && destNode != (noden - 1) && distLength != 0
								&& fastestRouteHop[destNode][0] == 0 && distLength != 0) {
							log("New fastest route to " + (destNode + 1) + " through " + (sourceNode + 1) + " in "
									+ (distLength + 1) + " hops.");
							fastestRouteHop[destNode][0] = (distLength + 1);
							fastestRouteHop[destNode][1] = sourceNode;
						}
						if (distLength == 0 && fastestRouteHop[destNode][1] == sourceNode) {
							fastestRouteHop[destNode][0] = 0;
							fastestRouteHop[destNode][1] = 0;
						}
						r_p_server = r_p_server.replace(destNode + "To" + distLength + "Num" + nextHop + "Next", "");
					}

					for (int i = 0; i < sourceNeighbors.length; i++) {
						if (i != (noden - 1) && i != sourceNode) {
							if (olsrNeighbors[sourceNode][i] == 0 && sourceNeighbors[i] == 1) {
								olsrNeighbors[sourceNode][i] = 1;
								log("neighbor #" + (sourceNode + 1) + "'s neighbor added: " + (i + 1));
							}
							if (olsrNeighbors[sourceNode][i] == 1 && sourceNeighbors[i] == 0) {
								olsrNeighbors[sourceNode][i] = 0;
								log("neighbor #" + (sourceNode + 1) + " lost neighbor #" + (i + 1));
								fastestRouteHop = new int[node.allNodes.length][2];
								linkBreakPause = 1;
								linkBreakBool = true;
							}
						}
					}

					// if we havent received a hello from them in ~2s then we consider them lost
					if (helloMsgTimeout == 0 && !timeoutChecked) { 
						timeoutChecked = true;
						for (int i = 0; i < recentNeighbors.length; i++) {
							if (olsrNeighbors[i][i] == 1 && recentNeighbors[i] == 0) {
								log("Havent heard from node, dropping: " + (i + 1));
								olsrNeighbors[i] = new int[node.allNodes.length];
								fastestRouteHop = new int[node.allNodes.length][2];
								linkBreakPause = 1;
								linkBreakBool = true;
							}
						}
						recentNeighbors = new int[node.allNodes.length];
					}
					// SHOW ROUTE INFO
					/*
					 * for(int i = 0; i < fastestRouteHop.length; i++) {
					 * log("Fastest route to " + (i + 1) + " is " +
					 * fastestRouteHop[i][0] + " hops, through " +
					 * (fastestRouteHop[i][1] + 1)); }
					 */
				}
				else {
					log("Different Message..");
					String source_addr = r_p_server.substring(r_p_server.indexOf("SR") + 2, r_p_server.indexOf("PN"))
							.trim();
					System.out.println("Source: " + source_addr);
					String dest_addr = r_p_server.substring(r_p_server.indexOf("DR") + 2, r_p_server.indexOf("SR"))
							.trim();
					System.out.println("Dest: " + dest_addr);
					
					int i1 = r_p_server.indexOf("P");
					int j1 = 0;
					if (r_p_server.contains("H")) {
						j1 = r_p_server.indexOf("H");
					} else if (r_p_server.contains("T")) {
						j1 = r_p_server.indexOf("T");
					} else if (r_p_server.contains("L")) {
						j1 = r_p_server.indexOf("L");
					} else if (r_p_server.contains("X")) {
						j1 = r_p_server.indexOf("X");
					}

					String packetNum = "";

					if (j1 == 0) {
						int index = r_p_server.indexOf("DR");
						packetNum = r_p_server.substring(i1 + 1, index);
						System.out.println("Packet Number: " + packetNum);
					} else {
						packetNum = r_p_server.substring((i1 + 1), j1);
						System.out.println("Packet Number: " + packetNum);
					}
					
					int test = Integer.parseInt(packetNum);
					int test2 = Integer.parseInt(cache_table[0][1]);
					if (r_p_server.contains("Z")) {
						int z2 = r_p_server.indexOf("Z");
						new_z1 = Integer.parseInt(r_p_server.substring(0, z2));
					} else {
						z1 = 0;
						new_z1 = 0;
					} 
					
					if (Integer.parseInt(packetNum) > Integer.parseInt(cache_table[0][1])
							|| (new_z1 > z1 && r_p_server.contains("Z"))) {
						z1 = new_z1;
						if (!r_p_server.contains("A")) {
							cache_table[0][1] = packetNum;
						}

						String previous_node = r_p_server.substring(r_p_server.indexOf("PN") + 2).trim();
						System.out.println("Previous Node: " + previous_node);
						r_p_server = r_p_server.substring(0, r_p_server.indexOf("PN"));
						r_p_server = r_p_server.trim() + "PN" + (noden);
						
//						boolean notInRange = node.inRangeSimulate(node.allNodes[noden - 1].distances[dest_addr]);
						log("Trying to forward packet..");
						log("" + node.inRangeSimulate(node.allNodes[noden - 1].distances[Integer.parseInt(dest_addr)]));
						
						byte buff[] = r_p_server.getBytes();
						if(!node.inRangeSimulate(node.allNodes[noden - 1].distances[Integer.parseInt(dest_addr)])) {
							// InetAddress addressT =
							// InetAddress.getByName(node.allNodes[Integer.parseInt(dest_addr)].machine
							// + ".eng.auburn.edu");
							InetAddress addressT = InetAddress.getLocalHost();
							DatagramPacket packetSend = new DatagramPacket(buff, buff.length, addressT,
									Integer.parseInt(node.allNodes[Integer.parseInt(dest_addr)].portNumber));
							socket.send(packetSend);
						}
						else {
							// InetAddress addressT =
							// InetAddress.getByName(node.allNodes[Integer.parseInt(dest_addr)].machine
							// + ".eng.auburn.edu");
							InetAddress addressT = InetAddress.getLocalHost();
							DatagramPacket packetSend = new DatagramPacket(buff, buff.length, addressT,
									Integer.parseInt(node.allNodes[fastestRouteHop[Integer.parseInt(dest_addr)][1]].portNumber));
							socket.send(packetSend);
						}
						

							
						
//						  int[] numNeighbors = new int[node.allNodes.length];
//						  int[] numHopNeighbor = new int[node.allNodes.length];
//						int[] shouldForward = new int[node.allNodes.length];
//						for (int i = 0; i < node.allNodes.length; i++) {
//							if (i != (noden - 1) && olsrNeighbors[i][i] == 1) {
//								numHopNeighbor[i] = 1;
//								for (int j = 0; j < node.allNodes.length; j++) {
//									if (i != j && j != (noden - 1)) {
//										if (olsrNeighbors[i][j] == 1) {
//											numHopNeighbor[j] = 2;
//										}
//										if (olsrNeighbors[j][j] != 1) { // it
//																		// isn't
//																		// a
//																		// 1-hop
//																		// neighbor
//																		// of
//																		// ours
//											numNeighbors[i]++;
//										}
//									}
//								}
//							}
//						}
//						
						
					}
					
				}
				
				

				/*******************************************************************************
				 * END handle hello Messages
				 *******************************************************************************/

/*

						
						  int[] numNeighbors = new int[node.allNodes.length];
						  int[] numHopNeighbor = new int[node.allNodes.length];
						  int[] shouldForward = new int[node.allNodes.length];
						  for (int i = 0; i < node.allNodes.length; i++) {
	if (i != (noden - 1) && olsrNeighbors[i][i] == 1) {
						  numHopNeighbor[i] = 1; for (int j = 0; j <
						  node.allNodes.length; j++) { if (i != j && j !=
						  (noden - 1)) { if (olsrNeighbors[i][j] == 1) {
						  numHopNeighbor[j] = 2; } if (olsrNeighbors[j][j] !=
						  1) { //it isn't a 1-hop neighbor of ours
						 numNeighbors[i]++; } } } } }
						 
//						log ("dest_addr");
//						log (dest_addr);
						
						

//						boolean notInRange = node.inRangeSimulate(node.allNodes[noden - 1].distances[i]);
//						log("Trying to forward packet..");
//						log("" + node.inRangeSimulate(node.allNodes[noden - 1].distances[3]));


						// for(int i = 0; i < node.allNodes.length; i++) {
						// //System.out.println("buffer length"+buff.length);
						// // InetAddress addressT = InetAddress.getLocalHost();
						// System.out.println(node.allNodes[d_attachnodeindex].machine);
						// System.out.println("an_port: " + an_port);
						//
						// boolean result =
						// node.gremlinFunctionManet(node.allNodes[d_nodeindex].distances[d_attachnodeindex]);
						// if (!result) {
						// System.out.println("Target out of range.");
						// }
						// else {
						// System.out.println("Target in range, sending..");
						//
						// }
						// }

						*//*******************************************************************************
						 * END logic for which neighbors to send to, and which
						 * should forward
						 *******************************************************************************//*
						
						 * Notes: olsrNeighbors array: size of the array is
						 * total# nodes by total # nodes if the array has a '1'
						 * at a olsrNeighbors[x][y], and x and y are equal, then
						 * it is a 1-hop neighbor if x and y are different, then
						 * x is the 1-hop neighbor, and you can reach y through
						 * x deciding where to send:
						 

//						for (int i = 0; i <= 0; i++) {
//							System.out.println("IN0.5" + node.allNodes.length);
//							// System.out.println(d_nodeindex);
//							// System.out.println(node.allNodes[d_nodeindex].getAttachedNode(0));
//							// System.out.println(node.allNodes[d_nodeindex].getAttachedNode(1));
//							// if(node.allNodes[d_nodeindex].getAttachedNode(i)==0)
//							if (false) {
//								System.out.println("IN1" + node.allNodes.length);
//								break;
//							}
//
//							int d_attachnodeindex = -1;
//							for (int s1 = 0; s1 < node.allNodes.length; s1++) {
//								System.out.println("IN1.5");
//								if (node.allNodes[s1] != null) {
//
//									System.out.println("IN2");
//									if (node.allNodes[s1].getNodeName()
//											.equals("Node" + node.allNodes[d_nodeindex].getAttachedNode(i))) {
//										System.out.println(node.allNodes[s1].getNodeName());
//										d_attachnodeindex = s1;
//										break;
//									}
//
//								}
//
//							}
//
//							System.out.println(d_attachnodeindex);
//							System.out.println("IN3");
//							if (d_attachnodeindex != -1) {
//								System.out.println("IN4");
//								int an = node.allNodes[d_nodeindex].getAttachedNode(i);
//
//								if (node.allNodes[d_nodeindex].distances[d_attachnodeindex] < 100) {
//									System.out.println("IN5");
//
//									if (Integer.parseInt(previous_node) != an && Integer.parseInt(source_addr) != noden
//											&& Integer.parseInt(source_addr) != an) {
//										System.out.println("IN6");
//										int an_port = Integer.parseInt(node.allNodes[d_attachnodeindex].portNumber);
//										// Thread.sleep(1000); //pause for
//										// readability
//										System.out.println("r_p_server : " + r_p_server);
//										System.out.println("Forwarding from port :" + port + "to :" + an_port);
//										byte buff[] = r_p_server.getBytes();
//										// System.out.println("buffer
//										// length"+buff.length);
//										// InetAddress addressT =
//										// InetAddress.getLocalHost();
//										// InetAddress addressT =
//										// InetAddress.getByName(node.allNodes[d_attachnodeindex].machine
//										// + ".eng.auburn.edu");
//										InetAddress addressT = InetAddress.getLocalHost();
//										System.out.println(node.allNodes[d_attachnodeindex].machine);
//										System.out.println("an_port: " + an_port);
//										DatagramPacket packetSend = new DatagramPacket(buff, buff.length, addressT,
//												an_port);
//
//										boolean result = node.gremlinFunctionManet(
//												node.allNodes[d_nodeindex].distances[d_attachnodeindex]);
//										if (!result) {
//											System.out.println("Target out of range.");
//										} else {
//											System.out.println("Target in range, sending..");
//											socket.send(packetSend);
//
//										}
//									}
//								}
//							}
//						}
					}
				}*/
			} catch (SocketTimeoutException e) {
				// if we havent received a hello from them in ~3s then we consider them lost
				if (helloMsgTimeout == 0 && !timeoutChecked) { 
					timeoutChecked = true;
					for (int i = 0; i < recentNeighbors.length; i++) {
						if (olsrNeighbors[i][i] == 1 && recentNeighbors[i] == 0) {
							log("Havent heard from node, dropping: " + (i + 1));
							olsrNeighbors[i] = new int[node.allNodes.length];
							fastestRouteHop = new int[node.allNodes.length][2];
							linkBreakPause = 1;
							linkBreakBool = true;
						}
					}
					recentNeighbors = new int[node.allNodes.length];
				}
				continue;
			} catch (IOException ex) {
				System.out.println(ex.getMessage());

			}
		}
	}

	public static void log(int stringIn) {
		System.out.println(stringIn);
	}

	public static void log(String stringIn) {
		System.out.println(stringIn);
	}

	public static class helloSend implements Runnable {
		public void run() {
			System.out.println("Started Hello Thread!");

			while (true) {
				try {
					Thread.sleep(1000); // pause
				} catch (InterruptedException ex) {
					System.out.println(ex.getMessage());
				}
				// System.out.println("\nSending Hello!" + node.allNodes.length
				// + noden);
				// System.out.println("Sending Hello!" +
				// helloMsgTimeout);linkBreakPause
				if (linkBreakPause == 0) {
					linkBreakBool = false;
				}
				linkBreakPause = (linkBreakPause + 1) % 3;
				helloMsgTimeout = (helloMsgTimeout + 1) % 2;

				if (helloMsgTimeout == 0) {
					timeoutChecked = false;
				}
				for (int i = 0; i < node.allNodes.length; i++) {
					// log(node.allNodes[noden - 1].distances[i]);
					boolean notInRange = node.inRangeSimulate(node.allNodes[noden - 1].distances[i]);
					if (notInRange) {
						// System.out.println("Hello message can't reach
						// target");
					} else {
						// System.out.println("Hello message can reach target,
						// sending.." + linkBreakBool);

						String helloMessage = "H" + (noden - 1) + "HH";
						for (int j = 0; j < node.allNodes.length; j++) {
							 // dont add self as a neighbor
							if (j != (noden - 1) && !linkBreakBool) {
								if (olsrNeighbors[j][j] == 1) {
									helloMessage += "N" + (j) + "NN";
								}
							}
						}
						helloMessage += "FF";
						for (int k = 0; k < fastestRouteHop.length; k++) {
							if (fastestRouteHop[k][0] != 0 && k != (noden - 1) && !linkBreakBool) {
								helloMessage += k + "To" + fastestRouteHop[k][0] + "Num" + fastestRouteHop[k][1]
										+ "Next";
							}
						}
						byte hbuff[] = helloMessage.getBytes();

						try {
							// InetAddress addressT =
							// InetAddress.getByName(node.allNodes[i].machine
							// + ".eng.auburn.edu");
							InetAddress addressT = InetAddress.getLocalHost();
							DatagramPacket packetSend = new DatagramPacket(hbuff, hbuff.length, addressT,
									Integer.parseInt(node.allNodes[i].portNumber));
							if (Integer.parseInt(node.allNodes[i].portNumber) != 10169) {
								socket.send(packetSend);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}
			}
		}

		public void log(int stringIn) {
			System.out.println(stringIn);
		}

		public void log(String stringIn) {
			System.out.println(stringIn);
		}
	}
}
