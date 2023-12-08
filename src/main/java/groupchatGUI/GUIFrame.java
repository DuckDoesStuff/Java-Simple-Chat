package groupchatGUI;

import networking.clientserver.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIFrame implements ActionListener {
    JFrame mainFrame;

    JTextField msgInput;

    JButton sendButton;

    JTextArea chatMsg;
    Client client;

    public GUIFrame(){
        mainFrame = new JFrame("TCP/IP chat app");
        mainFrame.setLayout(new FlowLayout());
        mainFrame.setSize(600, 500);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        chatMsg = new JTextArea();
        chatMsg.setPreferredSize(new Dimension(550, 300));
        JScrollPane scrollPane = new JScrollPane(chatMsg);
        msgInput = new JTextField();
        msgInput.setPreferredSize(new Dimension(550, 30));
        sendButton = new JButton("Send a message");
        sendButton.addActionListener(this);
        msgInput.addActionListener(this);

        String username = JOptionPane.showInputDialog("Who are you?");
        client = new Client(chatMsg, username);

        chatMsg.setEditable(false);
//        mainFrame.add(chatMsg);
        mainFrame.add(scrollPane);
        mainFrame.add(msgInput);
        mainFrame.add(sendButton);
        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == sendButton || e.getSource() == msgInput) {
            sendMessage();
        }
    }

    private void sendMessage() {
        String message = msgInput.getText();
        if (!message.isEmpty()) {
            chatMsg.append("\nYou: " + message);
            msgInput.setText(""); // Clear input field after sending
            client.sendMessage(message);

            if (message.equalsIgnoreCase("bye")) {
                System.exit(0);
            }
        }
    }

    public static void main(String arg[]){
        GUIFrame test = new GUIFrame();
    }
}
