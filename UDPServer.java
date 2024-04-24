import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class UDPServer {
    private DatagramSocket serverSocket;
    private InetAddress clientAddress;
    private int clientPort;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField messageField;
    private JButton sendButton;

    public UDPServer() {
        try {
            serverSocket = new DatagramSocket(9876);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        frame = new JFrame("UDP Server");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        receiveMessages();
    }

    private void receiveMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);

                        clientAddress = receivePacket.getAddress();
                        clientPort = receivePacket.getPort();

                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        textArea.append("Client: " + message + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
   

    private void sendMessage() {
        try {
            String message = messageField.getText();
            byte[] sendData = message.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            serverSocket.send(sendPacket);

            textArea.append("Server: " + message + "\n");

            messageField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UDPServer();
    }
}
