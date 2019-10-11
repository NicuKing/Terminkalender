package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterUI extends Thread {
    public JFrame frame;
    private JTextField Username, Email;
    private JPasswordField Password, RepPassword;
    ClientConnection Connection;
    public String Uname, Pword, Mail, RPword;
    private RegisterUI window;


    //Das Fenster öffnen
    public void run() {
        try {
            window = new RegisterUI(Connection);
            window.frame.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Constructor
    public RegisterUI(ClientConnection c) {
        this.Connection = c;
        initialize();
    }

    //Hier wird das GUI aufgebaut
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 250, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(20, 10, 80, 20);
        frame.getContentPane().add(lblEmail);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setBounds(20, 55, 80, 20);
        frame.getContentPane().add(lblUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(20, 105, 80, 20);
        frame.getContentPane().add(lblPassword);

        JLabel lblRepPassword = new JLabel("Repeat Password");
        lblRepPassword.setBounds(20, 155, 150, 20);
        frame.getContentPane().add(lblRepPassword);

        Email = new JTextField();
        Email.setBounds(20, 30, 180, 20);
        frame.getContentPane().add(Email);
        Email.setColumns(10);

        Username = new JTextField();
        Username.setBounds(20, 75, 180, 20);
        frame.getContentPane().add(Username);
        Username.setColumns(10);

        Password = new JPasswordField();
        Password.setBounds(20, 125, 180, 20);
        frame.getContentPane().add(Password);

        RepPassword = new JPasswordField();
        RepPassword.setBounds(20, 175, 180, 20);
        frame.getContentPane().add(RepPassword);

        JLabel lblEmailError = new JLabel("");
        lblEmailError.setForeground(Color.RED);
        lblEmailError.setBounds(55, 10, 270, 20);
        frame.getContentPane().add(lblEmailError);

        JLabel lblUsernameError = new JLabel("");
        lblUsernameError.setForeground(Color.RED);
        lblUsernameError.setBounds(80, 55, 270, 20);
        frame.getContentPane().add(lblUsernameError);

        JLabel lblPasswordError = new JLabel("");
        lblPasswordError.setForeground(Color.RED);
        lblPasswordError.setBounds(80, 105, 270, 20);
        frame.getContentPane().add(lblPasswordError);

        JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Wenn der Loginbutton geklickt wird, sendet es die eingegebenen Werte an den Server, wo diese validiert werden
                boolean Validregistry = true;
                Mail = Email.getText();
                Uname = Username.getText();
                Pword = Password.getText();
                RPword = RepPassword.getText();
                //Email regex kopiert von https://stackoverflow.com/questions/8204680/java-regex-email/13013056#13013056
                Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                //Hier werden überprüfungen gemacht um zu schauen ob die Werte gültig sind, damit ein neuer User gemacht werden kann
                if(Pword.length()<6) {
                    Validregistry = false;
                    lblPasswordError.setText("*Password too short");
                }
                else {
                    lblPasswordError.setText("");
                }
                if(!Pword.equals(RPword)) {
                    Validregistry = false;
                    lblPasswordError.setText("*Passwords don't match");
                }
                else if(!lblPasswordError.getText().equals("*Password too short")){
                    lblPasswordError.setText("");
                }
                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(Mail);
                if (!matcher.find()) {
                    Validregistry = false;
                    lblEmailError.setText("*Email not valid");
                } else {
                    lblEmailError.setText("");
                }
                if(Uname.length()<6 || Uname.length()>24) {
                    Validregistry = false;
                    lblUsernameError.setText("*6-24 characters");
                }
                else {
                    lblUsernameError.setText("");
                }
                if(!Mail.equals("") || !Uname.equals("")) {
                    if (!Connection.CheckIfUserExists(Uname, Mail)) {
                        Validregistry = false;
                        lblEmailError.setText("Account with this Username or Email exists already");
                    } else {
                        lblEmailError.setText("");
                    }
                }
                if(Validregistry){
                    boolean Success = Connection.SendRegistryToServer(Mail, Uname, Pword);
                    if(Success) {
                        frame.setVisible(false);
                        Connection.InitLogin();
                    }
                }
            }
        });

        btnRegister.setBounds(115, 210, 85, 20);
        frame.getContentPane().add(btnRegister);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.setVisible(false);
                Connection.InitLogin();
            }
        });
        btnBack.setBounds(20, 210, 85, 20);
        frame.getContentPane().add(btnBack);

    }
}

