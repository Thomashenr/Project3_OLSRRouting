import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.lang.*;

public class Config { //Class that alters config file

   public String machines[]; //Array for machines used
   public int nodesOnMachine[]; //Array to hold node num for each machine respectively
   public int machineNum; // Number of machines being used  
   public int port = 10164; //Port Number to start with
   public int changes = 0;
   public int xvalue[];
   public int yvalue[]; 
   public int valuex = 0;
   public int valuy = 0; //Locations x and y coordinated
   public int totalNodes = 0;
   
   public void startUp() { //Method that prompts user for number of machines, machine names and nodes on machine to write to config file
      System.out.print("How many machines will be used in this program? ");
      Scanner s = new Scanner(System.in);
      machineNum = s.nextInt();
      machines = new String[machineNum];
      nodesOnMachine = new int[machineNum];
      for (int i = 0; i < machineNum; i++) {
         Scanner str = new Scanner(System.in);
         System.out.println("Enter machine name: ");
         machines[i] = str.nextLine();
         System.out.println("How many nodes will this machine hold? No more than 5." );
         Scanner num = new Scanner(System.in);
         int machineNodes = num.nextInt();
         if(machineNodes > 5) {
            System.out.println("Too many Nodes on machine.");
            System.exit(1);
         }
         nodesOnMachine[i] = machineNodes;
         totalNodes = totalNodes + machineNodes;
      } 
      xvalue = new int[totalNodes];
      yvalue = new int[totalNodes];
        
   }
   public String changeLocation(int x) { //Method that randomly changes the x and y coordinates
      Random rand = new Random();
      int resulty = rand.nextInt(3) - 1;
      int resultx = rand.nextInt(3) - 1;
      xvalue[x] = xvalue[x] + resultx;
      yvalue[x] = yvalue[x] + resulty;
      if (xvalue[x] < 0) {
    	  xvalue[x] = 0;
      }
      if (xvalue[x] > 300) {
    	  xvalue[x] = 300;
      }
      if (yvalue[x] < 0) {
    	  yvalue[x] = 0;
      }
      if (yvalue[x] > 300) {
    	  yvalue[x] = 300;
      }
      return (String.valueOf(xvalue[x]) + " " + String.valueOf(yvalue[x]));
   }
   public String generateLocation(int x) {
      Random rand = new Random();
      xvalue[x] = rand.nextInt(300) + 2;
      yvalue[x] = rand.nextInt(300) + 2; 
      return (String.valueOf(xvalue[x]) + " " + String.valueOf(yvalue[x]));
   }
   
   public void writeToFile(FileWriter file, BufferedWriter buffer) { //Method for original writing to file with data and original location
      try {
         int nodeNum = 1;
         for(int i = 0; i < machineNum; i++) {
            port = 10164;
            for(int x = 1; x <= nodesOnMachine[i]; x++){
               if (changes == 0) {
                  buffer.write("Node" + nodeNum + " " + machines[i] + " " + port + " " + generateLocation(x-1) + "\n");
               }
               else {
            	   //if NOT last node
            	   if (x != nodesOnMachine[machineNum - 1]) {
                       buffer.write("Node" + nodeNum + " " + machines[i] + " " + port + " " + changeLocation(x-1) + "\n");
            	   }
            	   //if IS last node
            	   else {
                       buffer.write("Node" + nodeNum + " " + machines[i] + " " + port + " " + changeLocation(x-1));
            	   }
               }
               port++;
               nodeNum++;
            }
         }
      }
      catch(IOException e) {
         e.printStackTrace();
      }
   }
    
   public static void main(String[] args) {
      Config run = new Config();
      run.startUp();
      BufferedWriter buffer = null;
      FileWriter file = null;
      while(true) {
         try { //Initial Writing To File
            file = new FileWriter(args[0]);
            buffer = new BufferedWriter(file);
            run.writeToFile(file, buffer);
            buffer.close();
            file.close();
            run.changes++;  
         } 
         catch(IOException e) {
            e.printStackTrace();
         }
         try {
            TimeUnit.SECONDS.sleep(5); //5 second wait before rewriting to file and changing locations
         } 
         catch(InterruptedException ex) {
            ex.printStackTrace();
         }
      }
      
   }
}