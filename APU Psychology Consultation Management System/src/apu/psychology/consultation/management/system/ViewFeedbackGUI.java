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
import java.util.List;
import java.util.ArrayList;

public class ViewFeedbackGUI extends JFrame {
    private final User user;
    private final UserService userService;
    private JTable feedbackTable;
    private JButton backButton;
    
    // Constructor
    public ViewFeedbackGUI(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
        setTitle("View Feedback");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        // Create panel
        JPanel panel = new JPanel(new BorderLayout());

        // Feedback Data load from text file
        String[] columnNames = {"Feedback ID", "Appointment ID", "User ID", "Username", "Role", "Rating", "Comments"};
        String[][] data = getFeedbackData();

        // Feedback Table
        feedbackTable = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(feedbackTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Back Button
        backButton = new JButton("Back");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(backButton);
        backButton.addActionListener(e -> navigateBack());

        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);
    }

    //Get feedback data from text file
    private String[][] getFeedbackData() {
        List<Feedback> feedbackList = Feedback.readFeedback();
        List<String[]> relevantFeedbacks = new ArrayList<>();

        for (Feedback feedback : feedbackList) {
            boolean isAnonymous = feedback.isAnonymous();
            boolean isOwnFeedback = feedback.getUserId().equals(user.getUserId());
            String[] feedbackData = new String[7];

            // Determine if feedback is relevant to the user
            if (user instanceof Lecturer && feedback.getRoleId().equals("Student")) {
                // Lecturer viewing feedback from students
                // Check if the feedback is related to the lecturer
                if (isFeedbackRelatedToLecturer(feedback, user.getUserId())) {
                    populateFeedbackData(feedbackData, feedback, isAnonymous, isOwnFeedback);
                    relevantFeedbacks.add(feedbackData);
                }
            } else if (user instanceof Student && feedback.getRoleId().equals("Lecturer")) {
                // Student viewing feedback from lecturers
                // Check if the feedback is related to the student
                if (isFeedbackRelatedToStudent(feedback, user.getUserId())) {
                    populateFeedbackData(feedbackData, feedback, isAnonymous, isOwnFeedback);
                    relevantFeedbacks.add(feedbackData);
                }
            }
        }

        // Convert List to Array
        String[][] data = new String[relevantFeedbacks.size()][7];
        for (int i = 0; i < relevantFeedbacks.size(); i++) {
            data[i] = relevantFeedbacks.get(i);
        }
        return data;
    }

    // populate those anonymous feedbck
    private void populateFeedbackData(String[] feedbackData, Feedback feedback, boolean isAnonymous, boolean isOwnFeedback) {
        feedbackData[0] = feedback.getFeedbackId();

        if (isAnonymous && !isOwnFeedback && feedback.getRoleId().equals("Student")) {
            // Hide details for anonymous feedback from students
            feedbackData[1] = "-";
            feedbackData[2] = "Anonymous";
            feedbackData[3] = "Anonymous";
        } else {
            feedbackData[1] = feedback.getAppointmentId();
            feedbackData[2] = feedback.getUserId();
            feedbackData[3] = feedback.getUsername();
        }

        feedbackData[4] = feedback.getRoleId();
        feedbackData[5] = String.valueOf(feedback.getRating());
        feedbackData[6] = feedback.getComments();
    }

    // make sure feedback is related to the lecturer
    private boolean isFeedbackRelatedToLecturer(Feedback feedback, String lecturerId) {
        Appointment appointment = getAppointmentById(feedback.getAppointmentId());
        return appointment != null && appointment.getLecturerId().equals(lecturerId);
    }

    // make sure the feedback is related to the student
    private boolean isFeedbackRelatedToStudent(Feedback feedback, String studentId) {
        // Assuming the appointmentId can be used to verify if the student is involved
        Appointment appointment = getAppointmentById(feedback.getAppointmentId());
        return appointment != null && appointment.getStudentId().equals(studentId);
    }

    // Get appointment id
    private Appointment getAppointmentById(String appointmentId) {
        List<Appointment> appointments = Appointment.readAppointments(Appointment.FILE_PATH);
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    // Go back to previosu screen
    private void navigateBack() {
        if (user instanceof Student) {
            new StudentDashboardGUI((Student) user, userService).setVisible(true);
        } else if (user instanceof Lecturer) {
            new LecturerDashboardGUI((Lecturer) user, userService).setVisible(true);
        }
        dispose();
    }
}

