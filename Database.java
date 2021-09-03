package hr.sven.seminarski_java;

import com.mysql.cj.x.protobuf.MysqlxCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.*;
import java.util.Vector;


public class Database {

    /* Resources */
    private final String connectionString;
    private Connection conn;
    private ResultSet rs;
    private Statement stmt;
    private static final Logger log = LoggerFactory.getLogger("libraryLog");
    /* ======================================================================================= ***/

    /* Hardcoded General Queries */
    String getBooks = "SELECT book_name, book_author, book_status, user.user_name FROM book INNER JOIN user ON book.book_user = user.user_id";
    String getUsers = "SELECT * FROM user";
    String insertUser = "INSERT INTO user VALUES";
    String insertBook = "INSERT INTO book VALUES";
    String alterBook = "UPDATE book SET";
    /* ======================================================================================= ***/

    public Database(){
        /* Database Configuration */
        connectionString = "jdbc:mysql://localhost:3306/library?user=root";
        OpenConnection();
        /* ======================================================================================= ***/
    }
    public Database(String connectionString){
        this.connectionString = connectionString;
        OpenConnection();
    }

    private void OpenConnection() {
        try {
            if (conn == null)
                conn = DriverManager.getConnection(connectionString);

            log.info("Connection successful.");
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Unable to open connection;");
        }
    }
    public void CloseConnection(){
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            log.error("Unable to close connection!");
        }

    }
    public Vector getData(String statement) {

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(statement);
            int colCount = rs.getMetaData().getColumnCount();

            Vector data = parseData(colCount);
            rs.close();

            log.info("Data successfully extracted.");

            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Unable to retrieve data.");
            return null;
        }
    }
    public Vector getHeaders(int colCount) {
        try {
            Vector columns = new Vector(colCount);
            for (int i = 1; i <= colCount; ++i) {
                columns.add(rs.getMetaData().getColumnName(i));
            }

            log.info("Headers successfully extracted from table.");

            return columns;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Unable to retrieve header data");
            return null;
        }
    }
    public Vector parseData(int colCount) {
        try {
            Vector data = new Vector();

            while (rs.next()) {
                Vector row = new Vector(colCount);
                for (int i = 1; i <= colCount; i++) {
                    //row[i-1] = rs.getObject(i);
                    row.add(rs.getString(i));
                }
                data.add(row);
            }

            log.info("Data successfully parsed from RowSet.");

            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Unable to parse data.");
            return null;
        }

    }
    public void InsertUser(String id, String name){
        try{
            stmt = conn.createStatement();

            rs = stmt.executeQuery("SELECT user_id FROM user");
            while(rs.next()){
                String result = rs.getObject(1).toString();
                if( result.equals(id)) {
                    log.info("User with that ID already exists!");
                    return;
                }
            }

            stmt.executeUpdate(insertUser + "('" + id + "','" + name + "')");
        }
        catch(SQLException e){
            e.printStackTrace();
            log.error("Unable to insert data!");
        }
    }
    public void InsertBook(String id, String name, String author, String status, String user){
        try{
            stmt = conn.createStatement();
            String separator = "','";

            stmt.executeUpdate(insertBook + "('" + id + separator + name + separator + author + separator + status + separator + user + "')");

        }
        catch(SQLException e){
            e.printStackTrace();
            log.error("Unable to insert book!");
        }
    }
    public void RentBook(String id, String status, String user_id){

        String updateBook = alterBook + " book_status = '" + status + "', book_user = '" + user_id + "' WHERE id = '" + id +"';";
        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(updateBook);
        }
        catch(SQLException e){
            e.printStackTrace();
            log.error("Unable to alter table row!");
        }


    }
    public void ReturnBook(String id){
        String updateBook = alterBook + " book_status = 'Available', book_user = '1' WHERE id = '" + id + "';";
        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(updateBook);
        }
        catch(SQLException e){
            e.printStackTrace();
            log.error("Unable to alter table row!");
        }

    }
}
