import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class UDPClient {
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField messageField;
    private JButton sendButton;

    public UDPClient() {
        try {
            clientSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName("localhost");
            serverPort = 9876;
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("UDP Client");
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
                        clientSocket.receive(receivePacket);

                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        textArea.append("Server: " + message + "\n");
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

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);

            textArea.append("Client: " + message + "\n");

            messageField.setText("");

           
            if (message.equalsIgnoreCase("kết thúc")) {
                clientSocket.close();
                frame.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UDPClient();
    }
}
