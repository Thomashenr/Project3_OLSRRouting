import java.awt.Dimension;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.lang.*;

public class Client extends JFrame {
	private static int bufferSize = 100;

	private static DatagramSocket socket;
	public static int c = 0;
	public static int ack = 1;
	public static String r = "1";
	public static String msg = "";
	public static String old_msg = "0";
	public static int counter1=0;

	public static int ip1 = 0;
	public static int ip2 = 0;
	public static int ip3 = 0;
	public static int ip4 = 0;
	public static int user_input_d_node=0;
	public static int g = 0;
	public static int g2 = 0;
	public static int gnew = 0;
	public static int g2new = 0;
	public static boolean alternator = true;
	public static int manetCount = 0;
	public static int ethernetCount = 0;	

	private static DatagramSocket socketHR;
	private static DatagramSocket socketLoc;
	private static DatagramSocket socketAir;
	private static DatagramSocket socketToxic;

	private static int indexHRRec = 0;
	private static int indexHRSend = 0;
	private static String bufferHR[] = new String[bufferSize];

	private static int indexLocRec = 0;
	private static int indexLocSend = 0;
	private static String bufferLoc[] = new String[bufferSize];

	private static int indexAirRec = 0;
	private static int indexAirSend = 0;
	private static String bufferAir[] = new String[bufferSize];

	public static int indexToxicRec = 0;
	private static int indexToxicSend = 0;
	private static String bufferToxic[] = new String[bufferSize];

	// Window for Sensor1
	public static JFrame s1 = new JFrame("Sensor Hub");
	public static JTextArea m1 = new JTextArea();

	public static void main(String[] args) throws InterruptedException, IOException {

		Thread hr = new Thread(new SensorHR());
		hr.start();
		Thread loc = new Thread(new SensorLoc());
		loc.start();
		Thread air = new Thread(new SensorAir());
		air.start();
		Thread toxic = new Thread(new SensorToxic());
		toxic.start();
		Thread userInput = new Thread(new UserInput());

		// s1.add(m1);
		s1.add(new JScrollPane(m1));
		s1.setSize(new Dimension(450, 350));
		s1.setBounds(100, 500, 450, 350);
		s1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s1.setVisible(true);
		m1.setEditable(false);

		Client client = new Client();
		Scanner s = new Scanner(System.in);
		System.out.println("Enter IP with numbers of Server separated by 'Enter' : ");
		ip1 = s.nextInt();
		ip2 = s.nextInt();
		ip3 = s.nextInt();
		ip4 = s.nextInt();
		s.nextLine();
		System.out.println("Enter Node number of destination : ");
		user_input_d_node=s.nextInt();
		System.out.println("Enter Gremlin Function Value (Ethernet) (0-100) : ");
		g = s.nextInt();
		gnew = g;
		System.out.println("Enter Gremlin Function Value (Manet) (0-100) : ");
		g2 = s.nextInt();
		g2new = g2;
		userInput.start();
		
		
		try {
			socket = new DatagramSocket(10164); //set to node1 port# value
		} catch (SocketException ex) {
			System.out.println("Failed to Create Socket!");
			System.exit(1);
		}

		// for demo handling

		while (c != -1) { // have it set to stop never
			Thread.sleep(40); // pause for readability
			if (gnew != g || g2new != g2) {
				System.out.println("Gremlin Values Updated!" + gnew + " and " + g2new);
				g = gnew;
				g2 = g2new;
			}

			msg = "";
			msg = "P" + c; // add the packet number

			// creating the message string based on sensors that have data
			boolean newData = false;
			if (indexHRRec != indexHRSend) {
				msg = msg + "" + bufferHR[indexHRSend].trim();
				indexHRSend = incrementIndex(indexHRSend);
				newData = true;
			}
			if (indexAirRec != indexAirSend) {
				msg = msg + "" + bufferAir[indexAirSend].trim();
				indexAirSend = incrementIndex(indexAirSend);
				newData = true;
			}
			if (indexLocRec != indexLocSend) {
				msg = msg + "" + bufferLoc[indexLocSend].trim();
				indexLocSend = incrementIndex(indexLocSend);
				newData = true;
			}
			if (indexToxicRec != indexToxicSend) {
				msg = msg + "" + bufferToxic[indexToxicSend].trim();
				indexToxicSend = incrementIndex(indexToxicSend);
				newData = true;
			}
			if (newData) {
				// saving old message in case we need to retransmit
				msg = msg.trim() + "DR7" + "SR1" + "PN1";
				old_msg = msg;
				// sending packet
				
				
				
				if (g == 100 && g2 == 100) {
					m1.insert("\n\nEthernet and MANET down, awaiting connection restoration..", 0);
				}
				else if (g == g2) {
					if (alternator) {
						client.sendPacketEthernet();
						alternator = false;
					}
					else {
						client.sendPacket();
						alternator = true;
					}
				}
				else if (g < g2) {
					client.sendPacketEthernet();
					//alternator = false;
				}
				else if (g > g2) {
					client.sendPacket();
					//alternator = true;
				}
				
				/*
				if (g < 100 && g2 < 100) {
					if (alternator) {
						client.sendPacketEthernet();
						alternator = !alternator;
					}
					else {
						client.sendPacket();
						alternator = !alternator;
					}
				}
				else if (g < 100) {
					client.sendPacketEthernet();
				}
				else if (g2 < 100) {
					client.sendPacket();
				}
				else {
					m1.insert("\n\nEthernet and MANET down, awaiting connection restoration..", 0);
				}
				*/
				// preparing to recieve ACK
				r = client.readyToReceivPacket();
				c++;
				c++;
			}
		}
	}

