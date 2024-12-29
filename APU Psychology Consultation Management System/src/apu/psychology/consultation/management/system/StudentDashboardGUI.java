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

public class StudentDashboardGUI extends JFrame {
    private final Student student;
    private final UserService userService;
    private JButton upcomingAppointmentsButton;
    private JButton pastAppointmentsButton;
    private JButton bookAppointmentButton;
    private JButton viewFeedbackButton;
    private JButton giveFeedbackButton;
    private JButton logoutButton;

    // Constructor
    public StudentDashboardGUI(Student student, UserService userService) {
        this.student = student;
        this.userService = userService;
        setTitle("Student Dashboard - " + student.getName());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        // Set the layout to BorderLayout
        setLayout(new BorderLayout());

        // Background image
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/apu/psychology/consultation/management/system/pic/Consultation.png")));
        background.setLayout(new GridBagLayout());
        add(background);

        GridBagConstraints gbc = new GridBagConstraints();

        // Profile information panel
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(new Color(161, 127, 105, 60)); // bg color
        profilePanel.setPreferredSize(new Dimension(350, 110));
        profilePanel.setMinimumSize(new Dimension(350, 110));
        profilePanel.setMaximumSize(new Dimension(350, 110)); // make sure the size dont go over

        // Student Info Labels
        JLabel studentIdLabel = new JLabel("Student ID: " + student.getUserId());
        JLabel nameLabel = new JLabel("Name: " + student.getName());
        JLabel emailLabel = new JLabel("Email: " + student.getEmail());
        JLabel roleLabel = new JLabel("Role: " + student.getRole());

        // Font type and size and color and alignment
        Font labelFont = new Font("Serif", Font.BOLD, 14);
        studentIdLabel.setFont(labelFont);
        nameLabel.setFont(labelFont);
        emailLabel.setFont(labelFont);
        roleLabel.setFont(labelFont);
        
        studentIdLabel.setForeground(new Color(43, 34, 5));
        nameLabel.setForeground(new Color(43, 34, 5));
        emailLabel.setForeground(new Color(43, 34, 5));
        roleLabel.setForeground(new Color(43, 34, 5));

        studentIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to Profile Panel
        profilePanel.add(Box.createVerticalStrut(10)); 
        profilePanel.add(studentIdLabel);
        profilePanel.add(Box.createVerticalStrut(5)); // Gap between labels
        profilePanel.add(nameLabel);
        profilePanel.add(Box.createVerticalStrut(5));
        profilePanel.add(emailLabel);
        profilePanel.add(Box.createVerticalStrut(5));
        profilePanel.add(roleLabel);

        // profile panel location
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(100, 0, 15, 0); // Moved profile panel down more
        gbc.fill = GridBagConstraints.NONE; // Prevent automatic resizing
        gbc.weightx = 0;
        gbc.weighty = 0;
        background.add(profilePanel, gbc);

        // Button panel 
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // keep it transparent
        buttonPanel.setLayout(new GridLayout(3, 2, 20, 20)); // 3 rows, 2 columns and - spacing 1 spacing
        buttonPanel.setPreferredSize(new Dimension(400, 300));

        // Creating buttons
        upcomingAppointmentsButton = createStyledButton("Upcoming Appointments");
        pastAppointmentsButton = createStyledButton("Past Appointments");
        bookAppointmentButton = createStyledButton("Book Appointment");
        viewFeedbackButton = createStyledButton("View Feedback");
        giveFeedbackButton = createStyledButton("Give Feedback");
        logoutButton = createStyledButton("Logout");

        // Adding buttons to button panel
        buttonPanel.add(upcomingAppointmentsButton);
        buttonPanel.add(pastAppointmentsButton);
        buttonPanel.add(bookAppointmentButton);
        buttonPanel.add(viewFeedbackButton);
        buttonPanel.add(giveFeedbackButton);
        buttonPanel.add(logoutButton);

        // Adding button panel to background label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 20, 0);
        background.add(buttonPanel, gbc);

        // Adding action listeners for buttons
        upcomingAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UpcomingAppointmentGUI(student, userService).setVisible(true);
                dispose();
            }
        });

        pastAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PastAppointmentGUI(student, userService).setVisible(true);
                dispose();
            }
        });

        bookAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LecturerListGUI(student, userService).setVisible(true);
                dispose();
            }
        });

        viewFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewFeedbackGUI(student, userService).setVisible(true);
                dispose();
            }
        });

        giveFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FeedbackFormGUI(student, userService).setVisible(true);
                dispose();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginGUI().setVisible(true);
                dispose();
            }
        });
    }

    // Create styled button 
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(200, 40)); // Set preferred size for the button
        button.setMinimumSize(new Dimension(200, 40));   // Set minimum size so won't shrink (ltr it sometimes big sometimes small )
        button.setMaximumSize(new Dimension(200, 40)); 
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

}







