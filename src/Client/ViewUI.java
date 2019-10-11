package Client;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ViewUI extends Thread {
    public JFrame frame;
    JTextArea Long;
    JTextField Short;
    ClientConnection Connection;
    private ViewUI window;
    String[] data;


    //Das Fenster öffnen
    public void run() {
        try {
            window = new ViewUI(Connection, data);
            window.frame.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Constructor
    public ViewUI(ClientConnection c, String[] data) {
        this.data = data;
        this.Connection = c;
        initialize();
    }

    //Hier wird das GUI aufgebaut
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 300, 330);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblTimeTitle = new JLabel("Time:");
        lblTimeTitle.setBounds(20,10,100,20);
        frame.getContentPane().add(lblTimeTitle);

        JTextArea taTime = new JTextArea(data[0]);
        taTime.setFont(new Font("Arial", Font.ITALIC, 13));
        taTime.setLineWrap(true);
        taTime.setWrapStyleWord(true);
        taTime.setOpaque(false);
        taTime.setEditable(false);
        taTime.setBounds(20,30,120,20);
        frame.getContentPane().add(taTime);

        JLabel lblShortTextTitle = new JLabel("Short Description:");
        lblShortTextTitle.setBounds(20, 55, 120, 20);
        frame.getContentPane().add(lblShortTextTitle);

        JTextArea taShortText = new JTextArea(data[1]);
        taShortText.setFont(new Font("Arial", Font.ITALIC, 13));
        taShortText.setLineWrap(true);
        taShortText.setWrapStyleWord(true);
        taShortText.setOpaque(false);
        taShortText.setEditable(false);
        taShortText.setBounds(20, 75, 200, 20);
        frame.getContentPane().add(taShortText);

        JLabel lblLongTextTitle = new JLabel("Long Description:");
        lblLongTextTitle.setBounds(20, 100, 250, 20);
        frame.getContentPane().add(lblLongTextTitle);

        JTextArea taLongText = new JTextArea(data[2]);
        taLongText.setFont(new Font("Arial", Font.ITALIC, 13));
        taLongText.setLineWrap(true);
        taLongText.setWrapStyleWord(true);
        taLongText.setOpaque(false);
        taLongText.setEditable(false);
        taLongText.setBounds(20, 120, 250, 100);
        frame.getContentPane().add(taLongText);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.setVisible(false);
                Connection.InitCalendarList();
            }
        });
        btnBack.setBounds(30, 245, 90, 20);
        frame.getContentPane().add(btnBack);

    }

    //Dies wird gebraucht um die Daten zu holen
    public void getData(String[] data) {
        this.data = data;
        initialize();
    }
}
