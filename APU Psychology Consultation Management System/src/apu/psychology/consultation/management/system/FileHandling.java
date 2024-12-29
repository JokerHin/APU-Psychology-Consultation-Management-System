/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apu.psychology.consultation.management.system;

/**
 *
 * @author behbe
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandling {
    private static final String STUDENT_FILE = "student.txt";
    private static final String LECTURER_FILE = "lecturer.txt";

    // Constructor to initialize files
    public FileHandling() {
        initializeFiles();
    }

    // Initialize text files if they don't exist
    private void initializeFiles() {
        try {
            File studentFile = new File(STUDENT_FILE);
            if (!studentFile.exists()) {
                studentFile.createNewFile();
            }

            File lecturerFile = new File(LECTURER_FILE);
            if (!lecturerFile.exists()) {
                lecturerFile.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error initializing files: " + e.getMessage());
        }
    }

    // Read all student data from the text file
    public List<String> readStudents() {
        List<String> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                students.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading student file: " + e.getMessage());
        }
        return students;
    }

    // Read all lecturer data from the text file
    public List<String> readLecturers() {
        List<String> lecturers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LECTURER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                lecturers.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading lecturer file: " + e.getMessage());
        }
        return lecturers;
    }

    // Write a new student to the text file
    public void writeStudent(String studentData) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENT_FILE, true))) {
            bw.write(studentData);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to student file: " + e.getMessage());
        }
    }

    // Write a new lecturer to the text file
    public void writeLecturer(String lecturerData) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LECTURER_FILE, true))) {
            bw.write(lecturerData);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to lecturer file: " + e.getMessage());
        }
    }

    // Update student data in the text file
    public void updateStudent(List<String> updatedStudents) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENT_FILE))) {
            for (String student : updatedStudents) {
                bw.write(student);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating student file: " + e.getMessage());
        }
    }

    // Update lecturer data in the text file
    public void updateLecturer(List<String> updatedLecturers) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LECTURER_FILE))) {
            for (String lecturer : updatedLecturers) {
                bw.write(lecturer);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating lecturer file: " + e.getMessage());
        }
    }

    // Get the maximum user ID based on role prefix
    public int getMaxUserId(String rolePrefix) {
        int maxId = 0;
        List<String> users = rolePrefix.equals("S") ? readStudents() : readLecturers();

        for (String userData : users) {
            String[] details = userData.split(",");
            if (details.length > 0) {
                String userId = details[0]; // Assuming userId is the first field
                if (userId.startsWith(rolePrefix)) {
                    try {
                        int id = Integer.parseInt(userId.substring(1));
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid IDs
                    }
                }
            }
        }
        return maxId;
    }
}

