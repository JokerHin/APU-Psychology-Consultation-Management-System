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
import java.util.List;

public class FeedbackFormGUI extends JFrame {
    private User user;
    private UserService userService;
    private JTextField appointmentIdField;
    private JTextArea feedbackArea;
    private JComboBox<Integer> ratingComboBox;
    private JCheckBox anonymousCheckBox;
    private JButton submitButton;
    private JButton cancelButton;

    // Constructor
    public FeedbackFormGUI(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
        setTitle("Give Feedback");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        // Creating panel to hold components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Adjusting insets and fill
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Appointment ID Label and Text Field
        JLabel appointmentIdLabel = new JLabel("Appointment ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(appointmentIdLabel, gbc);

        appointmentIdField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(appointmentIdField, gbc);

        // Feedback Label and Text Area
        JLabel feedbackLabel = new JLabel("Feedback:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(feedbackLabel, gbc);

        feedbackArea = new JTextArea(5, 20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(feedbackArea), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        if (user instanceof Student){
            // Rating Label and Combo Box
            JLabel ratingLabel = new JLabel("Rating (1-5):");
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(ratingLabel, gbc);

            ratingComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(ratingComboBox, gbc);

        // Anonymous Feedback Checkbox (only for students)

            anonymousCheckBox = new JCheckBox("Submit Anonymously");
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(anonymousCheckBox, gbc);
        }

        // Submit and Cancel Buttons
        submitButton = new JButton("Submit");
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(submitButton, gbc);

        cancelButton = new JButton("Cancel");
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(cancelButton, gbc);

        // Adding action listeners for buttons
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitFeedback();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateBack();
            }
        });

        add(panel);
    }

    // Submit feedback
    private void submitFeedback() {
        String appointmentId = appointmentIdField.getText().trim();
        String feedbackText = feedbackArea.getText().trim();
        int rating = 0;
        boolean isAnonymous = anonymousCheckBox != null && anonymousCheckBox.isSelected();

        if (appointmentId.isEmpty() || feedbackText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Appointment ID and Feedback are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if appointment exists and is associated with the user
        Appointment appointment = getAppointmentById(appointmentId);
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "Appointment ID does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isAppointmentAssociatedWithUser(appointment)) {
            JOptionPane.showMessageDialog(this, "You are not associated with this appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (user instanceof Student){
            rating = (int) ratingComboBox.getSelectedItem();
        }
        
        // Generate unique feedback ID
        String feedbackId = Feedback.generateFeedbackId();

        // Create Feedback object
        Feedback newFeedback = new Feedback(
                feedbackId,
                appointmentId,
                user.getUserId(),
                user.getName(),
                user.getRole(),
                rating,
                feedbackText,
                isAnonymous
        );

        // Add feedback to feedback.txt
        Feedback.addFeedback(newFeedback);

        JOptionPane.showMessageDialog(this, "Feedback submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        navigateBack();
    }

    // Get appointment by ID
    private Appointment getAppointmentById(String appointmentId) {
        List<Appointment> appointments = Appointment.readAppointments(Appointment.FILE_PATH);
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    // Check if the appointment is associated with the user
    private boolean isAppointmentAssociatedWithUser(Appointment appointment) {
        if (user instanceof Student) {
            return appointment.getStudentId().equals(user.getUserId());
        } else if (user instanceof Lecturer) {
            return appointment.getLecturerId().equals(user.getUserId());
        }
        return false;
    }

    // back
    private void navigateBack() {
        if (user instanceof Student) {
            new StudentDashboardGUI((Student) user, userService).setVisible(true);
        } else if (user instanceof Lecturer) {
            new LecturerDashboardGUI((Lecturer) user, userService).setVisible(true);
        }
        dispose();
    }
}


