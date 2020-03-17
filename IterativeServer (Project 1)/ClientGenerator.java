            
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientGenerator {


    public static void main(String[] args){

        int numOfClients;
        int requestType;
        String networkAddress;
        int port;

        do {
            Scanner input = new Scanner(System.in);

            System.out.println("Enter Network Address or Q to quit: ");
            networkAddress = input.next();

            if(networkAddress.equalsIgnoreCase("Q")){

                break;

}

            System.out.println("Enter Port Number: ");
            port = input.nextInt();

            System.out.println("Enter number of clients requests (1, 5, 10, 15, 20, 25): ");
            numOfClients = input.nextInt();

            System.out.println("Request Type: " +
                    "\n Date and Time (1) " +
                    "\n Uptime (2) " +
                    "\n Memory Use (3) " +
                    "\n Netstat (4) " +
                    "\n Current Users (5) " +
                    "\n Running Processes (6) " +
                    "\n Enter Request: ");
            requestType = input.nextInt();

            //create #of clients and start requests
            for (int i = 0; i < numOfClients; i++) {
                Client client = new Client(requestType, port, networkAddress);
                client.start();

            }

            try {
                while (Client.getActiveThreads() > 0 ) {
                    Thread.sleep(20);
                }
            }catch (Exception e)
            {
                System.out.println("Thread counter failed, thread time may be wrong");
            }

            double sec = ((double) Client.seconds.sumThenReset())/1000000000;
            System.out.println("Total Turn Around Time: " + sec);
            System.out.println("Average Turn Around Time: " + sec/numOfClients);
         
            
	                //delay printing of user input screen.
            try {
                Thread.sleep(200);

            }catch (Exception e)
            {
                System.out.println("Thread counter failed, thread time may be wrong");
            }

        }while(true);

    }
}
class Client extends Thread{

    private int request;
    private int port;
    private String address;

    public static LongAdder seconds = new LongAdder();
    private static int activeThreads = 0;
    private static Lock lock = new ReentrantLock();


    public Client(int request, int port, String address){
        this.request = request;
        this.port = port;
        this.address = address;


    }

    public void run(){

        long startTime = System.nanoTime();
        try {
	     increaseCounter();

            //create connection to Address&Port
            Socket socket = new Socket(address, port);


            //create output
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            //send output
            writer.println(request);


            //get request
            InputStream input = socket.getInputStream();

            //load request into buffer reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            //print output
            String response;
            while ((response = reader.readLine()) != null){
                System.out.println(response);
            }

            writer.flush();
            long stopTime = System.nanoTime();
            long clientTime = stopTime - startTime;
            double sec = ((double) clientTime)/1000000000;
            seconds.add(clientTime);
            System.out.println("Elapsed Time for Client: " + sec + " seconds");
            decreaseCounter();

        } catch (IOException | InterruptedException e) {
            System.out.println("Error"+ e.toString());
        }
    }
    
public int getRequest(){
        return this.request;
    }
    public void setRequest(int request){
        this.request = request;
    }


    private void increaseCounter() throws InterruptedException {
        while (!lock.tryLock()){

            Thread.sleep(10);

        }
        activeThreads++;
        lock.unlock();
    }
    private void decreaseCounter() throws InterruptedException {
        while (!lock.tryLock()){

            Thread.sleep(10);

        }
        activeThreads--;
        lock.unlock();
    }
    public static int getActiveThreads() throws InterruptedException {

        while (!lock.tryLock()){

            Thread.sleep(10);

        }
        int i = activeThreads;
        lock.unlock();
        return i;
    }
}

