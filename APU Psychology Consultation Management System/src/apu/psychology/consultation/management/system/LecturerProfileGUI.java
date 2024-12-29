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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class LecturerProfileGUI extends JFrame {
    private Lecturer lecturer;
    private Student student;
    private UserService userService;
    private JLabel lecturerInfoLabel;
    private JTable feedbackTable;
    private JTable slotTable;
    private JButton bookButton;
    private JButton backButton;

    public LecturerProfileGUI(Student student, Lecturer lecturer, UserService userService) {
        this.student = student;
        this.lecturer = lecturer;
        this.userService = userService;
        setTitle("Lecturer Profile");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        // Main panel
        JPanel panel = new JPanel(new BorderLayout());

        // Lecturer Info Panel
        JPanel lecturerInfoPanel = new JPanel(new GridLayout(4, 1));
        lecturerInfoPanel.setBorder(BorderFactory.createTitledBorder("Lecturer Information"));

        // Lecturer Information Labels
        String lecturerId = lecturer.getUserId();
        String name = lecturer.getName();
        String email = lecturer.getEmail();

        int availableSlotsCount = getAvailableSlotsCount();

        lecturerInfoPanel.add(new JLabel("Lecturer ID: " + lecturerId, SwingConstants.CENTER));
        lecturerInfoPanel.add(new JLabel("Name: " + name, SwingConstants.CENTER));
        lecturerInfoPanel.add(new JLabel("Email: " + email, SwingConstants.CENTER));
        lecturerInfoPanel.add(new JLabel("Available Slots: " + availableSlotsCount, SwingConstants.CENTER));

        panel.add(lecturerInfoPanel, BorderLayout.NORTH);

        // Feedback Table
        String[] feedbackColumnNames = {"User ID", "Name", "Feedback", "Rating"};
        String[][] feedbackData = getFeedbackData();

        feedbackTable = new JTable(feedbackData, feedbackColumnNames);
        JScrollPane feedbackScrollPane = new JScrollPane(feedbackTable);
        feedbackScrollPane.setBorder(BorderFactory.createTitledBorder("Feedback"));
        feedbackScrollPane.setPreferredSize(new Dimension(750, 200));

        panel.add(feedbackScrollPane, BorderLayout.CENTER);

        // Slot Table and Booking
        String[] slotColumnNames = {"Slot ID", "Date", "Time", "Location"};
        String[][] slotData = getAvailableSlotsData();

        slotTable = new JTable(slotData, slotColumnNames);
        JScrollPane slotScrollPane = new JScrollPane(slotTable);
        slotScrollPane.setBorder(BorderFactory.createTitledBorder("Available Slots"));
        slotScrollPane.setPreferredSize(new Dimension(750, 200));

        // Book Appointment Button
        bookButton = new JButton("Book Appointment");
        bookButton.addActionListener(e -> bookAppointment());

        // Back Button
        backButton = new JButton("Back");
        backButton.addActionListener(e -> navigateBack());

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(bookButton);
        bottomPanel.add(backButton);

        JPanel slotPanel = new JPanel(new BorderLayout());
        slotPanel.add(slotScrollPane, BorderLayout.CENTER);
        slotPanel.add(bottomPanel, BorderLayout.SOUTH);

        panel.add(slotPanel, BorderLayout.SOUTH);

        add(panel);
    }

    // Get available slots count
    private int getAvailableSlotsCount() {
        List<TimeSlot> slots = TimeSlot.readTimeSlotsByLecturerId(lecturer.getUserId());
        int count = 0;
        for (TimeSlot slot : slots) {
            if (!slot.getStatus().equalsIgnoreCase("Booked")) {
                count++;
            }
        }
        return count;
    }

    // Get feedback data
    private String[][] getFeedbackData() {
        List<Feedback> feedbackList = Feedback.readFeedback();
        List<String[]> relevantFeedbacks = new ArrayList<>();

        for (Feedback feedback : feedbackList) {
            if (feedback.getAppointmentId() != null) {
                // Get the appointment to check if it's associated with the lecturer
                Appointment appointment = Appointment.getAppointmentById(feedback.getAppointmentId(), Appointment.FILE_PATH);
                if (appointment != null && appointment.getLecturerId().equals(lecturer.getUserId())) {
                    // Check if feedback is anonoymous
                    String userId = feedback.isAnonymous() ? "Anonymous" : feedback.getUserId();
                    String username = feedback.isAnonymous() ? "Anonymous" : feedback.getUsername();
                    
                    //only student feedback will be display, 0 is lecturer's feedback
                    if (feedback.getRating() > 0 && feedback.getRating() <=5) {
                        String[] data = {
                            userId,
                            username,
                            feedback.getComments(),
                            String.valueOf(feedback.getRating())
                        };
                        relevantFeedbacks.add(data);
                    }
                }
            }
        }

        String[][] dataArray = new String[relevantFeedbacks.size()][4];
        for (int i = 0; i < relevantFeedbacks.size(); i++) {
            dataArray[i] = relevantFeedbacks.get(i);
        }
        return dataArray;
    }

    // Get available slots data
    private String[][] getAvailableSlotsData() {
        List<TimeSlot> slots = TimeSlot.readTimeSlotsByLecturerId(lecturer.getUserId());
        List<String[]> availableSlots = new ArrayList<>();

        for (TimeSlot slot : slots) {
            if (!slot.getStatus().equalsIgnoreCase("Booked")) {
                String[] data = {
                    slot.getSlotId(),
                    slot.getDate(),
                    slot.getTime(),
                    slot.getLocation()
                };
                availableSlots.add(data);
            }
        }

        String[][] dataArray = new String[availableSlots.size()][5];
        for (int i = 0; i < availableSlots.size(); i++) {
            dataArray[i] = availableSlots.get(i);
        }
        return dataArray;
    }

    // Book an appointment
    private void bookAppointment() {
        int selectedRow = slotTable.getSelectedRow();
        if (selectedRow != -1) {
            String slotId = (String) slotTable.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to book this slot?", "Confirm Booking", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Update slot status to "Booked"
                TimeSlot slot = TimeSlot.getTimeSlotById(slotId);
                if (slot != null) {
                    slot.setStatus("Booked");
                    TimeSlot.updateTimeSlot(slot);

                    // Create a new appointment
                    String appointmentId = Appointment.generateAppointmentId();
                    Appointment newAppointment = new Appointment(
                            appointmentId,
                            student.getUserId(),
                            lecturer.getUserId(),
                            slot.getDate(),
                            slot.getTime(),
                            slot.getLocation(),
                            "Upcoming"
                    );
                    Appointment.addAppointment(newAppointment, Appointment.FILE_PATH);

                    JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshSlotTable();
                    refreshLecturerInfo();
                } else {
                    JOptionPane.showMessageDialog(this, "Slot not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a slot to book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Refresh slot table after booking
    private void refreshSlotTable() {
        String[] slotColumnNames = {"Slot ID", "Date", "Start Time", "End Time", "Location"};
        String[][] slotData = getAvailableSlotsData();
        slotTable.setModel(new DefaultTableModel(slotData, slotColumnNames));
    }

    // Refresh lecturer info after booking
    private void refreshLecturerInfo() {
        // Update lecturer info panel
        // and refresh entire gui
        getContentPane().removeAll();
        initializeComponents();
        revalidate();
        repaint();
    }

    // Back
    private void navigateBack() {
        new StudentDashboardGUI(student, userService).setVisible(true);
        dispose();
    }
}




