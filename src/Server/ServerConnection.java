package Server;

import com.sun.tools.javac.Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

public class ServerConnection extends Thread {

    Socket Sock;
    MainServer Server;
    DataInputStream Input;
    DataOutputStream Output;
    boolean Shouldrun = true;
    MainDatabase db = new MainDatabase();

    public ServerConnection(Socket s, MainServer se) {
        super("ServerConnectionThread");
        this.Sock = s;
        this.Server = se;
        //Input um Nachrichten vom Server zu lesen und Output um Nachrichten an den Server zu schicken
        try {
            Input = new DataInputStream(Sock.getInputStream());
            Output = new DataOutputStream(Sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    //Override von Thread, wird nach Constructor ausgeführt
    @Override
    public void run() {
        try {
            while(Shouldrun) {
                //überprüfen ob eine Nachricht reingekommen ist
                while(Input.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                        Shouldrun = false;
                        close();
                    }
                }
                //Nachricht lesen
                String Message = Input.readUTF();
                String[] Command;
                //Nachricht aufteilen in Command und Werte
                Command = Message.split("%");
                //nachdem die Message in einem Array ist, ist die stelle [0] das Command, was gemacht werden soll.
                if (Command[0].equals("Attempt_Login")) {
                    SendLoginAnswerToClient(Command[1], Command[2]);
                }
                else if(Command[0].equals("Calendar_List")) {
                    SendAllDatesToClient();
                }
                else if(Command[0].equals("Create_New_User")) {
                    AddNewUserToDatabase(Command[1], Command[2], Command[3]);
                }
                else if(Command[0].equals("Check_Usernames")) {
                    CheckDatabaseForUsername(Command[1], Command[2]);
                }
                else if(Command[0].equals("Delete_Date")) {
                    DeleteDateFromDatabase(Command[1]);
                }
                else if(Command[0].equals("Add_Date")) {
                    AddNewDateToDatabase(Command[1], Command[2], Command[3]);
                }
                else if(Command[0].equals("Get_Date_Data")) {
                    GetDataFromDate(Command[1]);
                }
                else if(Command[0].equals("Update_Date")) {
                    UpdateDataOnServer(Command[1], Command[2], Command[3], Command[4], Command[5]);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
            close();
        }
    }

    //Hier werden die Daten an die Datenbank gegeben, welche verändert werden sollen
    private void UpdateDataOnServer(String date, String shortDescrip, String longDescrip, String isDone, String oldDescrip) {
        boolean isAdded = db.UpdateDate(date, shortDescrip, longDescrip, isDone, oldDescrip);
        try {
            if(isAdded) {
                Output.writeUTF("Date_Updated");
                Output.flush();
            } else {
                Output.writeUTF("Date_Not_Updated");
                Output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Hier werden die Daten eines Termin geholt
    private void GetDataFromDate(String valueAt) {
        String date = db.GetDate(valueAt);
        try {
            Output.writeUTF(date);
            Output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Hier wird der neue Termin der Datenbank übergeben
    private void AddNewDateToDatabase(String date, String shortText, String longText) {
        boolean isAdded = db.AddDate(date, shortText, longText);
        try {
            if(isAdded) {
                Output.writeUTF("Date_Added");
                Output.flush();
            } else {
                Output.writeUTF("Date_Not_Added");
                Output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Hier wird der Datenbank der Wert übergeben wo es etwas läschen soll
    private void DeleteDateFromDatabase(String valueAt) {
        boolean isDeleted = db.DeleteDate(valueAt);
        try {
            if(isDeleted) {
                Output.writeUTF("Date_Deleted");
                Output.flush();
            } else {
                Output.writeUTF("Not_Deleted");
                Output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //fragt die Datenbank ab ob der Username schon existiert
    private void CheckDatabaseForUsername(String Username, String Email) {
        boolean Exists = db.UsernameExists(Username, Email);
            try {
                if(Exists) {
                    Output.writeUTF("Username_Taken");
                    Output.flush();
                } else {
                    Output.writeUTF("Username_Free");
                    Output.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    //Methode um das Login zu validieren. Muss noch mit Datenbank gemacht werden
    public void SendLoginAnswerToClient(String Username, String Password) {
        boolean Valid = false;
        String Salt = db.getSaltFromDatabase(Username);
        String Hashed_Password = toHexString(getSHA(Password+Salt));
        Valid = db.getLogin(Username, Hashed_Password);
        if(Valid) {
            try {
                Output.writeUTF("Valid_Login");
                Output.flush();
            } catch (IOException e) {
                System.out.println(e);
                close();
            }
        } else {
            try {
                Output.writeUTF("Invalid_Login");
                Output.flush();
            } catch (IOException e) {
                System.out.println(e);
                close();
            }
        }
    }

    //fügt den neuen User der Datenbank hinzu
    public void AddNewUserToDatabase(String Email, String Username, String Password) {
        //Hier wird ein Salt generiert und das Password danach gehashed
        Random r = new Random();
        String Salt = "";
        String characters = "!#$%&*0123456789?@ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for(int i=0;i<256;i++) {
            Salt += characters.charAt(r.nextInt(characters.length()));
        }
        String Hashed_Password = toHexString(getSHA(Password+Salt));
        boolean Success = db.AddUser(Email, Username, Hashed_Password, Salt);
        try {
            if(Success) {
                Output.writeUTF("User_Added");
                Output.flush();
            } else {
                Output.writeUTF("User_Not_Added");
                Output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //Methode die Alle Termine dem Client sendet
    public void SendAllDatesToClient() {
        //Dies solle durch Datenbank passieren
        String dates = db.GetAllDates();
        try {
            Output.writeUTF(dates);
            Output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    //Für ein Sha-256 Hash zu generieren
    public static byte[] getSHA(String input)
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    //Verbindungen sauber schliessen
    public void close() {
        try {
            Input.close();
            Output.close();
            Sock.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
