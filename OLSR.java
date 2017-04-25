import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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
   public int distances[];
   private Node nodesConnected[];


   public Node(String nodeIn) { //Constructor for nodes
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

   public long checkFileModification() { //Checks to see if file has been modified before sending
      return file.lastModified();
   }

   public void linkNodes(String configFile) {  //Links nodes appropriately
      allNodes = new Node[20];
      String link;
      Node current;
      file = new File(configFile); // Instance of file
      String line;
      BufferedReader buffer = null;
      try {
         buffer = new BufferedReader(new FileReader(file));
         int added = 0;
         int i = 0; 
         while ((line = buffer.readLine()) != null) { // Iterates through file line by line
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
            System.out.println(line);
            index = line.indexOf(" ");
            currentNode.x = Integer.parseInt(line.substring(0, index));
            line = line.replace(line.substring(0, index) + " ", "");  
            currentNode.y = Integer.parseInt(line.substring(0, index));;
            allNodes[i] = currentNode; 
            i++;
         }
      }
      catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      for (int b = 0; b < 20; b++) { //Calculates the distance between Nodes
         if(allNodes[b] != null) {
            for(int q = 0; q < 4; q++) {
               allNodes[b].distances[q] = ((int)Math.sqrt((Math.pow(Math.abs(allNodes[b].x - allNodes[q].x), 2) + Math.pow(Math.abs(allNodes[b].y - allNodes[q].y), 2))));   
               if(allNodes[b].distances[q] <= 100) {
                  allNodes[b].nodesConnected[q] = allNodes[q];
               }
               else {
                  allNodes[b].nodesConnected[q] = null;
               }
            }
         }
      }
      
      for(int y = 0; y < 20; y++) {
         for(int z = 0; z < 20; z++) {
            if (allNodes[y] != null) {
               if(allNodes[y].nodesConnected[z] != null) {
                  System.out.println(allNodes[y].getNodeName() + " Connected to: " +  allNodes[y].nodesConnected[z].getNodeName() + " Distance: " + allNodes[y].distances[z]);
               }
            }
         }
      }
   
   }
   
   public boolean gremlinFunctionManet(int distance) { //PacketRateDrop function
      Random randSend = new Random();
      int gremlin = randSend.nextInt(100) + 1;
      double results = 100 - (distance / 5);
      results = (int) results;
      System.out.println(results);
      if (gremlin < (int) results) {
         return true;
      }
      //if gremlin doesnt drop, then send it to the server
      else {
         return false;
      }
      
   }
   public boolean inRangeSimulate(int distance) {
      if (distance > 100) {
         return true;
      }
      if (distance == 0) {
         return true;
      }
      else {
         return false;
      }
   }
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
   public static String[][] cache_table=new String[10][10];
   public static int z1=0;
   public static int new_z1=0;
   public static int noden = 0;


   public static void main(String[] args) throws InterruptedException, IOException {
      if (args.length == 0) {
         System.out.println("Missing argument: Configuration File needed.");
      }
      fileName = args[0];
      node.linkNodes(fileName);
      originalTimeStamp = node.checkFileModification();
   	//UDP_MANET client=new UDP_MANET();
      Scanner s = new Scanner(System.in);
      System.out.println("Enter Node : ");
      noden=s.nextInt();
   	
      cache_table[0][0]="source_addr";
      cache_table[0][1]="-1";
   	
      //dynamic node checking
      String d_nodename="Node"+noden;	
      int d_nodeindex=-1;
      for(int s1=0;s1<node.allNodes.length;s1++)
      {
         if(node.allNodes[s1]!=null)
         {
            if(node.allNodes[s1].getNodeName().equals(d_nodename))
            {
               d_nodeindex=s1;
               break;
            }
         }
      }
      if(d_nodeindex==-1)
      {
         System.exit(0);
      }
      //dynamic node checking
      System.out.println("Current Port : "+node.allNodes[d_nodeindex].portNumber);
      int port = Integer.parseInt(node.allNodes[d_nodeindex].portNumber);
      
   
      socket = new DatagramSocket(port);
      
      //start hello thread for updating table
      
      Thread hello = new Thread(new helloSend());
      hello.start();
      //end hello thread for updating table
   
      while(true){
      
         try{   
         	
            if(node.checkFileModification() > originalTimeStamp) {
               System.out.println("Config file has been modified. Must relink Nodes.");
               originalTimeStamp = node.checkFileModification();
               node.linkNodes(fileName);
            }
            d_nodeindex=-1;
            for(int s1=0;s1<node.allNodes.length;s1++)
            {
               if(node.allNodes[s1]!=null)
               {
                  if(node.allNodes[s1].getNodeName().equals(d_nodename))
                  {
                     d_nodeindex=s1;
                     break;
                  }
               }
            }
            if(d_nodeindex==-1)
            {
               System.exit(0);
            }
         
            byte buff1[] = new byte[128];
            DatagramPacket packet = new DatagramPacket(buff1,buff1.length);
         
            socket.receive(packet);
            String r_p_server = new String(packet.getData());
            System.out.println("Data: " + r_p_server);
            String source_addr=r_p_server.substring(r_p_server.indexOf("SR")+2,r_p_server.indexOf("PN")).trim();
            System.out.println("Source: " + source_addr);
            String dest_addr=r_p_server.substring(r_p_server.indexOf("DR")+2,r_p_server.indexOf("SR")).trim();
         	
            int i1=r_p_server.indexOf("P");
            int j1 = 0;
            if(r_p_server.contains("H")) 
            {
               j1=r_p_server.indexOf("H");
            }
            else if (r_p_server.contains("T")) 
            {
               j1=r_p_server.indexOf("T");
            }
            else if (r_p_server.contains("L")) 
            {					
               j1=r_p_server.indexOf("L");
            }
            else if (r_p_server.contains("X")) 
            {					
               j1=r_p_server.indexOf("X");
            }
         	
            String packetNum="";
         	
            if(j1==0)
            {
               int index = r_p_server.indexOf("DR");
               packetNum = r_p_server.substring(i1 + 1, index);
               System.out.println("Packet Number: " + packetNum );
            }
            else
            {
               packetNum = r_p_server.substring((i1 + 1),j1);
               System.out.println("Packet Number: " + packetNum );
            }
         	
         
            String previous_node="";
         
            int test = Integer.parseInt(packetNum);
            int test2 = Integer.parseInt(cache_table[0][1]);
            if(r_p_server.contains("Z"))
            {
               int z2 = r_p_server.indexOf("Z");
               new_z1=Integer.parseInt(r_p_server.substring(0,z2));
            }
            else
            {
               z1=0;
               new_z1=0;
            }
           
            if(Integer.parseInt(packetNum) > Integer.parseInt(cache_table[0][1]) || (new_z1 > z1 && r_p_server.contains("Z")))
            {
               z1=new_z1;
               if(!r_p_server.contains("A"))
               {
                  cache_table[0][1]=packetNum;
               }
            
            
               
               System.out.println("-----");
               previous_node = r_p_server.substring(r_p_server.indexOf("PN")+2).trim();
               System.out.println("Previous Node: " + previous_node);
               r_p_server = r_p_server.substring(0, r_p_server.indexOf("PN"));
               r_p_server = r_p_server.trim() +"PN"+(noden);
            
               for(int i=0;i<20;i++)
               {
                  if(node.allNodes[d_nodeindex].getAttachedNode(i)==0)
                  {
                     break;
                  }
               
                  int d_attachnodeindex=-1;
                  for(int s1=0;s1<node.allNodes.length;s1++)
                  {
                     if(node.allNodes[s1]!=null)
                     {
                     
                        if(node.allNodes[s1].getNodeName().equals("Node"+node.allNodes[d_nodeindex].getAttachedNode(i)))
                        {
                           System.out.println(node.allNodes[s1].getNodeName());
                           d_attachnodeindex=s1;
                           break;
                        }
                     
                     }
                  
                  
                  }
               
                  System.out.println(d_attachnodeindex);
                  if(d_attachnodeindex!=-1)
                  {
                     int an= node.allNodes[d_nodeindex].getAttachedNode(i);
                  
                  
                  
                     if (node.allNodes[d_nodeindex].distances[d_attachnodeindex]<100)
                     {
                     
                        if(Integer.parseInt(previous_node)!=an && Integer.parseInt(source_addr)!=noden && Integer.parseInt(source_addr)!=an) 
                        {
                           int an_port = Integer.parseInt(node.allNodes[d_attachnodeindex].portNumber);
                        //Thread.sleep(1000); //pause for readability
                           System.out.println("r_p_server : "+r_p_server);
                           System.out.println("Forwarding from port :"+port+"to :"+an_port);
                           byte buff[]=r_p_server.getBytes();
                        //System.out.println("buffer length"+buff.length);
                        //  InetAddress addressT = InetAddress.getLocalHost();
                           InetAddress addressT = InetAddress.getByName(node.allNodes[d_attachnodeindex].machine + ".eng.auburn.edu");
                           System.out.println(node.allNodes[d_attachnodeindex].machine);
                           DatagramPacket packetSend = new DatagramPacket(buff, buff.length, addressT, an_port);
                        
                           boolean result = node.gremlinFunctionManet(node.allNodes[d_nodeindex].distances[d_attachnodeindex]);
                           if (!result) {
                              System.out.println("Target out of range.");
                           }
                           else {
                              System.out.println("Target in range, sending..");
                              socket.send(packetSend);
                           
                           }
                        }
                     }
                  }
               }
            }
         }
         catch(IOException ex){
            System.out.println(ex.getMessage());
         
         }
      }
   }

   public static class helloSend implements Runnable {
      public void run() {
         System.out.println("Started Hello Thread!");
      	
         while (true) {
            try {
               Thread.sleep(1000); // pause
            }
            catch(InterruptedException ex) {
               System.out.println(ex.getMessage());
            }
            System.out.println("\nSending Hello!");
         	
            boolean result = true;
            for (int i = 0; i < node.allNodes.length; i++) {
               log("Distance: " + node.allNodes[noden].distances[i]);
               boolean canSend = node.inRangeSimulate(node.allNodes[noden].distances[i]);
            
               if (canSend) {
                  System.out.println("Target out of range.");
               }
               else {
                  System.out.println("Target in range, sending..");
               }
            }
         }
      }
      public void log(String stringIn) {
         System.out.println(stringIn);
      }
   }
}
