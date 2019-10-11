package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends Thread {
    public JFrame frame;
    private JTextField Username;
    private JPasswordField Password;
    ClientConnection Connection;
    public String Uname, Pword;
    private LoginUI window;


    //Das Fenster öffnen
    public void run() {
        try {
            window = new LoginUI(Connection);
            window.frame.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Constructor
    public LoginUI(ClientConnection c) {
        this.Connection = c;
        initialize();
    }

    //Hier wird das GUI aufgebaut
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 250, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setBounds(20, 10, 120, 20);
        frame.getContentPane().add(lblUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(20, 60, 110, 20);
        frame.getContentPane().add(lblPassword);

        Username = new JTextField();
        Username.setBounds(20, 30, 180, 20);
        frame.getContentPane().add(Username);
        Username.setColumns(10);

        Password = new JPasswordField();
        Password.setBounds(20, 80, 180, 20);
        frame.getContentPane().add(Password);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Wenn der Loginbutton geklickt wird, sendet es die eingegebenen Werte an den Server, wo diese validiert werden
                Uname = Username.getText();
                Pword = Password.getText();
                boolean Serveranswer = Connection.SendLoginToServer(Uname, Pword);
                if(Serveranswer) {
                    frame.setVisible(false);
                    Connection.InitCalendarList();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Logininformation", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        btnLogin.setBounds(20, 115, 90, 20);
        frame.getContentPane().add(btnLogin);

        JButton btnNavRegister = new JButton("Register");
        btnNavRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                    frame.setVisible(false);
                    Connection.InitRegister();
            }
        });
        btnNavRegister.setBounds(120, 115, 90, 20);
        frame.getContentPane().add(btnNavRegister);

    }
}

