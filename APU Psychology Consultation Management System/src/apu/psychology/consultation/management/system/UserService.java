/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apu.psychology.consultation.management.system;

/**
 *
 * @author behbe
 */
import java.util.List;
import java.util.ArrayList;

public class UserService {
    private final FileHandling fileHandling;

    // Constructor
    public UserService(FileHandling fileHandling) {
        this.fileHandling = fileHandling;
    }

    // Method to register a student
    public boolean registerStudent(String userId, String name, String email, String password) {
        // Check if email is already registered
        if (isEmailRegistered(email)) {
            System.out.println("Email already registered.");
            return false;
        }
        
        // Prepare student data and write to file
        String studentData = userId + "," + name + "," + email + "," + password;
        fileHandling.writeStudent(studentData);
        return true;
    }

    // Method to register a lecturer
    public boolean registerLecturer(String userId, String name, String email, String password) {
        // Check if email is already registered
        if (isEmailRegistered(email)) {
            System.out.println("Email already registered.");
            return false;
        }
        
        // Prepare lecturer data and write to file
        String lecturerData = userId + "," + name + "," + email + "," + password;
        fileHandling.writeLecturer(lecturerData);
        return true;
    }

    // Method to validate login
    public User login(String email, String password) {
        // Check student file for matching credentials
        List<String> students = fileHandling.readStudents();
        for (String student : students) {
            String[] details = student.split(",");
            if (details.length >= 4 && details[2].equals(email) && details[3].equals(password)) {
                return new Student(details[0], details[1], details[2], details[3]);
            }
        }
        // Check lecturer file for matching credentials
        List<String> lecturers = fileHandling.readLecturers();
        for (String lecturer : lecturers) {
            String[] details = lecturer.split(",");
            if (details.length >= 4 && details[2].equals(email) && details[3].equals(password)) {
                return new Lecturer(details[0], details[1], details[2], details[3]);
            }
        }
        // If no match is found
        System.out.println("Invalid email or password.");
        return null;
    }

    // Check if an email is already registered
    private boolean isEmailRegistered(String email) {
        // Check student file for email
        List<String> students = fileHandling.readStudents();
        for (String student : students) {
            String[] details = student.split(",");
            if (details.length >= 3 && details[2].equals(email)) {
                return true;
            }
        }
        
        // Check lecturer file for email
        List<String> lecturers = fileHandling.readLecturers();
        for (String lecturer : lecturers) {
            String[] details = lecturer.split(",");
            if (details.length >= 3 && details[2].equals(email)) {
                return true;
            }
        }
        
        return false;
    }

    // Retrieve a Student by ID
    public Student getStudentById(String studentId) {
        List<String> students = fileHandling.readStudents();
        for (String student : students) {
            String[] details = student.split(",");
            if (details.length >= 1 && details[0].equals(studentId)) {
                if (details.length >= 4) {
                    return new Student(details[0], details[1], details[2], details[3]);
                } else {
                    System.out.println("Incomplete student data for ID: " + studentId);
                }
            }
        }
        return null;
    }

    // Retrieve a Lecturer by ID
    public Lecturer getLecturerById(String lecturerId) {
        List<String> lecturers = fileHandling.readLecturers();
        for (String lecturer : lecturers) {
            String[] details = lecturer.split(",");
            if (details.length >= 1 && details[0].equals(lecturerId)) {
                if (details.length >= 4) {
                    return new Lecturer(details[0], details[1], details[2], details[3]);
                } else {
                    System.out.println("Incomplete lecturer data for ID: " + lecturerId);
                }
            }
        }
        return null;
    }
    
    // Get available slot for a specific lecturer
    public List<TimeSlot> getAvailableSlots(String lecturerId) {
        List<TimeSlot> allSlots = TimeSlot.readTimeSlots();
        List<TimeSlot> availableSlots = new ArrayList<>();
        for (TimeSlot slot : allSlots) {
            if (slot.getLecturerId().equals(lecturerId) && slot.getStatus().equalsIgnoreCase("Available")) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    void updateUser(Student student) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}



