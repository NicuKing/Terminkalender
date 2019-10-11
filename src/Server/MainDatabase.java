package Server;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainDatabase {

    DatabaseConnection dbconnection = new DatabaseConnection();
    private Connection connection = dbconnection.getConnection();

    //hohlt alle Termine von der Datenbank
    public String GetAllDates() {
        try {
            Statement statement = connection.createStatement();
            String sql="SELECT DateId, Date, ShortDescrip, Done FROM dates;";
            ResultSet result = statement.executeQuery(sql);
            int nCol = result.getMetaData().getColumnCount();
            ArrayList<String[]> table = new ArrayList<>();
            while( result.next()) {
                String[] row = new String[nCol];
                for( int iCol = 1; iCol <= nCol; iCol++ ){
                    Object obj = result.getObject( iCol );
                    row[iCol-1] = (obj == null) ?null:obj.toString();
                }
                table.add( row );
            }
            //zu einem String machen
            String dates = "";
            for( String[] row: table ) {
                for( String s: row ) {
                    dates += s+"#";
                }
                dates.substring(0, dates.length() - 1);
                dates += "%";
            }

            return dates;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //holt einen gewissen Termin von der Datenbank
    public String GetDate(String shortDescrip) {
        try {
            Statement statement = connection.createStatement();
            String sql= "SELECT Date, ShortDescrip, LongDescrip, Done FROM dates WHERE ShortDescrip='"+shortDescrip+"';";
            ResultSet result = statement.executeQuery(sql);
            int nCol = result.getMetaData().getColumnCount();
            ArrayList<String[]> table = new ArrayList<>();
            while( result.next()) {
                String[] row = new String[nCol];
                for( int iCol = 1; iCol <= nCol; iCol++ ){
                    Object obj = result.getObject( iCol );
                    row[iCol-1] = (obj == null) ?null:obj.toString();
                }
                table.add( row );
            }
            //zu einem String machen
            String dates = "";
            for( String[] row: table ) {
                for( String s: row ) {
                    dates += s+"#";
                }
                dates.substring(0, dates.length() - 1);
            }
            return dates;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //fügt einen neuen User der Datenbank hinzu
    public boolean AddUser(String email, String username, String hashed_password, String salt) {
        String sql="INSERT INTO users (Username, Email, Password, Salt) VALUES ('"+username+"','"+email+"','"+hashed_password+"','"+salt+"');";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //holt das Salt von der Datenbank um das Passwort zu überprüfen
    public String getSaltFromDatabase(String username) {
        try {
            String Salt = "";
            Statement statement = connection.createStatement();
            String sql="SELECT Salt FROM users WHERE Username=\""+username+"\";";
            ResultSet result = statement.executeQuery(sql);
            int nCol = result.getMetaData().getColumnCount();
            while( result.next()) {
                String[] row = new String[nCol];
                for( int iCol = 1; iCol <= nCol; iCol++ ){
                    Object obj = result.getObject( iCol );
                    Salt = (obj == null) ?null:obj.toString();

                }
            }
            return Salt;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //holt das Password um es mit dem vom Client eingegebenen Passwort zu vergelichen
    public boolean getLogin(String username, String hashed_password) {
        try {
            String Database_Password = "";
            Statement statement = connection.createStatement();
            String sql="SELECT Password FROM users WHERE Username=\""+username+"\";";
            ResultSet result = statement.executeQuery(sql);
            int nCol = result.getMetaData().getColumnCount();
            while( result.next()) {
                String[] row = new String[nCol];
                for( int iCol = 1; iCol <= nCol; iCol++ ){
                    Object obj = result.getObject( iCol );
                    Database_Password = (obj == null) ?null:obj.toString();
                }
            }
            if(Database_Password.equals(hashed_password)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //läscht Termin, welcher mit der ShortDescriptino identifiziert wird
    public boolean DeleteDate(String shortDescription) {
        try {
            Statement statement = connection.createStatement();
            String sql="DELETE FROM dates WHERE ShortDescrip=\""+shortDescription+"\";";
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //fügt ein neuen Termin der Datenbank hinzu
    public boolean AddDate(String date, String shortDesrip, String longDescrip) {
        try {
            Statement statement = connection.createStatement();
            String sql="INSERT INTO dates (Date, ShortDescrip, LongDescrip, Done) VALUES ('"+date+"','"+shortDesrip+"','"+longDescrip+"','0');";
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Schaut ob der Username schon existiert
    public boolean UsernameExists(String username, String email) {
        try {
            Statement statement = connection.createStatement();
            String sql="SELECT * FROM users WHERE Username=\""+username+"\" OR Email=\""+email+"\";";
            ResultSet result = statement.executeQuery(sql);
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //verändert die Daten eines Termin der mit der ShortDescription identifiziert wird
    public boolean UpdateDate(String date, String shortDescrip, String longDescrip, String isDone, String oldDescrip) {
        try {
            Statement statement = connection.createStatement();
            String sql="UPDATE dates SET Date='"+date+"',ShortDescrip='"+shortDescrip+"',LongDescrip='"+longDescrip+"',Done='"+isDone+"' WHERE ShortDescrip='"+oldDescrip+"';";
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