	// function to send the packet
	public void sendPacket() throws InterruptedException {
		// UDP_Client rpacket = new UDP_Client();
		manetCount++;
		m1.insert("\n\nSending message packet: " + msg + " via MANET (" + manetCount + ")", 0);
		try {
			byte buff[] = msg.getBytes();
			//byte[] ipAddr = new byte[] { (byte) ip1, (byte) ip2, (byte) ip3, (byte) ip4 };
			//InetAddress addressT = InetAddress.getByAddress(ipAddr);
			
			//HARD CODE to machine name of 2nd node
//			InetAddress addressT = InetAddress.getByName("tux204.eng.auburn.edu");
			InetAddress addressT = InetAddress.getLocalHost();
			
			
			
			// gremlin function to determin if the packet is dropped
			boolean gremlin = gremlinFunction();
			if (gremlin == false) {
				m1.insert("\nPacket Number " + c + " Dropped!", 0);
			}
			// if gremlin doesnt drop, then send it to the server
			else {
				DatagramPacket packetSend = new DatagramPacket(buff, buff.length, addressT, 10165);
				socket.send(packetSend);
			}
		} catch (IOException ex) {
			m1.insert("\n" + ex.getMessage(), 0);
		}
	}

	public void sendPacketEthernet() {
		// UDP_Client rpacket = new UDP_Client();
		ethernetCount++;
		m1.insert("\n\nSending message packet: " + msg + " via Ethernet (" + ethernetCount + ")", 0);
		try {
			byte buff[] = msg.getBytes();
			byte[] ipAddr = new byte[] { (byte) ip1, (byte) ip2, (byte) ip3, (byte) ip4 };
//			InetAddress addressT = InetAddress.getByAddress(ipAddr);
			InetAddress addressT = InetAddress.getLocalHost();
			//addressT = InetAddress.getByName("tux201.eng.auburn.edu");
			// gremlin function to determine if the packet is dropped
			boolean gremlin = gremlinFunctionEthernet();
			if (gremlin == false) {
				m1.insert("\nPacket Number " + c + " Dropped!", 0);
			}
			// if gremlin doesnt drop, then send it to the server
			else {
				DatagramPacket packetSend = new DatagramPacket(buff, buff.length, addressT, 10169);
				socket.send(packetSend);
			}
		} catch (IOException ex) {
			m1.insert("\n" + ex.getMessage(), 0);
		}
		
	}
	
	public boolean gremlinFunction() {
		Random randSend = new Random();
		int gremlin = randSend.nextInt(100) + 1;
		if (gremlin < g2) {
			return false;
		}
		// if gremlin doesnt drop, then send it to the server
		else {
			return true;
		}

	}	
	public boolean gremlinFunctionEthernet() {
		Random randSend = new Random();
		int gremlin = randSend.nextInt(100);
		if (gremlin < g) {
			return false;
		}
		// if gremlin doesnt drop, then send it to the server
		else {
			return true;
		}

	}

