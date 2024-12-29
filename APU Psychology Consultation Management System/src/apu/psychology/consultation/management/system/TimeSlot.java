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

public class TimeSlot {
    private static final String FILE_PATH = "slots.txt";
    private static AtomicInteger slotIdCounter = new AtomicInteger(getMaxSlotId() + 1);

    private String slotId;
    private String lecturerId;
    private String date;
    private String time;
    private String location;
    private String status;

    // Constructor
    public TimeSlot(String slotId, String lecturerId, String date, String time, String location, String status) {
        this.slotId = slotId;
        this.lecturerId = lecturerId;
        this.date = date;
        this.time = time;
        this.location = location;
        this.status = status;
    }

    // Getters and Setters
    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Read all time slots from the text file
    public static List<TimeSlot> readTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",", 6); // Adjusted for 6 fields
                if (details.length == 6) {
                    TimeSlot timeSlot = new TimeSlot(
                            details[0], // slotId
                            details[1], // lecturerId
                            details[2], // date
                            details[3], // time
                            details[4], // location
                            details[5]  // status
                    );
                    timeSlots.add(timeSlot);
                } else {
                    System.out.println("Invalid time slot entry: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading time slots file: " + e.getMessage());
        }
        return timeSlots;
    }

    // Read time slots for a specific lecturer
    public static List<TimeSlot> readTimeSlotsByLecturerId(String lecturerId) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",", 6); // Adjusted for 6 fields
                if (details.length == 6 && details[1].equals(lecturerId)) {
                    TimeSlot timeSlot = new TimeSlot(
                            details[0], 
                            details[1], 
                            details[2],
                            details[3],
                            details[4], 
                            details[5]  
                    );
                    timeSlots.add(timeSlot);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading time slots file: " + e.getMessage());
        }
        return timeSlots;
    }

    // Write all time slots to the text file
    public static void writeTimeSlots(List<TimeSlot> timeSlots) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (TimeSlot timeSlot : timeSlots) {
                bw.write(timeSlot.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to time slots file: " + e.getMessage());
        }
    }

    // Convert time slot details to CSV format
    public String toCSV() {
        return String.join(",",
                slotId,
                lecturerId,
                date,
                time,
                location,
                status
        );
    }

    // Add a new time slot
    public static void addTimeSlot(TimeSlot newTimeSlot) {
        List<TimeSlot> timeSlots = readTimeSlots();
        timeSlots.add(newTimeSlot);
        writeTimeSlots(timeSlots);
    }

    // Update an existing time slot
    public static void updateTimeSlot(TimeSlot updatedTimeSlot) {
        List<TimeSlot> timeSlots = readTimeSlots();
        boolean found = false;
        for (int i = 0; i < timeSlots.size(); i++) {
            if (timeSlots.get(i).getSlotId().equals(updatedTimeSlot.getSlotId())) {
                timeSlots.set(i, updatedTimeSlot);
                found = true;
                break;
            }
        }
        if (found) {
            writeTimeSlots(timeSlots);
        } else {
            System.out.println("Time slot with ID " + updatedTimeSlot.getSlotId() + " not found.");
        }
    }

    // Remove a time slot by ID
    public static void removeTimeSlot(String slotId) {
        List<TimeSlot> timeSlots = readTimeSlots();
        boolean removed = timeSlots.removeIf(slot -> slot.getSlotId().equals(slotId));
        if (removed) {
            writeTimeSlots(timeSlots);
        } else {
            System.out.println("Time slot with ID " + slotId + " not found.");
        }
    }

    // Generate a unique Slot ID using AtomicInteger
    public static String generateSlotId() {
        return String.format("S%03d", slotIdCounter.getAndIncrement());
    }

    // Get the maximum slot ID from slots.txt
    private static int getMaxSlotId() {
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",", 6); // Adjusted for 6 fields
                if (details.length >= 1) {
                    String slotId = details[0];
                    if (slotId.startsWith("S")) {
                        try {
                            int id = Integer.parseInt(slotId.substring(1));
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
            System.out.println("Error reading time slots file: " + e.getMessage());
        }
        return maxId;
    }

    // Get a time slot by ID
    public static TimeSlot getTimeSlotById(String slotId) {
        List<TimeSlot> timeSlots = readTimeSlots();
        for (TimeSlot slot : timeSlots) {
            if (slot.getSlotId().equals(slotId)) {
                return slot;
            }
        }
        return null;
    }
}
