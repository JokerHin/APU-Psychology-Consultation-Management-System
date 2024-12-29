/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apu.psychology.consultation.management.system;

/**
 *
 * @author behbe
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.awt.font.TextAttribute;

public class LoginGUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel signUpLabel;
    private final UserService userService;

    // Constructor
    public LoginGUI() {
        userService = new UserService(new FileHandling());
        setTitle("APU Psychology Consultation Management System - Login");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        // Set the layout to BorderLayout
        setLayout(new BorderLayout());

        // Background Panel with Image
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/apu/psychology/consultation/management/system/pic/Consultation.png")));
        background.setLayout(new GridBagLayout()); // Using GridBagLayout to center components
        add(background);

        // Panel to hold login form components
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(161, 127, 105, 40)); // login panel color & transparancy
        loginPanel.setPreferredSize(new Dimension(350, 250));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        // Email Field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(200, 30));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(200, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // Sign up Button
        signUpLabel = new JLabel("Don't have an account? Sign Up");
        signUpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpLabel.setForeground(Color.BLUE);
        signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        // Add underline effect for label
        signUpLabel.setFont(signUpLabel.getFont().deriveFont(Collections.singletonMap(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)));

        // Add mouse listener for navigation
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SignUpGUI().setVisible(true);
                dispose();
            }
        });

        // Adding components to login panel
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(emailLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(emailField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createVerticalStrut(20));

        loginPanel.add(signUpLabel);
        loginPanel.add(Box.createVerticalStrut(20));

        // Adding login panel to the background label
        background.add(loginPanel);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.login(email, password);
        if (user != null) {
            if (user instanceof Student) {
                new StudentDashboardGUI((Student) user, userService).setVisible(true);
            } else if (user instanceof Lecturer) {
                new LecturerDashboardGUI((Lecturer) user, userService).setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


