package networking.clientserver;

/* SERVER – may enhance to work for multiple clients */
import java.net.*;
import java.io.*;
import java.util.ArrayList;

class ClientHandler implements Runnable{
    static ArrayList<ClientHandler> allClients = new ArrayList<>();
    Socket client;
    String username;

    InputStream clientIn;
    OutputStream clientOut;

    BufferedReader br;
    PrintWriter pw;


    ClientHandler(Socket client) {
        this.client = client;
        try {
            clientIn = client.getInputStream();
            clientOut = client.getOutputStream();
            br = new BufferedReader(new
                    InputStreamReader(clientIn));
            pw = new PrintWriter(clientOut, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        allClients.add(this);
    }

    @Override
    public void run() {
        try {
            System.out.println("Connect request is accepted...");
            String clientHost = client.getInetAddress().getHostAddress();
            int clientPort = client.getPort();
            System.out.println("Client host = " + clientHost + " Client port = " + clientPort);

            // Read data from the client
            String msgFromClient = br.readLine();
            username = msgFromClient;

            // Send response to the client
            if (msgFromClient != null && !msgFromClient.equalsIgnoreCase("bye")) {
                String ansMsg = "Hello, " + msgFromClient;
                pw.println(ansMsg);
            }

            while(client.isConnected()) {
                msgFromClient = br.readLine();
                broadcastMessage(msgFromClient);

                // Close connection
                if(msgFromClient.equalsIgnoreCase("bye")) {
                    System.out.println("Client " + username + " has sent bye message");
                    pw.println("bye");
                    broadcastMessage(username + " has left the chat");
                    client.close();
                    allClients.remove(this);
                    break;
                }
            }
        } catch (IOException ie) {
            System.out.println("I/O error - Start server and turn off Firewall" + ie);
        }
    }

    public void broadcastMessage(String msg){
        msg = username + ": " + msg;
        for (ClientHandler client : allClients){
            if(client == this) continue;
            client.pw.println(msg);
        }
    }
}

public class Server {
   
    public static void main(String [] args) {
        ServerSocket server = null;
        Socket client;
       
        // Default port number we are going to use
        int portnumber = 1234;
        if (args.length >= 1){
            portnumber = Integer.parseInt(args[0]);
        }
       
        // Create Server side socket
        try {
            server = new ServerSocket(portnumber);
        } catch (IOException ie) {
            System.out.println("Cannot open socket." + ie);
            System.exit(1);
        }
        System.out.println("ServerSocket is created " + server);
       
        // Wait for the data from the client and reply
        while(true) {
            try {
                // Listens for a connection to be made to
                // this socket and accepts it. The method blocks until
                // a connection is made
                System.out.println("Waiting for connect request...");
                client = server.accept();
                Thread clientThread = new Thread(new ClientHandler(client));
                clientThread.start();
            } catch (IOException ie) {
                System.out.println("I/O error " + ie);
            }
        }
    }
}