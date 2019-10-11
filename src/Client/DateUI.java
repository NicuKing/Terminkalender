package Client;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import javafx.scene.layout.Border;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DateUI extends Thread {
    public JFrame frame;
    private JTextField Short;
    private JTextArea Long;
    String[] MonthCount = new String[] {"1","2","3","4","5","6","7","8","9","10","11","12"};
    String[] Times = new String[96];
    ClientConnection Connection;
    public String Uname, Pword;
    private DateUI window;


    //Das Fenster öffnen
    public void run() {
        try {
            window = new DateUI(Connection);
            window.frame.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    //Constructor
    public DateUI(ClientConnection c) {
        this.Connection = c;
        int o=0;
        //Hier wird die Uhrzeitliste erstellt
        for(int i=0;i<24;i++) {
            for(int j=0;j<60;j+=15) {
                if(i<10 && j==0) {
                    Times[o] = ("0"+i+":"+j+"0");
                }
                else if(i<10) {
                    Times[o] = ("0"+i+":"+j);
                }
                else if(j==0) {
                    Times[o] = (i + ":" + j + "0");
                }
                else {
                    Times[o] = (i + ":" + j);
                }
                o++;
            }
        }
        initialize();
    }

    //Hier wird das GUI aufgebaut
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 300, 320);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblDate = new JLabel("Date");
        lblDate.setBounds(20, 20, 120, 20);
        frame.getContentPane().add(lblDate);

        JLabel lblTime = new JLabel("Time");
        lblTime.setBounds(150, 20, 120, 20);
        frame.getContentPane().add(lblTime);

        //Dies ist ein plugin um Termine einfacher auszuwählen
        JDateChooser dcDate = new JDateChooser();
        dcDate.setBounds(20, 40, 120, 20);
        frame.getContentPane().add(dcDate);

        JComboBox cbTime = new JComboBox(Times);
        cbTime.setBounds(150, 40, 100, 20);
        frame.getContentPane().add(cbTime);

        JLabel lblShortDescrip = new JLabel("Short Description");
        lblShortDescrip.setBounds(20, 65, 120, 20);
        frame.getContentPane().add(lblShortDescrip);

        JLabel lblLongDescrip = new JLabel("Long Description");
        lblLongDescrip.setBounds(20, 110, 110, 20);
        frame.getContentPane().add(lblLongDescrip);

        Short = new JTextField();
        Short.setBounds(20, 85, 250, 20);
        frame.getContentPane().add(Short);
        Short.setColumns(10);

        Long = new JTextArea();
        Long.setBorder(BorderFactory.createLineBorder(Color.black));
        Long.setLineWrap(true);
        Long.setWrapStyleWord(true);
        Long.setBounds(20, 130, 250, 80);
        frame.getContentPane().add(Long);

        //Der Button um den Termin mit den eingegebenen Daten der Datenbank hinzuzufügen
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                Date d = dcDate.getDate();
                String strDate = format.format(d);
                strDate += " - "+cbTime.getSelectedItem().toString();
                String st = Short.getText();
                String lt = Long.getText();
                boolean Serveranswer = Connection.SendNewDateToServer(strDate, st, lt);
                if(Serveranswer) {
                    frame.setVisible(false);
                    Connection.InitCalendarList();
                }
                else {

                }
            }
        });
        btnAdd.setBounds(20, 230, 90, 20);
        frame.getContentPane().add(btnAdd);

        //Button um zurück zu navigieren ohne einen neuen Termin zu erstellen
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.setVisible(false);
                Connection.InitCalendarList();
            }
        });
        btnBack.setBounds(120, 230, 90, 20);
        frame.getContentPane().add(btnBack);

    }
}