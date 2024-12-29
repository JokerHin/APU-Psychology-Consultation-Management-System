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

public class UpcomingAppointmentGUI extends JFrame {
    private final User user;
    private final UserService userService; // Added UserService field
    private JTable appointmentTable;
    private JButton backButton;
    private JButton cancelAppointmentButton;
    private JButton rescheduleButton;
    private JButton completeAppointmentButton;
    private JButton manageRescheduleButton;

    // Constructor
    public UpcomingAppointmentGUI(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
        setTitle("Upcoming Appointments");
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
            columnNames = new String[]{"Appointment ID", "Lecturer ID", "Date", "Time", "Location", "Status"};
        } else if (user instanceof Lecturer) {
            columnNames = new String[]{"Appointment ID", "Student ID", "Date", "Time", "Location", "Status"};
        } else {
            columnNames = new String[]{"Appointment ID", "User ID", "Date", "Time", "Location", "Status"};
        }

        // Appointment Table with cannot edit de cells
        appointmentTable = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        JScrollPane tableScrollPane = new JScrollPane(appointmentTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        if (user instanceof Student) {
            cancelAppointmentButton = new JButton("Cancel Appointment");
            rescheduleButton = new JButton("Reschedule Appointment");
            buttonPanel.add(cancelAppointmentButton);
            buttonPanel.add(rescheduleButton);

            cancelAppointmentButton.addActionListener(e -> cancelAppointment());
            rescheduleButton.addActionListener(e -> rescheduleAppointment());
        } else if (user instanceof Lecturer) {
            completeAppointmentButton = new JButton("Complete Appointment");
            manageRescheduleButton = new JButton("Manage Reschedule Requests");
            buttonPanel.add(completeAppointmentButton);
            buttonPanel.add(manageRescheduleButton);

            completeAppointmentButton.addActionListener(e -> completeAppointment());
            manageRescheduleButton.addActionListener(e -> manageRescheduleRequests());
        }

        backButton = new JButton("Back");
        backButton.addActionListener(e -> navigateBack());
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);
    }

    // Get appointment data for the logged-in user
    private String[][] getAppointmentData() {
        List<Appointment> appointments = Appointment.readAppointments(Appointment.FILE_PATH);
        List<String[]> relevantAppointments = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (appointment.getStatus().equalsIgnoreCase("Upcoming") || appointment.getStatus().equalsIgnoreCase("Pending")) {
                if (user instanceof Student && appointment.getStudentId().equals(user.getUserId())) {
                    String[] data = {
                        appointment.getAppointmentId(),
                        appointment.getLecturerId(),
                        appointment.getDate(),
                        appointment.getTime(),
                        appointment.getLocation(),
                        appointment.getStatus()
                    };
                    relevantAppointments.add(data);
                } else if (user instanceof Lecturer && appointment.getLecturerId().equals(user.getUserId())) {
                    String[] data = {
                        appointment.getAppointmentId(),
                        appointment.getStudentId(),
                        appointment.getDate(),
                        appointment.getTime(),
                        appointment.getLocation(),
                        appointment.getStatus()
                    };
                    relevantAppointments.add(data);
                }
            }
        }

        String[][] dataArray = new String[relevantAppointments.size()][6];
        for (int i = 0; i < relevantAppointments.size(); i++) {
            dataArray[i] = relevantAppointments.get(i);
        }
        return dataArray;
    }

    // Cancel appointment (set status to cancelled)
    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow != -1) {
            String appointmentId = (String) appointmentTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this appointment?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Appointment appointment = Appointment.getAppointmentById(appointmentId, Appointment.FILE_PATH);
                if (appointment != null && appointment.getStudentId().equals(user.getUserId())) {
                    appointment.setStatus("Cancelled");
                    Appointment.updateAppointment(appointment, Appointment.FILE_PATH);

                    // Update slot status to "Available"
                    String slotId = appointment.getSlotId();
                    if (slotId != null && !slotId.isEmpty()) {
                        TimeSlot slot = TimeSlot.getTimeSlotById(slotId);
                        if (slot != null) {
                            slot.setStatus("Available");
                            TimeSlot.updateTimeSlot(slot);
                        } else {
                            JOptionPane.showMessageDialog(this, "Associated time slot not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "You can only cancel your own appointments.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reschedule appoinment
    private void rescheduleAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow != -1) {
            String appointmentId = (String) appointmentTable.getValueAt(selectedRow, 0);
            Appointment appointment = Appointment.getAppointmentById(appointmentId, Appointment.FILE_PATH);
            if (appointment != null && appointment.getStudentId().equals(user.getUserId())) {
                // Fetch available slots for rescheduling
                List<TimeSlot> availableSlots = userService.getAvailableSlots(appointment.getLecturerId());

                if (availableSlots.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No available slots for rescheduling at the moment.", "Information", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Create a list of available slots in "Lecturer's Date Time - SlotID" format
                String[] slotOptions = new String[availableSlots.size()];
                for (int i = 0; i < availableSlots.size(); i++) {
                    TimeSlot slot = availableSlots.get(i);
                    slotOptions[i] = slot.getDate() + " " + slot.getTime() + " (Slot ID: " + slot.getSlotId() + ")";
                }

                // Display available slots in a dropdown
                String selectedSlot = (String) JOptionPane.showInputDialog(
                        this,
                        "Select an available slot for rescheduling:",
                        "Reschedule Appointment",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        slotOptions,
                        slotOptions[0]
                );

                if (selectedSlot != null) {
                    // Extract slotId from the selected slot string
                    String slotId = selectedSlot.substring(selectedSlot.indexOf("Slot ID: ") + 9, selectedSlot.length() - 1);
                    TimeSlot newSlot = TimeSlot.getTimeSlotById(slotId);

                    if (newSlot != null && newSlot.getStatus().equalsIgnoreCase("Available")) {
                        // Update old slot status to "Available"
                        String oldSlotId = appointment.getSlotId();
                        if (oldSlotId != null && !oldSlotId.isEmpty()) {
                            TimeSlot oldSlot = TimeSlot.getTimeSlotById(oldSlotId);
                            if (oldSlot != null) {
                                oldSlot.setStatus("Available");
                                TimeSlot.updateTimeSlot(oldSlot);
                            }
                        }

                        // Update appointment details
                        appointment.setOldSlotId(appointment.getSlotId());
                        appointment.setOldDate(appointment.getDate());
                        appointment.setOldTime(appointment.getTime());

                        appointment.setSlotId(newSlot.getSlotId());
                        appointment.setDate(newSlot.getDate());
                        appointment.setTime(newSlot.getTime());
                        appointment.setStatus("Pending"); // Set status to Pending
                        // Update new slot status to "Booked"
                        newSlot.setStatus("Booked");
                        TimeSlot.updateTimeSlot(newSlot);

                        // Update the appointment in the file
                        Appointment.updateAppointment(appointment, Appointment.FILE_PATH);

                        refreshTable();
                        JOptionPane.showMessageDialog(this, "Appointment rescheduled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Selected slot is no longer available.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "You can only reschedule your own appointments.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to reschedule.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Lecturers to complete an appointment
    private void completeAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow != -1) {
            String appointmentId = (String) appointmentTable.getValueAt(selectedRow, 0);
            Appointment appointment = Appointment.getAppointmentById(appointmentId, Appointment.FILE_PATH);
            if (appointment != null && appointment.getLecturerId().equals(user.getUserId())) {
                int confirm = JOptionPane.showConfirmDialog(this, "Have you completed this appointment?", "Confirm Completion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    appointment.setStatus("Completed");
                    Appointment.updateAppointment(appointment, Appointment.FILE_PATH);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Appointment marked as completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "You can only complete your own appointments.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to complete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Manage reschedule requests
    private void manageRescheduleRequests() {
        List<Appointment> appointments = Appointment.readAppointments(Appointment.FILE_PATH);
        List<Appointment> pendingAppointments = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (appointment.getStatus().equalsIgnoreCase("Pending") && appointment.getLecturerId().equals(user.getUserId())) {
                pendingAppointments.add(appointment);
            }
        }

        if (pendingAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No reschedule requests at the moment.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a dialog to display pending appointments
        JDialog dialog = new JDialog(this, "Reschedule Requests", true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"Appointment ID", "Student ID", "Old Date", "Old Time", "New Date", "New Time"};
        String[][] data = new String[pendingAppointments.size()][6];

        for (int i = 0; i < pendingAppointments.size(); i++) {
            Appointment appointment = pendingAppointments.get(i);
            data[i][0] = appointment.getAppointmentId();
            data[i][1] = appointment.getStudentId();
            data[i][2] = appointment.getOldDate();
            data[i][3] = appointment.getOldTime();
            data[i][4] = appointment.getDate();
            data[i][5] = appointment.getTime();
        }

        JTable pendingTable = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        JScrollPane scrollPane = new JScrollPane(pendingTable);
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");

        approveButton.addActionListener(e -> {
            int selectedRow = pendingTable.getSelectedRow();
            if (selectedRow != -1) {
                String appointmentId = (String) pendingTable.getValueAt(selectedRow, 0);
                approveReschedule(appointmentId);
                ((DefaultTableModel) pendingTable.getModel()).removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select an appointment to approve.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        rejectButton.addActionListener(e -> {
            int selectedRow = pendingTable.getSelectedRow();
            if (selectedRow != -1) {
                String appointmentId = (String) pendingTable.getValueAt(selectedRow, 0);
                rejectReschedule(appointmentId);
                ((DefaultTableModel) pendingTable.getModel()).removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select an appointment to reject.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel actionPanel = new JPanel();
        actionPanel.add(approveButton);
        actionPanel.add(rejectButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(actionPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);

        refreshTable();
    }

    // Approve a reschedule request
    private void approveReschedule(String appointmentId) {
        Appointment appointment = Appointment.getAppointmentById(appointmentId, Appointment.FILE_PATH);
        if (appointment != null) {
            // Update old slot status to "Available"
            String oldSlotId = appointment.getOldSlotId();
            if (oldSlotId != null && !oldSlotId.isEmpty()) {
                TimeSlot oldSlot = TimeSlot.getTimeSlotById(oldSlotId);
                if (oldSlot != null) {
                    oldSlot.setStatus("Available");
                    TimeSlot.updateTimeSlot(oldSlot);
                } else {
                    JOptionPane.showMessageDialog(this, "Old time slot not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Update new slot status to "Booked"
            String newSlotId = appointment.getSlotId();
            if (newSlotId != null && !newSlotId.isEmpty()) {
                TimeSlot newSlot = TimeSlot.getTimeSlotById(newSlotId);
                if (newSlot != null) {
                    newSlot.setStatus("Booked");
                    TimeSlot.updateTimeSlot(newSlot);
                } else {
                    JOptionPane.showMessageDialog(this, "New time slot not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Update appointment status and details
            appointment.setStatus("Upcoming");
            Appointment.updateAppointment(appointment, Appointment.FILE_PATH);

            JOptionPane.showMessageDialog(this, "Reschedule approved for Appointment ID " + appointmentId + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Reject a reschedule request
    private void rejectReschedule(String appointmentId) {
        Appointment appointment = Appointment.getAppointmentById(appointmentId, Appointment.FILE_PATH);
        if (appointment != null) {
            // Update old slot status to "Available"
            String oldSlotId = appointment.getOldSlotId();
            if (oldSlotId != null && !oldSlotId.isEmpty()) {
                TimeSlot oldSlot = TimeSlot.getTimeSlotById(oldSlotId);
                if (oldSlot != null) {
                    oldSlot.setStatus("Available");
                    TimeSlot.updateTimeSlot(oldSlot);
                } else {
                    JOptionPane.showMessageDialog(this, "Old time slot not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Update appointment status to "Cancelled"
            appointment.setStatus("Cancelled");
            Appointment.updateAppointment(appointment, Appointment.FILE_PATH);

            JOptionPane.showMessageDialog(this, "Reschedule rejected for Appointment ID " + appointmentId + ". Appointment has been cancelled.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Navigate
    private void navigateBack() {
        if (user instanceof Student) {
            new StudentDashboardGUI((Student) user, userService).setVisible(true);
        } else if (user instanceof Lecturer) {
            new LecturerDashboardGUI((Lecturer) user, userService).setVisible(true);
        }
        dispose();
    }

    // Refresh
    private void refreshTable() {
        String[][] data = getAppointmentData();
        String[] columnNames;
        if (user instanceof Student) {
            columnNames = new String[]{"Appointment ID", "Lecturer ID", "Date", "Time", "Location", "Status"};
        } else if (user instanceof Lecturer) {
            columnNames = new String[]{"Appointment ID", "Student ID", "Date", "Time", "Location", "Status"};
        } else {
            columnNames = new String[]{"Appointment ID", "User ID", "Date", "Time", "Location", "Status"};
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            // Make cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable.setModel(model);
    }
}





