package hr.sven.seminarski_java;

import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.ThreadLocalRandom;

public class AddBookDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldTitle;
    private JTextField textFieldAuthor;
    private Database context;

    public AddBookDialog() {
        setContentPane(contentPane);
        setModal(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        getRootPane().setDefaultButton(buttonOK);
        pack();
        setVisible(true);

        context = new Database();

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
    }

    private void onOK() {
        insertBook();
        dispose();
    }

    private void onCancel() {

        dispose();
    }
    private void insertBook(){

        Integer rand = ThreadLocalRandom.current().nextInt(1, 999999);
        String id = rand.toString();
        String name = textFieldTitle.getText();
        String author = textFieldAuthor.getText();
        String status = "Available";
        String user = "1";

        if(name.equals("") || author.equals("")){
            return;
        }

        context.InsertBook(id, name, author,status,user);
        context.CloseConnection();



    }
}
