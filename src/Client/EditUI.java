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

public class EditUI extends Thread {
    public JFrame frame;
    JTextArea Long;
    JTextField Short;
    ClientConnection Connection;
    private EditUI window;
    String[] Times = new String[96];
    String[] data;


    //Das Fenster öffnen
    public void run() {
        try {
            window = new EditUI(Connection, data);
            window.frame.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Constructor
    public EditUI(ClientConnection c, String[] data) {
        this.data = data;
        this.Connection = c;
        //Hier wird die Liste der Uhrzeiten erstellt
        int o=0;
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
        frame.setBounds(100, 100, 300, 330);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblDate = new JLabel("Date");
        lblDate.setBounds(20, 20, 120, 20);
        frame.getContentPane().add(lblDate);

        JLabel lblTime = new JLabel("Time");
        lblTime.setBounds(150, 20, 120, 20);
        frame.getContentPane().add(lblTime);

        //Hier wird der DateChooser mit den Daten die von der Datenbank gekommen sind aufgefüllt
        JDateChooser dcDate = new JDateChooser();
        try {
            Date date = new SimpleDateFormat("dd.MM.yyyy").parse(data[0]);
            dcDate.setDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dcDate.setBounds(20, 40, 120, 20);
        frame.getContentPane().add(dcDate);

        JComboBox cbTime = new JComboBox(Times);
        String[] hour = data[0].split(" - ");
        cbTime.setSelectedItem(hour[1]);
        cbTime.setBounds(150, 40, 100, 20);
        frame.getContentPane().add(cbTime);

        JLabel lblShortDescrip = new JLabel("Short Description");
        lblShortDescrip.setBounds(20, 65, 120, 20);
        frame.getContentPane().add(lblShortDescrip);

        JLabel lblLongDescrip = new JLabel("Long Description");
        lblLongDescrip.setBounds(20, 110, 110, 20);
        frame.getContentPane().add(lblLongDescrip);

        //Hier wird das TextField mit dem ShortDescription aus der Datenbank ausgefüllt
        Short = new JTextField(data[1]);
        Short.setBounds(20, 85, 250, 20);
        frame.getContentPane().add(Short);
        Short.setColumns(10);

        //Hier wird das TextArea mit der LongDescription aus der Datenbank ausfgefüllt
        Long = new JTextArea(data[2]);
        Long.setBorder(BorderFactory.createLineBorder(Color.black));
        Long.setLineWrap(true);
        Long.setWrapStyleWord(true);
        Long.setBounds(20, 130, 250, 80);
        frame.getContentPane().add(Long);

        JLabel lblDone = new JLabel("Done?");
        lblDone.setBounds(40, 217, 100, 20);
        frame.getContentPane().add(lblDone);

        //Hier wird gechecked ob in der Datenbank das Done 1 oder 0 ist und je nachdem ist die CheckBox gecheckt oder nicht
        JCheckBox cbDone = new JCheckBox();
        if(data[3]== "0") {
            cbDone.setSelected(true);
        } else {
            cbDone.setSelected(false);
        }
        cbDone.setBounds(75, 217, 20, 20);
        frame.getContentPane().add(cbDone);

        //Hier ist der Button um die Werte zu verändern zu den eingegebenen Werten
        JButton btnChange = new JButton("Change");
        btnChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                Date d = dcDate.getDate();
                String strDate = format.format(d);
                strDate += " - "+cbTime.getSelectedItem().toString();
                String st = Short.getText();
                String lt = Long.getText();
                String IsDone = "";
                if(cbDone.isSelected()) {
                    IsDone = "1";
                } else {
                    IsDone = "0";
                }
                boolean Serveranswer = Connection.SendChangedDateToServer(strDate, st, lt, IsDone, data[1]);
                if(Serveranswer) {
                    frame.setVisible(false);
                    Connection.InitCalendarList();
                }
            }
        });
        btnChange.setBounds(20, 245, 90, 20);
        frame.getContentPane().add(btnChange);

        //Hier ist der Button um zurück zu navigieren ohne die Werte zu verändern
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.setVisible(false);
                Connection.InitCalendarList();
            }
        });
        btnBack.setBounds(120, 245, 90, 20);
        frame.getContentPane().add(btnBack);

    }

    //Diese Methode wird gebraucht um die Daten der Datenbank zu erhalten
    public void getData(String[] data) {
        this.data = data;
        initialize();
    }
}
