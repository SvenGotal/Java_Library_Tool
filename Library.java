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
import java.util.Vector;

public class Library {

    /* Resources */
    private static final Logger log = LoggerFactory.getLogger("libraryLog");
    private Database context;
    private static JMenuBar menu;
    /* ======================================================================================= ***/

    /* General Queries */
    String getBooks = "SELECT id, book_name, book_author, book_status, user.user_name FROM book INNER JOIN user ON book.book_user = user.user_id";
    String getUsers = "SELECT * FROM user";
    /* ======================================================================================= ***/

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

    public Library() {
        context = new Database();
        /* Menu bar */
        setupMenuBar();
        /* ======================================================================================= ***/

        /* Listeners ***/
        buttonExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (context != null) {
                    context.CloseConnection();
                    log.info("Closing Connection;");
                }
                System.exit(0);

            }
        });
        buttonBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                booksData(getBooks);

            }
        });
        buttonUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                usersData(getUsers);
            }
        });
        buttonSearchBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String search = textFieldBooks.getText();

                if (search == "" || search == null) {
                    return;
                }

                String statement = getBooks + " WHERE book_name LIKE " + "'%" + search + "%'";
                booksData(statement);
                textFieldBooks.setText("");
            }
        });
        buttonSearchUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String search = textFieldUsers.getText();

                if (search.equals("") || search.equals(null)) {
                    return;
                }
                String statement = getUsers + " WHERE user_name LIKE " + "'%" + search + "%'";
                usersData(statement);
                textFieldUsers.setText("");


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

                if (rowIsSelected(table1)) {
                    String id = getSelectedRowId(table1);
                    BookRentalDialog rental = new BookRentalDialog();
                    rental.SetBookId(id);


                } else {
                    JOptionPane.showOptionDialog(
                            panelData,
                            "Please select book in the table.",
                            "Book not selected",
                            JOptionPane.CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            new String[]{"Cancel"},
                            "Cancel"
                    );
                }


            }
        });
        buttonReturnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (rowIsSelected(table1)) {
                    String id = getSelectedRowId(table1);
                    context = new Database();
                    context.ReturnBook(id);
                } else {
                    JOptionPane.showOptionDialog(
                            panelData,
                            "Please select book in the table.",
                            "Book not selected",
                            JOptionPane.CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            new String[]{"Cancel"},
                            "Cancel"
                    );
                }
            }
        });
        /* ======================================================================================= ***/
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
    private void DisplayData(Vector data, Vector columns, JTable table) {
        DefaultTableModel model = new DefaultTableModel(data, columns);
        table.setModel(model);
        log.info("Table successfully formatted.");
    }

    private void booksData(String statement) {

        Vector data = context.getData(statement);
        Vector columns = new Vector();
        columns.add("Book Id");
        columns.add("Title");
        columns.add("Author");
        columns.add("Status");
        columns.add("Member");

        DisplayData(data, columns, table1);
    }

    private void usersData(String statement) {

        Vector data = context.getData(statement);
        Vector columns = new Vector();
        columns.add("Id");
        columns.add("Member");

        DisplayData(data, columns, table1);
    }

    private void setupMenuBar() {
        menu = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem booksItem = new JMenuItem("Books");
        booksItem.setToolTipText("Show all books in the library.");
        booksItem.addActionListener(actionEvent -> booksData(getBooks));

        JMenuItem usersItem = new JMenuItem("Members");
        usersItem.setToolTipText("Show all library members.");
        usersItem.addActionListener(actionEvent -> usersData(getUsers));

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setToolTipText("Exit application.");
        exitItem.addActionListener((actionEvent -> System.exit(0)));

        fileMenu.add(booksItem);
        fileMenu.add(usersItem);
        fileMenu.add(exitItem);
        menu.add(fileMenu);

    }

    private String getSelectedRowId(JTable table) {

        int rowNum = table.getSelectedRow();
        int colNum = 0;

        return table.getValueAt(rowNum, colNum).toString();
    }

    private boolean rowIsSelected(JTable table) {
        return !(table.getSelectedRow() == -1);
    }
    /* ======================================================================================= ***/

}
