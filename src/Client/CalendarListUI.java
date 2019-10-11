package Client;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class CalendarListUI extends Thread {
    public JFrame frame;
    public JTable table;
    public JScrollPane sp;
    ClientConnection Connection;
    public String Uname, Pword;
    public CalendarListUI window;
    Object[][] data;


    //Das Fenster öffnen
    public void run() {
        try {
            window = new CalendarListUI(Connection);
            window.frame.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Constructor
    public CalendarListUI(ClientConnection c) {
        this.Connection = c;
        initialize();
    }

    //Hier wird das GUI aufgebaut
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 530, 218);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        //Hier ist die Liste der Termine
        data = Connection.getAllCalendarDates();
        String[] columnNames = { "Date", "Short Description", "Done"};
        table = new JTable(data, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setPreferredScrollableViewportSize(new Dimension(470, 100));
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 10, 473, 103);
        frame.getContentPane().add(sp);

        JLabel lblDeleteError = new JLabel("");
        lblDeleteError.setForeground(Color.RED);
        lblDeleteError.setBounds(20, 240, 270, 20);
        frame.getContentPane().add(lblDeleteError);

        //Der Button "Add" um zum neuer termin erstellen gui zu navigieren
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.setVisible(false);
                Connection.InitDate();
            }
        });
        btnAdd.setBounds(30, 120, 90, 20);
        frame.getContentPane().add(btnAdd);

        //Der Button "Edit" um auf das termin bearbeiten gui zu navigieren
        JButton btnEdit = new JButton("Edit");
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(table.getSelectedRow() != -1) {
                    frame.setVisible(false);
                    Connection.GetSelectedDataToEdit((String) table.getValueAt(table.getSelectedRow(), 1));
                }
            }
        });
        btnEdit.setBounds(130, 120, 90, 20);
        frame.getContentPane().add(btnEdit);

        //Der Button Delete um einen Termin direkt zu löschen. Der Termin wird nicht direkt gelöscht
        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(table.getSelectedRow() != -1) {
                    boolean ServerAnswer = Connection.DeleteDateFromDatabase((String) table.getValueAt(table.getSelectedRow(), 1));
                    if(!ServerAnswer) {
                        lblDeleteError.setText("Something went wrong...");
                    } else {
                        lblDeleteError.setText("");
                        frame.setVisible(false);
                        Connection.InitCalendarList();
                    }
                }
            }
        });
        btnDelete.setBounds(230, 120, 90, 20);
        frame.getContentPane().add(btnDelete);

        //Der Button um zur Einzelansicht eines Termin zu navigieren
        JButton btnView = new JButton("View");
        btnView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(table.getSelectedRow() != -1) {
                    frame.setVisible(false);
                    Connection.GetDateFromServer((String) table.getValueAt(table.getSelectedRow(), 1));
                }
            }
        });
        btnView.setBounds(330, 120, 90, 20);
        frame.getContentPane().add(btnView);
    }
}
