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

public class PastAppointmentGUI extends JFrame {
    private final User user;
    private JTable appointmentTable;
    private JButton backButton;
    private final UserService userService; // Instantiate UserService

    public PastAppointmentGUI(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
        setTitle("Past Appointments");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        // Main panel
        JPanel panel = new JPanel(new BorderLayout());

        // Column names and data
        String[] columnNames;
        String[][] data = getAppointmentData();

        // Adjust column names based on user role
        if (user instanceof Student) {
            columnNames = new String[]{"Appointment ID", "Lecturer Name", "Date", "Time", "Location", "Status"};
        } else if (user instanceof Lecturer) {
            columnNames = new String[]{"Appointment ID", "Student Name", "Date", "Time", "Location", "Status"};
        } else {
            // Default column names if user type is unknown
            columnNames = new String[]{"Appointment ID", "User Name", "Date", "Time", "Location", "Status"};
        }

        // Appointment Table
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            // Make cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(appointmentTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Back Button
        backButton = new JButton("Back");
        backButton.addActionListener(e -> navigateBack());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private String[][] getAppointmentData() {
        List<Appointment> appointments = Appointment.readAppointments(Appointment.FILE_PATH); 
        List<String[]> relevantAppointments = new ArrayList<>();

        for (Appointment appointment : appointments) {
            // Check if appointment is 'Completed' or 'Cancelled'
            if (appointment.getStatus().equalsIgnoreCase("Completed") || appointment.getStatus().equalsIgnoreCase("Cancelled")) {
                // Check if the appointment is associated with the logged-in user
                if (isAppointmentAssociatedWithUser(appointment)) {
                    String[] data = new String[6];
                    data[0] = appointment.getAppointmentId();

                    if (user instanceof Student) {
                        // Retrieve Lecturer Name
                        Lecturer lecturer = userService.getLecturerById(appointment.getLecturerId());
                        data[1] = lecturer != null ? lecturer.getName() : "Unknown";
                    } else if (user instanceof Lecturer) {
                        // Retrieve Student Name
                        Student student = userService.getStudentById(appointment.getStudentId());
                        data[1] = student != null ? student.getName() : "Unknown";
                    } else {
                        // Default to User ID if role is unknown
                        data[1] = "Unknown";
                    }

                    data[2] = appointment.getDate();
                    data[3] = appointment.getTime();
                    data[4] = appointment.getLocation();
                    data[5] = appointment.getStatus();
                    relevantAppointments.add(data);
                }
            }
        }

        // Convert list to array
        String[][] dataArray = new String[relevantAppointments.size()][6];
        for (int i = 0; i < relevantAppointments.size(); i++) {
            dataArray[i] = relevantAppointments.get(i);
        }
        return dataArray;
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

    // Back
    private void navigateBack() {
        if (user instanceof Student) {
            new StudentDashboardGUI((Student) user, userService).setVisible(true);
        } else if (user instanceof Lecturer) {
            new LecturerDashboardGUI((Lecturer) user, userService).setVisible(true);
        }
        dispose();
    }
}


