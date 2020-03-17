                        
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class ServerSide
{
    public static void main(String[] args) throws UnknownHostException
    {
        //Scanner to read user input
        Scanner s = new Scanner(System.in);

        //Prompting user for IP Address
        System.out.print("IP ADDRESS: ");
        String ip = s.nextLine();
        InetAddress ipAddr = InetAddress.getByName(ip);

        //Prompting user for Port
        System.out.print("PORT: ");
        int port = s.nextInt();

        try(ServerSocket serverSocket = new ServerSocket(port, 25, ipAddr))
        {
            System.out.println("Server is listening on port " + port);

            while(true)
            {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected...");

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output);

                boolean running = true;

                Process process = null;
                String processValue = null;
                BufferedReader br;

                while(running)
                {

                    String userIn = reader.readLine();

                    switch (userIn) {
                        //Date and Time - the date and time on the server
                        case "1":
                            process = Runtime.getRuntime().exec("date");
                            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            while((processValue = br.readLine()) != null){
                                writer.println(processValue);
                            }
                            break;
                            //Uptime - how long the server has been running since last boot-up
                        case "2":
                            process = Runtime.getRuntime().exec("uptime");
                            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            while((processValue = br.readLine()) != null) {
                                writer.println(processValue);
                            }
                            break;
                            //Memory Use - the current memory usage on the server
                        case "3":
                            process = Runtime.getRuntime().exec("free -h -t");
                            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            while((processValue = br.readLine()) != null) {
                                writer.println(processValue);

                            }
                            break;

                            //Netstat - lists network connections on the server
                        case "4":
                            process = Runtime.getRuntime().exec("netstat -rn");
                            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            while((processValue = br.readLine()) != null) {
                                writer.println(processValue);

                            }
                            break;
                            //Current Users - list of users currently connected to the server
                        case "5":
                            process = Runtime.getRuntime().exec("w");
                            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            while((processValue = br.readLine()) != null) {
                                writer.println(processValue);

                            }
                            break;

                            //Running Processes - list of programs currently running on the server
                        case "6":
                            process = Runtime.getRuntime().exec("ps");
                            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            while((processValue = br.readLine()) != null) {
                                writer.println(processValue);

                            }
                            break;

                        default:
                            break;
                    }
                    writer.close();
                    socket.close();
                    break;
                }
            }
        }
        catch(IOException e)
        {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}