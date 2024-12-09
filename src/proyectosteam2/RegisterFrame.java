/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosteam2;

/**
 *
 * @author Junior Nuñes
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox adminCheckBox;
    private JButton registerButton;
    private JButton backButton;
    private UserManager userManager;

    public RegisterFrame() {
        userManager = new UserManager();
        setTitle("Register");
        setSize(300, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel usernameLabel = new JLabel("Usuario:");
        usernameLabel.setBounds(20, 20, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(100, 20, 150, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 60, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(100, 60, 150, 25);
        add(passwordField);

        adminCheckBox = new JCheckBox("Admin");
        adminCheckBox.setBounds(100, 100, 150, 25);
        add(adminCheckBox);

        registerButton = new JButton("Registrar");
        registerButton.setBounds(20, 140, 100, 25);
        add(registerButton);

        backButton = new JButton("Atras");
        backButton.setBounds(150, 140, 100, 25);
        add(backButton);

        registerButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        boolean isAdmin = adminCheckBox.isSelected();

        if (userManager.registerUser(username, password, isAdmin)) {
            JOptionPane.showMessageDialog(null, "Usuario Registrado Correctamente!");

            new MainMenuFrame(username).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Usuario ya existe.");
        }
    }
});
        setLocationRelativeTo(null);


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
    }
}