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
import java.util.ArrayList;

public class LecturerListGUI extends JFrame {
    private final Student student;
    private JTextField searchField;
    private JButton searchButton;
    private JButton backButton;
    private JPanel lecturerListPanel;
    private final FileHandling fileHandling;
    private final UserService userService;

    // Constructor
    public LecturerListGUI(Student student, UserService userService) {
        this.student = student;
        this.userService = userService;
        this.fileHandling = new FileHandling();
        setTitle("Lecturer List - Book an Appointment");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        // Creating main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Aligning center
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search Lecturer by ID: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Background image
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/apu/psychology/consultation/management/system/pic/background.jpg")));
        background.setLayout(new GridBagLayout());
        add(background);

        // Lecturer List Panel
        lecturerListPanel = new JPanel();
        lecturerListPanel.setLayout(new GridBagLayout()); // Using GridBagLayout to ensure consistent box size and alignment
        JScrollPane listScrollPane = new JScrollPane(lecturerListPanel);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);

        // Back Button
        backButton = new JButton("Back");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Aligning center
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Adding action listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchLecturer();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudentDashboardGUI(student, userService).setVisible(true);
                dispose();
            }
        });

        add(mainPanel);
        displayLecturers(parseLecturers(fileHandling.readLecturers()));
    }
    

    // parse lecturer data (debug easier)
    private List<Lecturer> parseLecturers(List<String> lecturerData) {
       List<Lecturer> lecturers = new ArrayList<>();
        for (String data : lecturerData) {
            String[] details = data.split(",");
            if (details.length == 4) { 
                String userId = details[0].trim();
                String name = details[1].trim();
                String email = details[2].trim();
                String password = details[3].trim();
                Lecturer lecturer = new Lecturer(userId, name, email, password);
                System.out.println("Parsed Lecturer: " + lecturer); // Ensure Lecturer has a meaningful toString()
                lecturers.add(lecturer);
            } else {
                System.out.println("Invalid lecturer data format: " + data);
            }
        }
        return lecturers;
    }

    // Display lecturers in a grid layout
    private void displayLecturers(List<Lecturer> lecturerList) {
        lecturerListPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        int columnCount = 2; // Ensure two columns per row

        for (int i = 0; i < lecturerList.size(); i++) {
            Lecturer lecturer = lecturerList.get(i);
            JPanel lecturerPanel = new JPanel();
            lecturerPanel.setLayout(new BoxLayout(lecturerPanel, BoxLayout.Y_AXIS));
            lecturerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 2),
                    BorderFactory.createEmptyBorder(15, 25, 15, 25)
            ));
            lecturerPanel.setPreferredSize(new Dimension(300, 200)); // Set consistent size for all lecturer boxes

            JLabel lecturerIdLabel = new JLabel("Lecturer ID: " + lecturer.getUserId());
            JLabel nameLabel = new JLabel("Name: " + lecturer.getName());
            JLabel availableSlotsLabel = new JLabel("Available Slots: " + getAvailableSlotsCount(lecturer.getUserId()));
            JButton viewButton = new JButton("View Profile");

            viewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Selected Lecturer ID: " + lecturer.getUserId());
                    System.out.println("Selected Lecturer Name: " + lecturer.getName());
                    new LecturerProfileGUI(student, lecturer, userService).setVisible(true);
                    dispose();
                }
            });

            lecturerPanel.add(Box.createVerticalStrut(10)); // Adding spacing between labels
            lecturerPanel.add(lecturerIdLabel);
            lecturerPanel.add(Box.createVerticalStrut(10));
            lecturerPanel.add(nameLabel);
            lecturerPanel.add(Box.createVerticalStrut(10));
            lecturerPanel.add(availableSlotsLabel);
            lecturerPanel.add(Box.createVerticalStrut(25));
            lecturerPanel.add(viewButton);

            lecturerListPanel.add(lecturerPanel, gbc);
            if ((i + 1) % columnCount == 0) {
                gbc.gridx = 0;
                gbc.gridy++;
            } else {
                gbc.gridx++;
            }
        }

        // Adding an empty panel as filler to ensure alignment starts from top-left
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weighty = 1;
        JPanel fillerPanel = new JPanel();
        lecturerListPanel.add(fillerPanel, gbc);

        lecturerListPanel.revalidate();
        lecturerListPanel.repaint();
    }

    // Search for a lecturer by ID
    private void searchLecturer() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            List<Lecturer> allLecturers = parseLecturers(fileHandling.readLecturers());
            List<Lecturer> filteredLecturers = allLecturers.stream()
                    .filter(lecturer -> lecturer.getUserId().equalsIgnoreCase(searchText))
                    .toList();
            displayLecturers(filteredLecturers);
        } else {
            displayLecturers(parseLecturers(fileHandling.readLecturers()));
        }
    }

    // Get available slots count for a lecturer
    private int getAvailableSlotsCount(String lecturerId) {
        List<TimeSlot> timeSlots = TimeSlot.readTimeSlots();
        return (int) timeSlots.stream()
                .filter(slot -> slot.getLecturerId().equals(lecturerId) && slot.getStatus().equalsIgnoreCase("Available"))
                .count();
    }
}


















