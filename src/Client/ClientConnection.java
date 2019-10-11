package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

public class ClientConnection extends Thread {

    Socket Sock;
    DataInputStream Input;
    DataOutputStream Output;
    LoginUI login;
    CalendarListUI Calendarlist;
    RegisterUI Register;
    EditUI Editor;
    DateUI AddDate;
    ViewUI View;
    boolean Shouldrun = true;

    //Hier wird der Socket der Klasse gegeben und der Input und Output definiert um mit dem Server zu kommunizieren
    public ClientConnection(Socket s, MainClient mc) {
        this.Sock = s;
        try {
            Input = new DataInputStream(Sock.getInputStream());
            Output = new DataOutputStream(Sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    //wird nach Constructor ausgeführt
    public void run() {
        try {
            //Input um Nachricht vom Server zu lesen, Output um Nachricht an Server zu senden
            Input = new DataInputStream(Sock.getInputStream());
            Output = new DataOutputStream(Sock.getOutputStream());
            InitLogin();

        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    //Login an den Server senden
    public boolean SendLoginToServer(String Username, String Password) {
        boolean Serveranswer = false;
        try {
            //Nachricht an den Server
            Output.writeUTF("Attempt_Login%"+Username+"%"+Password);
            Output.flush();

            //Überprüfen ob eine Nachricht reingekommen ist
            while (Input.available() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    close();

                }
            }
            //Nachricht lesen
            String reply = Input.readUTF();
            //Antwort auswerten
            if(reply.equals("Valid_Login")) {
                Serveranswer = true;
            }
            else if(reply.equals("Invalid_Login")) {
                Serveranswer = false;
            }
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
        return Serveranswer;
    }

    //Schaut ob der Username schon vergeben ist für die Registration
    public boolean CheckIfUserExists(String Username, String email) {
        boolean Serveranswer = false;
        try {
            Output.writeUTF("Check_Usernames%"+Username+"%"+email);
            Output.flush();
            //Überprüfen ob eine Nachricht reingekommen ist.
            while (Input.available() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    close();

                }
            }
            String reply = Input.readUTF();
            if(reply.equals("Username_Free")) {
                Serveranswer = true;
            }
            else if(reply.equals("Username_Taken")) {
                Serveranswer = false;
            }
            return Serveranswer;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Die Registrationsdaten dem Server senden
    public boolean SendRegistryToServer(String Email, String Username, String Password) {
        boolean Serveranswer = false;
        try {
            Output.writeUTF("Create_New_User%"+Email+"%"+Username+"%"+Password);
            Output.flush();
            //Überprüfen ob eine Nachricht reingekommen ist
            while (Input.available() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    close();

                }
            }
            String reply = Input.readUTF();
            if(reply.equals("User_Added")) {
                Serveranswer = true;
            }
            else if(reply.equals("User_Not_Added")) {
                Serveranswer = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Serveranswer;

    }

    //hohlt die Liste aller Termine
    public Object[][] getAllCalendarDates() {
        try {
            Output.writeUTF("Calendar_List");
            Output.flush();

            //Überprüfen ob eine Nachricht reingekommen ist
            while (Input.available() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println(e);
                    close();

                }
            }
            //Nachricht lesen
            String reply = Input.readUTF();
            //System.out.println(reply);       ------Uncomment for Testing-------------
            String[] temporary = reply.split("%");
            Object[][] dates = new String[temporary.length][3];
            for(int i = 0; i<temporary.length;i++) {
                String[] temporary2 = temporary[i].split("#");
                for(int j = 1;j<temporary2.length;j++) {
                        if(temporary2[j].equals("0")){
                            dates[i][j-1] = "Nein";
                        } else if (temporary2[j].equals("1")) {
                            dates[i][j-1] = "Ja";
                        }
                        else {
                            dates[i][j - 1] = temporary2[j];
                        }
                }
            }
            return dates;
        } catch (IOException e) {
            e.printStackTrace();
            close();
            return null;
        }
    }

    //Kommuniziert dem Server, dass ein Termin gelöscht werden soll
    public boolean DeleteDateFromDatabase(String valueAt) {
        boolean Serveranswer = false;
        try {
            Output.writeUTF("Delete_Date%"+valueAt);
            Output.flush();
            //Überprüfen ob eine Nachricht reingekommen ist
            while (Input.available() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    close();

                }
            }
            String reply = Input.readUTF();
            if(reply.equals("Date_Deleted")) {
                Serveranswer = true;
            }
            else if(reply.equals("Not_Deleted")) {
                Serveranswer = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Serveranswer;
    }

    //hohlt Daten eines Gewissen Termin vom Server
    public void GetSelectedDataToEdit(String valueAt) {
        try {
            Output.writeUTF("Get_Date_Data%"+valueAt);
            Output.flush();
            //Überprüfen ob eine Nachricht reingekommen ist
            while (Input.available() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    close();

                }
            }
            String reply = Input.readUTF();
            reply.substring(0, reply.length() - 1);
            String[] data = reply.split("#");
            InitEdit(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Kommuniziert, dass der Server mit diesen Werten einen neuen Termin erstellen soll
    public boolean SendNewDateToServer(String date, String shortText, String longText) {
        boolean Serveranswer = false;
        try {
            Output.writeUTF("Add_Date%"+date+"%"+shortText+"%"+longText);
            Output.flush();
            //Überprüfen ob eine Nachricht reingekommen ist
            while (Input.available() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    close();

                }
            }
            String reply = Input.readUTF();
            if(reply.equals("Date_Added")) {
                Serveranswer = true;
            }
            else if(reply.equals("Date_Not_Added")) {
                Serveranswer = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Serveranswer;
    }

    //Kommuniziert dem Server dass der Termin mit der Beschreibung die neu eingetragenen Werte haben soll
    public boolean SendChangedDateToServer(String strDate, String st, String lt, String isDone, String oldDescrip) {
        boolean Serveranswer = false;
        try {
            Output.writeUTF("Update_Date%"+strDate+"%"+st+"%"+lt+"%"+isDone+"%"+oldDescrip);
            Output.flush();
            //Überprüfen ob eine Nachricht reingekommen ist
            while (Input.available() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    close();

                }
            }
            String reply = Input.readUTF();
            if(reply.equals("Date_Updated")) {
                Serveranswer = true;
            }
            else if(reply.equals("Date_Not_Updated")) {
                Serveranswer = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Serveranswer;
    }

    //holt ein gewissen Termin vom Server
    public void GetDateFromServer(String valueAt) {
        try {
            Output.writeUTF("Get_Date_Data%"+valueAt);
            Output.flush();
            //Überprüfen ob eine Nachricht reingekommen ist
            while (Input.available() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    close();

                }
            }
            String reply = Input.readUTF();
            reply.substring(0, reply.length() - 1);
            String[] data = reply.split("#");
            InitView(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Öffnet die Terminliste
    public void InitCalendarList() {
        try {
            Calendarlist = new CalendarListUI(this);
            Calendarlist.start();
        } catch (IllegalThreadStateException e) {
            Calendarlist.frame.setVisible(true);
        }
    }

    //Öffnet die Einzelansicht
    public void InitView(String[] data) {
        try {
            View = new ViewUI(this, data);
            View.start();
        } catch (IllegalThreadStateException e) {
            View.getData(data);
            View.frame.setVisible(true);
        }
    }

    //Öffnet das Loginformular
    public void InitLogin(){
        try {
            login = new LoginUI(this);
            login.start();
        } catch (IllegalThreadStateException e) {
            login.frame.setVisible(true);
        }
    }

    //Öffnet das Registerformular
    public void InitRegister() {
        try {
            Register = new RegisterUI(this);
            Register.start();
        } catch (IllegalThreadStateException e) {
            Register.frame.setVisible(true);
        }
    }

    //Öffnet das NewDate-Formular
    public void InitDate() {
        try {
            AddDate = new DateUI(this);
            AddDate.start();
        } catch (IllegalThreadStateException e) {
            AddDate.frame.setVisible(true);
        }
    }

    //Öffnet das Edit-Formular
    public void InitEdit(String[] data) {
        try {
            Editor = new EditUI(this, data);
            Editor.start();
        } catch (IllegalThreadStateException e) {
            Editor.getData(data);
            Editor.frame.setVisible(true);
        }
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
