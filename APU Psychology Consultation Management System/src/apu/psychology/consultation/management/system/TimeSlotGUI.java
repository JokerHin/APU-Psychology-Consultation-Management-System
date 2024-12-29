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


public class TimeSlotGUI extends JFrame {
    private Lecturer lecturer;
    private UserService userService;
    private JTable slotTable;
    private JButton addSlotButton;
    private JButton editSlotButton;
    private JButton removeSlotButton;
    private JButton backButton;

    // Constructor
    public TimeSlotGUI(Lecturer lecturer, UserService userService) {
        this.lecturer = lecturer;
        this.userService = userService;
        setTitle("Manage Time Slots");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        // Creating panel to hold components
        JPanel panel = new JPanel(new BorderLayout());

        // Slot Data - Load from TimeSlot class
        String[] columnNames = {"Slot ID", "Date", "Time", "Location", "Status"};
        String[][] data = getSlotData();

        // Slot Table
        slotTable = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(slotTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addSlotButton = new JButton("Add Slot");
        editSlotButton = new JButton("Edit Slot");
        removeSlotButton = new JButton("Remove Slot");
        backButton = new JButton("Back");
        buttonPanel.add(addSlotButton);
        buttonPanel.add(editSlotButton);
        buttonPanel.add(removeSlotButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Adding action listeners
        addSlotButton.addActionListener(e -> addSlot());
        editSlotButton.addActionListener(e -> editSlot());
        removeSlotButton.addActionListener(e -> removeSlot());
        backButton.addActionListener(e -> navigateBack());

        add(panel);
    }

    // Get slot data from TimeSlot class
    private String[][] getSlotData() {
        List<TimeSlot> timeSlots = TimeSlot.readTimeSlotsByLecturerId(lecturer.getUserId());
        String[][] data = new String[timeSlots.size()][5];
        for (int i = 0; i < timeSlots.size(); i++) {
            TimeSlot slot = timeSlots.get(i);
            data[i][0] = slot.getSlotId();
            data[i][1] = slot.getDate();
            data[i][2] = slot.getTime();
            data[i][3] = slot.getLocation();
            data[i][4] = slot.getStatus();
        }
        return data;
    }

    // Add a slot
    private void addSlot() {
        // Generate a unique slot ID
        String slotId = TimeSlot.generateSlotId();

        // Date Input using JComboBox
        JPanel datePanel = new JPanel();
        JComboBox<String> dayComboBox = new JComboBox<>(getDays());
        JComboBox<String> monthComboBox = new JComboBox<>(getMonths());
        JComboBox<String> yearComboBox = new JComboBox<>(getYears());
        datePanel.add(dayComboBox);
        datePanel.add(monthComboBox);
        datePanel.add(yearComboBox);

        // Time Input using JComboBox
        JPanel timePanel = new JPanel();
        JComboBox<String> hourComboBox = new JComboBox<>(getHours());
        JComboBox<String> minuteComboBox = new JComboBox<>(getMinutes());
        timePanel.add(hourComboBox);
        timePanel.add(minuteComboBox);

        // Location Input
        JComboBox<String> locationComboBox = new JComboBox<>(new String[]{"Any", "Online", "Campus"});

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Date (DD/MM/YYYY):"));
        formPanel.add(datePanel);
        formPanel.add(new JLabel("Time (HH:MM):"));
        formPanel.add(timePanel);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationComboBox);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Add New Slot", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String date = dayComboBox.getSelectedItem() + "/" + monthComboBox.getSelectedItem() + "/" + yearComboBox.getSelectedItem();
            String time = hourComboBox.getSelectedItem() + ":" + minuteComboBox.getSelectedItem();
            String location = (String) locationComboBox.getSelectedItem();
            String status = "Available";

            // Validate input fields
            if (date.isEmpty() || time.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TimeSlot newSlot = new TimeSlot(slotId, lecturer.getUserId(), date, time, location, status);
            TimeSlot.addTimeSlot(newSlot);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Slot added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Edit a slot
    private void editSlot() {
        int selectedRow = slotTable.getSelectedRow();
        if (selectedRow != -1) {
            String slotId = (String) slotTable.getValueAt(selectedRow, 0);
            String slotLecturerId = lecturer.getUserId();

            // Check if the slot belongs to the lecturer
            if (slotLecturerId.equals(lecturer.getUserId())) {
                TimeSlot slot = TimeSlot.getTimeSlotById(slotId);
                if (slot != null) {
                    // Date Input using JComboBox
                    JPanel datePanel = new JPanel();
                    JComboBox<String> dayComboBox = new JComboBox<>(getDays());
                    JComboBox<String> monthComboBox = new JComboBox<>(getMonths());
                    JComboBox<String> yearComboBox = new JComboBox<>(getYears());
                    String[] dateParts = slot.getDate().split("/");
                    dayComboBox.setSelectedItem(dateParts[0]);
                    monthComboBox.setSelectedItem(dateParts[1]);
                    yearComboBox.setSelectedItem(dateParts[2]);
                    datePanel.add(dayComboBox);
                    datePanel.add(monthComboBox);
                    datePanel.add(yearComboBox);

                    // Time Input using JComboBox
                    JPanel timePanel = new JPanel();
                    JComboBox<String> hourComboBox = new JComboBox<>(getHours());
                    JComboBox<String> minuteComboBox = new JComboBox<>(getMinutes());
                    String[] timeParts = slot.getTime().split(":");
                    hourComboBox.setSelectedItem(timeParts[0]);
                    minuteComboBox.setSelectedItem(timeParts[1]);
                    timePanel.add(hourComboBox);
                    timePanel.add(minuteComboBox);

                    // Location Input
                    JComboBox<String> locationComboBox = new JComboBox<>(new String[]{"Any", "Online", "Campus"});
                    locationComboBox.setSelectedItem(slot.getLocation());

                    // Form Panel
                    JPanel formPanel = new JPanel(new GridLayout(3, 2));
                    formPanel.add(new JLabel("Date (DD/MM/YYYY):"));
                    formPanel.add(datePanel);
                    formPanel.add(new JLabel("Time (HH:MM):"));
                    formPanel.add(timePanel);
                    formPanel.add(new JLabel("Location:"));
                    formPanel.add(locationComboBox);

                    int result = JOptionPane.showConfirmDialog(this, formPanel, "Edit Slot " + slotId, JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String date = dayComboBox.getSelectedItem() + "/" + monthComboBox.getSelectedItem() + "/" + yearComboBox.getSelectedItem();
                        String time = hourComboBox.getSelectedItem() + ":" + minuteComboBox.getSelectedItem();
                        String location = (String) locationComboBox.getSelectedItem();

                        // Validate input fields
                        if (date.isEmpty() || time.isEmpty() || location.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Update the slot
                        slot.setDate(date);
                        slot.setTime(time);
                        slot.setLocation(location);
                        TimeSlot.updateTimeSlot(slot);
                        refreshTable();
                        JOptionPane.showMessageDialog(this, "Slot updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Slot not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                refreshTable();
                JOptionPane.showMessageDialog(this, "You can only edit your own slots.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            refreshTable();
            JOptionPane.showMessageDialog(this, "Please select a slot to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Remove a slot
    private void removeSlot() {
        int selectedRow = slotTable.getSelectedRow();
        if (selectedRow != -1) {
            String slotId = (String) slotTable.getValueAt(selectedRow, 0);
            String slotLecturerId = lecturer.getUserId();

            // Check if the slot belongs to the lecturer
            if (slotLecturerId.equals(lecturer.getUserId())) {
                int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove Slot ID " + slotId + "?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    TimeSlot.removeTimeSlot(slotId);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Slot removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                refreshTable();
                JOptionPane.showMessageDialog(this, "You can only remove your own slots.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            refreshTable();
            JOptionPane.showMessageDialog(this, "Please select a slot to remove.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Refresh
    private void refreshTable() {
        String[][] data = getSlotData();
        String[] columnNames = {"Slot ID", "Date", "Time", "Location", "Status"};
        slotTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    // back
    private void navigateBack() {
        new LecturerDashboardGUI(lecturer, userService).setVisible(true);
        dispose();
    }

    // Get options for ComboBoxes
    private String[] getDays() {
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.format("%02d", i);
        }
        return days;
    }

    private String[] getMonths() {
        String[] months = new String[12];
        for (int i = 1; i <= 12; i++) {
            months[i - 1] = String.format("%02d", i);
        }
        return months;
    }

    private String[] getYears() {
        String[] years = new String[5];
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int i = 0; i < 5; i++) {
            years[i] = Integer.toString(currentYear + i);
        }
        return years;
    }

    private String[] getHours() {
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        return hours;
    }

    private String[] getMinutes() {
        return new String[]{"00", "15", "30", "45"};
    }
}




