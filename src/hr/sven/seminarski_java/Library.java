package hr.sven.seminarski_java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.sql.Connection;
import java.sql.*;
import java.util.Vector;

public class Library {
    private JPanel panelData;
    private JTable table1;
    private JButton buttonExit;
    private JButton buttonSearchBooks;
    private JButton buttonAdduser;
    private JTextField textFieldBooks;
    private JTextField textFieldUsers;
    private JButton buttonSearchUsers;
    private JButton buttonAddBook;
    private JButton buttonBooks;
    private JButton buttonUsers;
    private JButton buttonRentBook;
    private JButton buttonReturnBook;
    private static JMenuBar menu;

    /* Resources */
    private final String connectionString;
    private Connection conn;
    private ResultSet rs;
    private Statement stmt;
    private static final Logger log = LoggerFactory.getLogger("libraryLog");
    /* ======================================================================================= ***/

    /* General Queries */
    String getBooks = "SELECT id, book_name, book_author, book_status, user.user_name FROM book INNER JOIN user ON book.book_user = user.user_id";
    String getUsers = "SELECT * FROM user";
    /* ======================================================================================= ***/

    public Library() {
        /* Database Configuration */
        connectionString = "jdbc:mysql://localhost:3306/library?user=root";

        /* Menu bar */
        setupMenuBar();
        /* ======================================================================================= ***/

        /* Listeners ***/
        buttonExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if (conn != null) {
                        conn.close();
                        log.info("Closing Connection;");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.error("Unable to close DB connection;");
                }
                System.exit(0);

            }
        });
        buttonBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                booksData();

            }
        });
        buttonUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                usersData();
            }
        });
        buttonSearchBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String search = textFieldBooks.getText();

                if (search == "" || search == null) {
                    return;
                }

                OpenConnection();
                String statement = getBooks + " WHERE book_name LIKE " + "'%" + search + "%'";
                Vector data = getData(statement);
                Vector columns = new Vector();
                columns.add("Title");
                columns.add("Author");
                columns.add("Status");
                columns.add("Member");

                textFieldBooks.setText("");
                DisplayData(data,columns,table1);
            }
        });
        buttonSearchUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String search = textFieldUsers.getText();

                if (search == "" || search == null) {
                    return;
                }

                OpenConnection();
                String statement = getUsers + " WHERE user_name LIKE " + "'%" + search + "%'";
                Vector data = getData(statement);
                Vector columns = new Vector();
                columns.add("Id");
                columns.add("Member");

                textFieldUsers.setText("");
                DisplayData(data,columns,table1);

            }
        });
        buttonAdduser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddUserDialog user = new AddUserDialog();



            }
        });
        buttonAddBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddBookDialog book = new AddBookDialog();
            }
        });
        buttonRentBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(rowIsSelected(table1)){
                    String id = getSelectedRowId(table1);
                    BookRentalDialog rental = new BookRentalDialog();
                    rental.SetBookId(id);



                }
                else{
                    JOptionPane.showOptionDialog(
                            panelData,
                            "Please select book in the table.",
                            "Book not selected",
                            JOptionPane.CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            new String[] {"Cancel"},
                            "Cancel"
                    );
                }


            }
        });


        /* ======================================================================================= ***/

        buttonReturnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(rowIsSelected(table1)) {
                    String id = getSelectedRowId(table1);
                    Database context = new Database();
                    context.ReturnBook(id);
                    context.CloseConnection();
                }
                else{
                    JOptionPane.showOptionDialog(
                            panelData,
                            "Please select book in the table.",
                            "Book not selected",
                            JOptionPane.CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            new String[] {"Cancel"},
                            "Cancel"
                    );
                }


            }
        });
    }

    /* Main */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Library");
        frame.setContentPane(new Library().panelData);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(menu);
        frame.pack();
        frame.setVisible(true);
    }
    /* ======================================================================================= ***/

    /* Helper methods */
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
    private Vector getData(String statement) {

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
    private Vector getHeaders(int colCount) {
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
    private Vector parseData(int colCount) {
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
    private void DisplayData(Vector data, Vector columns, JTable table){
        DefaultTableModel model = new DefaultTableModel(data, columns);
        table.setModel(model);
        log.info("Table successfully formatted.");
    }
    private void booksData(){
        OpenConnection();

        Vector data = getData(getBooks);
        Vector columns = new Vector();
        columns.add("Book Id");
        columns.add("Title");
        columns.add("Author");
        columns.add("Status");
        columns.add("Member");

        DisplayData(data, columns, table1);
    }
    private void usersData(){
        OpenConnection();

        Vector data = getData(getUsers);
        Vector columns = new Vector();
        columns.add("Id");
        columns.add("Member");

        DisplayData(data, columns, table1);
    }
    private void setupMenuBar(){
        menu = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem booksItem = new JMenuItem("Books");
        booksItem.setToolTipText("Show all books in the library.");
        booksItem.addActionListener(actionEvent ->  booksData());

        JMenuItem usersItem = new JMenuItem("Members");
        usersItem.setToolTipText("Show all library members.");
        usersItem.addActionListener(actionEvent -> usersData());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setToolTipText("Exit application.");
        exitItem.addActionListener((actionEvent -> System.exit(0)));

        fileMenu.add(booksItem);
        fileMenu.add(usersItem);
        fileMenu.add(exitItem);
        menu.add(fileMenu);

    }
    private String getSelectedRowId(JTable table){

        int rowNum = table.getSelectedRow();
        int colNum = 0;

        return table.getValueAt(rowNum, colNum).toString();
    }
    private boolean rowIsSelected(JTable table){
        return !(table.getSelectedRow() == -1);
    }
    /* ======================================================================================= ***/

}
