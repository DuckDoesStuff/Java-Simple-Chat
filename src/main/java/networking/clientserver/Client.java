package networking.clientserver;

/* CLIENT */

import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    Socket client = null;
    PrintWriter pw = null;
    BufferedReader br = null;
    BufferedReader stdIn = null;
    Thread messageListener = null;
    String messageFromServer;
    JTextArea chatMsg;

    public Client(JTextArea chatMsg, String username) {
        this.chatMsg = chatMsg;
        connect(1234);
        // Create an output stream of the client socket
        OutputStream clientOut = null;
        try {
            clientOut = client.getOutputStream();

            pw = new PrintWriter(clientOut, true);

            // Create an input stream of the client socket
            InputStream clientIn = client.getInputStream();
            br = new BufferedReader(new
                    InputStreamReader(clientIn));

            // Create BufferedReader for a standard input
            stdIn = new BufferedReader(new
                    InputStreamReader(System.in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        startMessageListener();
        // Send username
        sendMessage(username);
    }

    public void connect(int portnumber) {
        // Create a client socket
        try {
            client = new Socket(InetAddress.getLocalHost(), portnumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client socket is created " + client);
    }

    public void setMessage(String msgServer) {
        messageFromServer = msgServer;
    }

    public String getMessage() {
        return messageFromServer;
    }

    public void sendMessage(String msg) {
        pw.println(msg);
    }

    public void startMessageListener() {
        messageListener = new Thread(() -> {
            while (client.isConnected()) {
                String msgFromServer = null;
                try {
                    msgFromServer = br.readLine();
                } catch (IOException e) {
                    System.out.println("Error reading message from server");
                    throw new RuntimeException(e);
                }
                if (msgFromServer != null) {
                    chatMsg.append(msgFromServer + '\n');
                }
            }
        });
        messageListener.start();
    }

    // Main thread
    public void startClient() {
        try {
            // Send username
            String msg = "";
            System.out.println("Enter your name. Type Bye to exit. ");
            // Read data from standard input device and write it
            // to the output stream of the client socket.
            msg = stdIn.readLine().trim();
            pw.println(msg);

            while (client.isConnected()) {
                msg = stdIn.readLine().trim();
                // Stop the operation
                if (msg.equalsIgnoreCase("Bye")) {
                    pw.println(msg);
                    messageListener.join();
                    client.close();
                    break;
                }
                // Send the entered message to the server
                pw.println(msg);
            }

        } catch (IOException ie) {
            System.out.println("I/O error " + ie);
        } catch (InterruptedException e) {
            System.out.println("Error joining message listener thread");
            throw new RuntimeException(e);
        }
    }
}
