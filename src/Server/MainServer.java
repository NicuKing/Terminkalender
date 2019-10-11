package Server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainServer extends Thread {

    ServerSocket Server;
    JFrame framePort, frameServer;
    JTextField Port;
    ArrayList<ServerConnection> Connections = new ArrayList<ServerConnection>();
    boolean shouldRun = true;

    public static void main(String[] args) {
        new MainServer();
    }

    public MainServer() {
        initializePortFrame();
    }

    //Hier wird das GUI aufgebaut
    public void initializePortFrame() {
        framePort = new JFrame();
        framePort.setBounds(100, 100, 250, 175);
        framePort.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePort.getContentPane().setLayout(null);

        JLabel lblPort = new JLabel("Port-Number");
        lblPort.setBounds(20, 25, 120, 20);
        framePort.getContentPane().add(lblPort);

        Port = new JTextField();
        Port.setBounds(20, 45, 180, 20);
        framePort.getContentPane().add(Port);
        Port.setColumns(10);

        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Man kann eine PortNr. eingeben und es wird dann geschaut ob diese Valid ist mit Regex
                Pattern VALID_PORT_NUMBER_REGEX = Pattern.compile("^[0-9]{3,4}$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = VALID_PORT_NUMBER_REGEX.matcher(Port.getText());
                if(matcher.find()) {
                    framePort.setVisible(false);
                    startServer(Integer.parseInt(Port.getText()));
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Port-Number", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        btnStart.setBounds(20, 80, 90, 20);
        framePort.getContentPane().add(btnStart);

        framePort.setVisible(true);
    }

    //Der Server wird gestartet
    public void startServer(int PortNr) {
        try {
            //Port öfnnen
            Server = new ServerSocket(PortNr);
            while(shouldRun) {
                //Client aufnehmen
                Socket Sock = Server.accept();
                ServerConnection Connection = new ServerConnection(Sock, this);
                Connection.start();
                //Client in Liste aufnehmen
                Connections.add(Connection);
        }
    } catch (IOException e) {
        e.printStackTrace();
        shouldRun = false;
    }
    }

}