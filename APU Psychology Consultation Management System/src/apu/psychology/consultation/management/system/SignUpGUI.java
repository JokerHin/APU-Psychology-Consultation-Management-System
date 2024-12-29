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
import java.util.concurrent.atomic.AtomicInteger;

public class SignUpGUI extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox lecturerCheckBox;
    private JButton signUpButton;
    private JButton backButton;
    private final UserService userService;
    private final FileHandling fileHandling;

    // AtomicInteger counters for student and lecturer IDs
    private static AtomicInteger studentIdCounter;
    private static AtomicInteger lecturerIdCounter;

    // Constructor
    public SignUpGUI() {
        fileHandling = new FileHandling(); // Initialize FileHandling
        userService = new UserService(fileHandling);

        // Initialize the counters
        studentIdCounter = new AtomicInteger(fileHandling.getMaxUserId("S") + 1);
        lecturerIdCounter = new AtomicInteger(fileHandling.getMaxUserId("L") + 1);

        setTitle("APU Psychology Consultation Management System - Sign Up");
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

        // Panel to hold sign-up form components
        JPanel signUpPanel = new JPanel();
        signUpPanel.setBackground(new Color(161, 127, 105, 40)); // Sign-up panel color & transparency
        signUpPanel.setPreferredSize(new Dimension(350, 360));
        signUpPanel.setLayout(new BoxLayout(signUpPanel, BoxLayout.Y_AXIS));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTH; // Keep the panel in the middle-bottom position
        gbc.insets = new Insets(0, 0, -100, 0); // Adjust top inset to have more space @ top

        // Name Field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(200, 25));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email Field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(200, 25));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(200, 25));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Confirm Password Field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(200, 25));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Lecturer Checkbox
        lecturerCheckBox = new JCheckBox("Sign up as Lecturer");
        lecturerCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        lecturerCheckBox.setBackground(new Color(161, 127, 105, 50)); // Match panel background

        // Sign Up Button
        signUpButton = new JButton("Sign Up");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpButton.addActionListener(new SignUpButtonListener());

        // Back Button
        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
        });

        // Adding components to sign-up panel
        signUpPanel.add(Box.createVerticalStrut(10));
        signUpPanel.add(nameLabel);
        signUpPanel.add(Box.createVerticalStrut(3));
        signUpPanel.add(nameField);
        signUpPanel.add(Box.createVerticalStrut(8));
        signUpPanel.add(emailLabel);
        signUpPanel.add(Box.createVerticalStrut(3));
        signUpPanel.add(emailField);
        signUpPanel.add(Box.createVerticalStrut(8));
        signUpPanel.add(passwordLabel);
        signUpPanel.add(Box.createVerticalStrut(3));
        signUpPanel.add(passwordField);
        signUpPanel.add(Box.createVerticalStrut(8));
        signUpPanel.add(confirmPasswordLabel);
        signUpPanel.add(Box.createVerticalStrut(3));
        signUpPanel.add(confirmPasswordField);
        signUpPanel.add(Box.createVerticalStrut(10));
        signUpPanel.add(lecturerCheckBox);
        signUpPanel.add(Box.createVerticalStrut(15));
        signUpPanel.add(signUpButton);
        signUpPanel.add(Box.createVerticalStrut(10));
        signUpPanel.add(backButton);

        // Adding sign-up panel to the background label
        background.add(signUpPanel, gbc);
    }

    // Action Listener for Sign Up Button
    private class SignUpButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            boolean isLecturer = lecturerCheckBox.isSelected();

            // Validate input fields
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(SignUpGUI.this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(SignUpGUI.this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Register user
            boolean registrationSuccess;
            if (isLecturer) {
                String userId = generateUserId(true);
                registrationSuccess = userService.registerLecturer(userId, name, email, password);
            } else {
                String userId = generateUserId(false);
                registrationSuccess = userService.registerStudent(userId, name, email, password);
            }

            if (registrationSuccess) {
                JOptionPane.showMessageDialog(SignUpGUI.this, "Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new LoginGUI().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(SignUpGUI.this, "Email is already registered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Generate user id
    private String generateUserId(boolean isLecturer) {
        String rolePrefix = isLecturer ? "L" : "S";
        int idNumber;

        if (isLecturer) {
            idNumber = lecturerIdCounter.getAndIncrement();
        } else {
            idNumber = studentIdCounter.getAndIncrement();
        }

        return String.format("%s%03d", rolePrefix, idNumber);
    }
}


