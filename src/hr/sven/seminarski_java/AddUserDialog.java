package hr.sven.seminarski_java;

import javax.swing.*;
import java.awt.event.*;

public class AddUserDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldName;
    private JTextField textFieldId;
    private Database context;

    public AddUserDialog() {
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
        insertUser();
        dispose();
    }
    private void onCancel() {

        dispose();
    }
    private void insertUser(){
        String id = textFieldId.getText();
        String name = textFieldName.getText();

        if(id.equals("") || name.equals(""))
            return;
        context.InsertUser(id, name);
        context.CloseConnection();

    }
}
