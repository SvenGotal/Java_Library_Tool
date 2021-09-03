package hr.sven.seminarski_java;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.Vector;

public class BookRentalDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable table1;
    private JTextField textFieldUser;
    private JButton searchButton;
    private Database context;
    private String bookId;

    public BookRentalDialog() {
        setContentPane(contentPane);
        setModal(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        getRootPane().setDefaultButton(buttonOK);
        pack();
        setVisible(true);

        context = new Database();
        populateTable(table1);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String username = textFieldUser.getText();
                Vector data = context.getData("SELECT * FROM user WHERE user_name LIKE '%" + username + "%'" );
                Vector headers = new Vector();
                headers.add("Id");
                headers.add("Name");

                displayData(data, headers, table1);


            }
        });
    }

    private void onOK() {

        if(rowIsSelected(table1)){
            alterBookRow();
            dispose();
        }
        else{
            JOptionPane.showOptionDialog(
                    contentPane,
                    "Please select user in the table.",
                    "User not selected",
                    JOptionPane.CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[] {"Cancel"},
                    "Cancel"
            );

        }

    }
    private void onCancel() {

        dispose();
    }
    public void SetBookId(String id){
        this.bookId = id;
    }
    private void alterBookRow(){

        String userId = getSelectedRowId(table1);
        context.RentBook(bookId, "RENTED", userId);

    }
    private String getSelectedRowId(JTable table){

        int rowNum = table.getSelectedRow();
        int colNum = 0;

        return table.getValueAt(rowNum, colNum).toString();
    }
    private boolean rowIsSelected(JTable table){
        return !(table.getSelectedRow() == -1);
    }
    private void populateTable(JTable table){

        Vector data = context.getData(context.getUsers);
        Vector headers = new Vector();
        headers.add("Id");
        headers.add("Name");

        displayData(data, headers, table);

    }
    private void displayData(Vector data, Vector columns, JTable table){
        DefaultTableModel model = new DefaultTableModel(data, columns);
        table.setModel(model);
    }

}
