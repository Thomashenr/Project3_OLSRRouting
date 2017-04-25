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
   public int valuex, valuey; //Locations x and y coordinated
   
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
      } 
        
   }
   public String changeLocation() { //Method that randomly changes the x and y coordinates
      Random rand = new Random();
      int resulty = rand.nextInt(3) - 2;
      int resultx = rand.nextInt(3) - 2;
      valuex = valuex + resultx;
      valuey = valuey + resulty; 
      return (String.valueOf(valuex) + " " + String.valueOf(valuey));
   }
   public String generateLocation() {
      Random rand = new Random();
      valuex = rand.nextInt(300) + 2;
      valuey = rand.nextInt(300) + 2;
      return (String.valueOf(valuex) + " " + String.valueOf(valuey));
   }
   
   public void writeToFile(FileWriter file, BufferedWriter buffer) { //Method for original writing to file with data and original location
      try {
         int nodeNum = 1;
         for(int i = 0; i < machineNum; i++) {
            port = 10164;
            for(int x = 1; x <= nodesOnMachine[i]; x++){
               if (changes == 0) {
                  buffer.write("Node" + nodeNum + " " + machines[i] + " " + port + " " + generateLocation() + "\n");
               }
               else {
                  buffer.write("Node" + nodeNum + " " + machines[i] + " " + port + " " + changeLocation() + "\n");
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