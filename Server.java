import java.awt.Dimension;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server extends JFrame {

	public static int user_input_c_node=0;
	public static void main(String[] args) throws InterruptedException {

		Server server = new Server();
		 Scanner s = new Scanner(System.in);
		 System.out.println("Enter Current Node of Server : ");
		 user_input_c_node=s.nextInt();
		System.out.println("Ready to receive");
		server.readyToReceivPacket();
	}

	private final JTextArea msgArea = new JTextArea();
	// Window for Sensor1
	public JFrame s1 = new JFrame("Sensor1: Oxgyen Tank Level");
	public JTextArea m1 = new JTextArea();
	// Window for Sensor2
	public JFrame s2 = new JFrame("Sensor2: Heart Rate");
	public JTextArea m2 = new JTextArea();
	// Window for Sensor3
	public JFrame s3 = new JFrame("Sensor3: Location");
	public JTextArea m3 = new JTextArea();
	// Window for Sensor4
	public JFrame s4 = new JFrame("Sensor4: Toxic Levels");
	public JTextArea m4 = new JTextArea();

	private DatagramSocket socket;
	public String msg = "1";
	public int packetNumber = 0;
	public int checkPacket = -1;


	public Server() {

		// Creating windows for each sensor as well as the main hub

		super("Message Server");
		super.add(new JScrollPane(msgArea));
		super.setSize(new Dimension(450, 350));
		super.setBounds(700, 50, 450, 350);
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setVisible(true);
		msgArea.setEditable(false);

		// s1.add(m1);
		s1.add(new JScrollPane(m1));
		s1.setSize(new Dimension(450, 350));
		s1.setBounds(100, 500, 450, 350);
		s1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s1.setVisible(true);
		m1.setEditable(false);
		s2.add(m2);

		s2.add(new JScrollPane(m2));
		s2.setSize(new Dimension(450, 350));
		s2.setBounds(550, 500, 450, 350);
		s2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s2.setVisible(true);
		m2.setEditable(false);
		s3.add(m3);

		s3.add(new JScrollPane(m3));
		s3.setSize(new Dimension(450, 350));
		s3.setBounds(1000, 500, 450, 350);
		s3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s3.setVisible(true);
		m3.setEditable(false);

		s4.add(new JScrollPane(m4));
		s4.setSize(new Dimension(450, 350));
		s4.setBounds(1000, 500, 450, 350);
		s4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s4.setVisible(true);
		m4.setEditable(false);
		
		
		try {
			//socket # here should be the port# of the last node in configuration file
			socket = new DatagramSocket(10169);
		} catch (SocketException ex) {
			System.out.println("failed to setup socket!");
			System.exit(1);
		}
	}

	public void readyToReceivPacket() throws InterruptedException {
		boolean check = true;
		while (check) {
			try {
				// try to receive packet
				byte buffer[] = new byte[128];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				String r_p_client = new String(packet.getData());
				showMsg("\n\nData:" + r_p_client);
				int i1 = r_p_client.indexOf("P");
				int j1 = 0;
					if (r_p_client.contains("H")) {
						j1 = r_p_client.indexOf("H");
						//String data = r_p_client.substring((j + 1), (j + 4));
						//m2.insert("\n" + data + " BPM", 0);
					} else if (r_p_client.contains("T")) {
						j1 = r_p_client.indexOf("T");
						//String data = r_p_client.substring((j + 1), (j + 3));
						//m1.insert("\n" + data + "%", 0);
					} else if (r_p_client.contains("L")) {
						j1 = r_p_client.indexOf("L");
						//String data = r_p_client.substring((j), (j + 8));
						//m3.insert("\n" + data, 0);
					} else if (r_p_client.contains("X")) {
						j1 = r_p_client.indexOf("X");
						//String data = r_p_client.substring((j + 1), (j + 3));
						//m4.insert("\n" + data + "%", 0);
					}
				String latestPNumber = "";
					if (j1 == 0) {
						int index1 = r_p_client.indexOf("DR");
     						latestPNumber = r_p_client.substring((i1 + 1), index1).trim();
					} else {
						latestPNumber = r_p_client.substring((i1 + 1), j1).trim();
					}
				System.out.println(latestPNumber + " " + checkPacket);
				if ( Integer.parseInt(latestPNumber) > checkPacket)
				{
					if (false) {
						msg = "0";
					} else {
						int i = r_p_client.indexOf("P");
						int j = 0;
						if (r_p_client.contains("H")) {
							j = r_p_client.indexOf("H");
							String data = r_p_client.substring((j + 1), (j + 4));
							m2.insert("\n" + data + " BPM", 0);
						} else if (r_p_client.contains("T")) {
							j = r_p_client.indexOf("T");
							String data = r_p_client.substring((j + 1), (j + 3));
							m1.insert("\n" + data + "%", 0);
						} else if (r_p_client.contains("L")) {
							j = r_p_client.indexOf("L");
							String data = r_p_client.substring((j), (j + 8));
							m3.insert("\n" + data, 0);
						} else if (r_p_client.contains("X")) {
							j = r_p_client.indexOf("X");
							String data = r_p_client.substring((j + 1), (j + 3));
							m4.insert("\n" + data + "%", 0);
						}

						String packetNum = "";
						if (j == 0) {
							int index = r_p_client.indexOf("DR");
								packetNum = r_p_client.substring((i + 1), index).trim();
						} else {
							packetNum = r_p_client.substring((i + 1), j).trim();
						}
						showMsg("Packet Number: " + packetNum);
						checkPacket = Integer.parseInt(packetNum);
						packetNumber = Integer.parseInt(packetNum);

					}
					
				}
				sendPacket(packet);
				
			} catch (IOException ex) {
				showMsg(ex.getMessage());
			}
		}
	}

	public void sendPacket(DatagramPacket packetReceived) {
		try {
			packetNumber++;
			msg = "AP" + packetNumber + "DR1SR7"+"PN7";
			System.out.println("Message: " + msg);
			byte buff[] = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buff, buff.length, packetReceived.getAddress(), packetReceived.getPort());
			socket.send(packet);

		} catch (IOException ex) {
			System.out.println("Error Sending Packet!");
	
	}
	}

	// printing message to hub
	public void showMsg(final String msg) {
		System.out.println(msg);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				msgArea.append(msg);
			}
		});
	}

}
