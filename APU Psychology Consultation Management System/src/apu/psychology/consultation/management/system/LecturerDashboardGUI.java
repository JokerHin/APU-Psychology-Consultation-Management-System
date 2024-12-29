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

public class LecturerDashboardGUI extends JFrame {
    private final Lecturer lecturer;
    private final UserService userService;
    private JButton upcomingAppointmentsButton;
    private JButton pastAppointmentsButton;
    private JButton manageTimeSlotButton;
    private JButton viewFeedbackButton;
    private JButton giveFeedbackButton;
    private JButton logoutButton;

    // Constructor
    public LecturerDashboardGUI(Lecturer lecturer, UserService userService) {
        this.lecturer = lecturer;
        this.userService = userService;
        setTitle("Lecturer Dashboard - " + lecturer.getName());
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
        profilePanel.setMaximumSize(new Dimension(350, 110)); // make sure the size don't go over

        // Lecturer Info Labels
        JLabel lecturerIdLabel = new JLabel("Lecturer ID: " + lecturer.getUserId());
        JLabel nameLabel = new JLabel("Name: " + lecturer.getName());
        JLabel emailLabel = new JLabel("Email: " + lecturer.getEmail());
        JLabel roleLabel = new JLabel("Role: Lecturer");

        // Font type, size, color, and alignment
        Font labelFont = new Font("Serif", Font.BOLD, 14);
        lecturerIdLabel.setFont(labelFont);
        nameLabel.setFont(labelFont);
        emailLabel.setFont(labelFont);
        roleLabel.setFont(labelFont);

        lecturerIdLabel.setForeground(new Color(43, 34, 5));
        nameLabel.setForeground(new Color(43, 34, 5));
        emailLabel.setForeground(new Color(43, 34, 5));
        roleLabel.setForeground(new Color(43, 34, 5));

        lecturerIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to Profile Panel
        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(lecturerIdLabel);
        profilePanel.add(Box.createVerticalStrut(5)); // Gap between labels
        profilePanel.add(nameLabel);
        profilePanel.add(Box.createVerticalStrut(5));
        profilePanel.add(emailLabel);
        profilePanel.add(Box.createVerticalStrut(5));
        profilePanel.add(roleLabel);

        // Profile panel location
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
        buttonPanel.setLayout(new GridLayout(3, 2, 20, 20)); // 3 rows, 2 columns
        buttonPanel.setPreferredSize(new Dimension(400, 300));

        // Creating buttons
        upcomingAppointmentsButton = createStyledButton("Upcoming Appointments");
        pastAppointmentsButton = createStyledButton("Past Appointments");
        manageTimeSlotButton = createStyledButton("Manage Time Slots");
        viewFeedbackButton = createStyledButton("View Feedback");
        giveFeedbackButton = createStyledButton("Give Feedback");
        logoutButton = createStyledButton("Logout");

        // Adding buttons to button panel
        buttonPanel.add(upcomingAppointmentsButton);
        buttonPanel.add(pastAppointmentsButton);
        buttonPanel.add(manageTimeSlotButton);
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
                new UpcomingAppointmentGUI(lecturer, userService).setVisible(true);
                dispose();
            }
        });

        pastAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PastAppointmentGUI(lecturer, userService).setVisible(true);
                dispose();
            }
        });

        manageTimeSlotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TimeSlotGUI(lecturer, userService).setVisible(true);
                dispose();
            }
        });

        viewFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewFeedbackGUI(lecturer, userService).setVisible(true);
                dispose();
            }
        });

        giveFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FeedbackFormGUI(lecturer, userService).setVisible(true);
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

    // Create a styled button
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(200, 40)); // Set preferred size for the button
        button.setMinimumSize(new Dimension(200, 40));   // Set minimum size so it won't shrink
        button.setMaximumSize(new Dimension(200, 40)); 
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
