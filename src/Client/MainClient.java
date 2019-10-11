package Client;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClient extends Thread {

    ClientConnection Connection;
    JFrame frame;
    JTextField Port, Ip;

    public static void main(String[] args) throws IOException {
        new MainClient();
    }

    public MainClient() throws IOException {
        initializeConnectionFrame();
    }

    //Hier wird das GUI aufgebaut
    public void initializeConnectionFrame() {
        frame = new JFrame();
        frame.setBounds(100, 100, 250, 190);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblPort = new JLabel("Port-Number");
        lblPort.setBounds(20, 25, 120, 20);
        frame.getContentPane().add(lblPort);

        Port = new JTextField();
        Port.setBounds(20, 45, 180, 20);
        frame.getContentPane().add(Port);
        Port.setColumns(10);

        JLabel lblIp = new JLabel("IP-Address");
        lblIp.setBounds(20, 70, 120, 20);
        frame.getContentPane().add(lblIp);

        Ip = new JTextField();
        Ip.setBounds(20, 90, 180, 20);
        frame.getContentPane().add(Ip);
        Ip.setColumns(10);

        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Hier ist ein Regex um die PortNr. zu validieren
                Pattern VALID_PORT_NUMBER_REGEX = Pattern.compile("^[0-9]{3,4}$", Pattern.CASE_INSENSITIVE);
                //Der Matcher wird gebraucht um den String zu Regexen und dann eine Boolean antwort zu erhalten
                Matcher Portmatcher = VALID_PORT_NUMBER_REGEX.matcher(Port.getText());
                //IP regex von https://www.regextester.com/22 ergänzt with localhost
                Pattern VALID_IP_ADDRESS_REGEX = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]){1,3}|localhost$", Pattern.CASE_INSENSITIVE);
                Matcher Ipmatcher = VALID_IP_ADDRESS_REGEX.matcher(Ip.getText());
                //.find() enthält das Boolean ob das Regex passt oder nicht
                if(Portmatcher.find() && Ipmatcher.find()) {
                    startConnection(Integer.parseInt(Port.getText()), Ip.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid PortNr. or IP-Adress", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        btnStart.setBounds(20, 115, 90, 20);
        frame.getContentPane().add(btnStart);

        frame.setVisible(true);
    }

    //Hier wird die Connection mit dem Server hergestellt
    public void startConnection(int PortNr, String Ip) {
        try {
            //Verbindung zum Server herstellen
            Socket Sock = new Socket(Ip, PortNr);
            frame.setVisible(false);
            ExecutorService service = Executors.newFixedThreadPool(4);
            service.submit(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            Thread.sleep(1000);
                            if(!Sock.isConnected()) {
                                JOptionPane.showMessageDialog(null, "Connection lost", "Error", JOptionPane.INFORMATION_MESSAGE);
                                System.exit(0);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Connection = new ClientConnection(Sock, this);
            Connection.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection couldn't be established", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
