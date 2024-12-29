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
import java.util.concurrent.atomic.AtomicInteger;

public class Feedback {
    private static final String FILE_PATH = "feedback.txt";
    private static AtomicInteger feedbackIdCounter = new AtomicInteger(getMaxFeedbackId() + 1);

    private String feedbackId;
    private String appointmentId;
    private String userId;
    private String username;
    private String roleId;
    private int rating;
    private String comments;
    private boolean isAnonymous;

    // Constructor
    public Feedback(String feedbackId, String appointmentId, String userId, String username, String roleId, int rating, String comments, boolean isAnonymous) {
        this.feedbackId = feedbackId;
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.username = username;
        this.roleId = roleId;
        this.rating = rating;
        this.comments = comments;
        this.isAnonymous = isAnonymous;
    }

    // Getters and Setters
    public String getFeedbackId() {
        return feedbackId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRoleId() {
        return roleId;
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    // Read all feedback from the text file
    public static List<Feedback> readFeedback() {
        List<Feedback> feedbackList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",", 8); // Limit to 8 fields to handle comments with commas
                if (details.length == 8) {
                    try {
                        Feedback feedback = new Feedback(
                                details[0],
                                details[1],
                                details[2],
                                details[3],
                                details[4],
                                Integer.parseInt(details[5]),
                                details[6],
                                Boolean.parseBoolean(details[7])
                        );
                        feedbackList.add(feedback);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format in feedback entry: " + line);
                    }
                } else {
                    System.out.println("Invalid feedback entry: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading feedback file: " + e.getMessage());
        }
        return feedbackList;
    }

    // Write all feedback to the text file
    public static void writeFeedback(List<Feedback> feedbackList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Feedback feedback : feedbackList) {
                bw.write(feedback.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to feedback file: " + e.getMessage());
        }
    }

    // Convert feedback details to CSV format
    public String toCSV() {
        return String.join(",",
                feedbackId,
                appointmentId,
                userId,
                username,
                roleId,
                String.valueOf(rating),
                comments.replace(",", "\\,"),
                String.valueOf(isAnonymous)
        );
    }

    // Add a new feedback
    public static void addFeedback(Feedback newFeedback) {
        List<Feedback> feedbackList = readFeedback();
        feedbackList.add(newFeedback);
        writeFeedback(feedbackList);
    }

    // Generate a unique feedback ID using AtomicInteger
    public static String generateFeedbackId() {
        return String.format("FB%03d", feedbackIdCounter.getAndIncrement());
    }

    // Get the maximum feedback ID from feedback.txt
    private static int getMaxFeedbackId() {
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",", 8); // Limit to 8 fields
                if (details.length >= 1) {
                    String feedbackId = details[0];
                    if (feedbackId.startsWith("FB")) {
                        try {
                            int id = Integer.parseInt(feedbackId.substring(2));
                            if (id > maxId) {
                                maxId = id;
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid IDs
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading feedback file: " + e.getMessage());
        }
        return maxId;
    }
}