	public String readyToReceivPacket() throws InterruptedException {
		int counter = 0;
		while (true) {
			if (gnew != g || g2new != g2) {
				System.out.println("Gremlin Values Updated!" + gnew + " and " + g2new);
				g = gnew;
				g2 = g2new;
			}
			if (counter == 0) {
				try {
					byte buff1[] = new byte[128];
					DatagramPacket packet = new DatagramPacket(buff1, buff1.length);
					counter = 1;

					socket.setSoTimeout(40); // set timeout for if a packet is
												// lost
					socket.receive(packet);
					counter1=0;
					String r_p_server = new String(packet.getData());
					// int i1=r_p_server.indexOf("P");
					// int index = r_p_server.indexOf("DR");
				      //   c = Integer.parseInt(r_p_server.substring(i1 + 1, index).trim());
				        // System.out.println("Packet Number: " + c );
					return r_p_server;
				} catch (IOException ex) {
					m1.insert("\n" + ex.getMessage(), 0);
				}
			} else {// if a packet has been dropped, then here we resend it
				Client client1 = new Client();
				m1.insert("\nResend!", 0);
				
				
				
				// sending packet
				if(!msg.contains("Z"))
				{
					counter1++;
					msg = counter1+"Z"+ msg;
				}
				else
				{
					counter1++;
					int z1 = msg.indexOf("Z");
					msg=msg.substring(z1+1);
					msg = counter1+"Z"+ msg;
				}
				
				
				
				if (g == 100 && g2 == 100) {
					m1.insert("\n\nEthernet and MANET down, awaiting connection restoration..", 0);
				}
				else if (g == g2) {
					if (alternator) {
						client1.sendPacketEthernet();
						alternator = false;
					}
					else {
						client1.sendPacket();
						alternator = true;
					}
				}
				else if (g < g2) {
					client1.sendPacketEthernet();
				}
				else if (g > g2) {
					client1.sendPacket();
				}
				
				/*
				if (g < 100 && g2 < 100) {
					if (alternator) {
						client1.sendPacketEthernet();
						alternator = !alternator;
					}
					else {
						client1.sendPacket();
						alternator = !alternator;
					}
				}
				else if (g < 100) {
					client1.sendPacketEthernet();
				}
				else if (g2 < 100) {
					client1.sendPacket();
				}
				else {
					m1.insert("\n\nEthernet and MANET down, awaiting connection restoration..", 0);
				}
				*/
				
				//client1.sendPacket();
				counter = 0;
			}
		}
	}

	public static int incrementIndex(int indexIn) {
		indexIn = (indexIn + 1) % 100;
		return indexIn;
	}

	private static class SensorHR implements Runnable {
		public void run() {
			System.out.println("Started HR Thread!");
			byte buff1[] = new byte[128];
			DatagramPacket packet = new DatagramPacket(buff1, buff1.length);
			try {
				socketHR = new DatagramSocket(10160);

			} catch (SocketException ex) {
				System.out.println("Creating HR Socket FAILED...");
				System.exit(1);
			}
			while (true) {
				try {
					socketHR.receive(packet);
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
				String hrData = new String(packet.getData());
				bufferHR[indexHRRec] = hrData;
				indexHRRec = incrementIndex(indexHRRec);
			}
		}
	}

	private static class SensorLoc implements Runnable {
		public void run() {
			System.out.println("Started Loc Thread!");
			byte buff1[] = new byte[128];
			DatagramPacket packet = new DatagramPacket(buff1, buff1.length);
			try {
				socketLoc = new DatagramSocket(10161);
			} catch (SocketException ex) {
				System.out.println("Creating Socket FAILED...");
				System.exit(1);
			}
			while (true) {
				try {
					socketLoc.receive(packet);
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
				String locData = new String(packet.getData());
				bufferLoc[indexLocRec] = locData;
				indexLocRec = incrementIndex(indexLocRec);
			}
		}
	}

	private static class SensorAir implements Runnable {
		public void run() {
			System.out.println("Started Air Thread!");
			byte buff1[] = new byte[128];
			DatagramPacket packet = new DatagramPacket(buff1, buff1.length);
			try {
				socketAir = new DatagramSocket(10162);

			} catch (SocketException ex) {
				System.out.println("Creating Socket FAILED...");
				System.exit(1);
			}
			while (true) {
				try {
					socketAir.receive(packet);
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
				String airData = new String(packet.getData());
				bufferAir[indexAirRec] = airData;
				indexAirRec = incrementIndex(indexAirRec);
			}
		}
	}

	private static class SensorToxic implements Runnable {
		public void run() {
			System.out.println("Started Toxic Thread!");
			byte buff1[] = new byte[128];
			DatagramPacket packet = new DatagramPacket(buff1, buff1.length);
			try {
				socketToxic = new DatagramSocket(10163);

			} catch (SocketException ex) {
				System.out.println("Creating Socket FAILED...");
				System.exit(1);
			}
			while (true) {
				try {
					socketToxic.receive(packet);
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
				String toxicData = new String(packet.getData());
				bufferToxic[indexToxicRec] = toxicData;
				indexToxicRec = incrementIndex(indexToxicRec);
			}
		}
	}

	private static class UserInput implements Runnable {
		public void run() {
			System.out.println(
					"\nAt any time,\nenter updated Ethernet Gemlin values and MANET Gremlin values,\nrespectively and separated by a space...\n");
			Scanner sLive = new Scanner(System.in);
			while (true) {
				int liveInput1 = sLive.nextInt();
				int liveInput2 = sLive.nextInt();
				sLive.nextLine();
				gnew = liveInput1;
				g2new = liveInput2;
			}

		}
	}

}
