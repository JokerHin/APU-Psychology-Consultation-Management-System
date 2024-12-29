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

public class Appointment {
    public static final String FILE_PATH = "appointments.txt";
    private static AtomicInteger appointmentIdCounter = new AtomicInteger(getMaxAppointmentId() + 1);

    private String appointmentId;
    private String studentId;
    private String lecturerId;
    private String date;
    private String time;
    private String location;
    private String status;
    private String slotId;     // New Field
    private String oldDate;    // New Field for Rescheduling
    private String oldTime;    // New Field for Rescheduling
    private String oldSlotId;  // New Field for Rescheduling

    // Constructor 
    public Appointment(String appointmentId, String studentId, String lecturerId, String date, String time, String location, String status) {
        this.appointmentId = appointmentId;
        this.studentId = studentId;
        this.lecturerId = lecturerId;
        this.date = date;
        this.time = time;
        this.location = location;
        this.status = status;
        this.slotId = "";      
        this.oldDate = "";
        this.oldTime = "";
        this.oldSlotId = "";
    }

    // Constructor for new 11-field entries
    public Appointment(String appointmentId, String studentId, String lecturerId, String date, String time, String location, String status, String slotId, String oldDate, String oldTime, String oldSlotId) {
        this.appointmentId = appointmentId;
        this.studentId = studentId;
        this.lecturerId = lecturerId;
        this.date = date;
        this.time = time;
        this.location = location;
        this.status = status;
        this.slotId = slotId;
        this.oldDate = oldDate;
        this.oldTime = oldTime;
        this.oldSlotId = oldSlotId;
    }

    // Getters and Setters for All Fields
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getOldDate() {
        return oldDate;
    }

    public String getOldTime() {
        return oldTime;
    }

    public String getOldSlotId() {
        return oldSlotId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public void setOldDate(String oldDate) {
        this.oldDate = oldDate;
    }

    public void setOldTime(String oldTime) {
        this.oldTime = oldTime;
    }

    public void setOldSlotId(String oldSlotId) {
        this.oldSlotId = oldSlotId;
    }

    // Read all appointments from the text file
    public static List<Appointment> readAppointments(String filePath) {
        List<Appointment> appointments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split into maximum 11 fields, handling missing fields
                String[] details = line.split(",", -1); // -1 to include trailing empty strings
                if (details.length >= 7) {
                    String appointmentId = details[0];
                    String studentId = details[1];
                    String lecturerId = details[2];
                    String date = details[3];
                    String time = details[4];
                    String location = details[5];
                    String status = details[6];
                    String slotId = details.length > 7 ? details[7] : "";
                    String oldDate = details.length > 8 ? details[8] : "";
                    String oldTime = details.length > 9 ? details[9] : "";
                    String oldSlotId = details.length > 10 ? details[10] : "";

                    if (details.length == 7) {
                        // Existing 7field appointment
                        Appointment appointment = new Appointment(appointmentId, studentId, lecturerId, date, time, location, status);
                        appointments.add(appointment);
                    } else if (details.length >= 11) {
                        // New 11field appointment
                        Appointment appointment = new Appointment(appointmentId, studentId, lecturerId, date, time, location, status, slotId, oldDate, oldTime, oldSlotId);
                        appointments.add(appointment);
                    } else {
                        System.out.println("Invalid appointment entry: " + line);
                    }
                } else {
                    System.out.println("Invalid appointment entry: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments file: " + e.getMessage());
        }
        return appointments;
    }

    // Write all appointments to the text file
    public static void writeAppointments(List<Appointment> appointments, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Appointment appointment : appointments) {
                bw.write(appointment.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to appointments file: " + e.getMessage());
        }
    }

    // Convert appointment details to CSV format
    public String toCSV() {
        return String.join(",",
                appointmentId,
                studentId,
                lecturerId,
                date,
                time,
                location,
                status,
                slotId != null ? slotId : "",
                oldDate != null ? oldDate : "",
                oldTime != null ? oldTime : "",
                oldSlotId != null ? oldSlotId : ""
        );
    }

    // Add a new appointment
    public static void addAppointment(Appointment newAppointment, String filePath) {
        List<Appointment> appointments = readAppointments(filePath);
        appointments.add(newAppointment);
        writeAppointments(appointments, filePath);
    }

    // Update an existing appointment
    public static void updateAppointment(Appointment updatedAppointment, String filePath) {
        List<Appointment> appointments = readAppointments(filePath);
        boolean found = false;
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getAppointmentId().equals(updatedAppointment.getAppointmentId())) {
                appointments.set(i, updatedAppointment);
                found = true;
                break;
            }
        }
        if (found) {
            writeAppointments(appointments, filePath);
        } else {
            System.out.println("Appointment with ID " + updatedAppointment.getAppointmentId() + " not found.");
        }
    }

    // Remove an appointment
    public static void removeAppointment(String appointmentId, String filePath) {
        List<Appointment> appointments = readAppointments(filePath);
        boolean removed = appointments.removeIf(appointment -> appointment.getAppointmentId().equals(appointmentId));
        if (removed) {
            writeAppointments(appointments, filePath);
        } else {
            System.out.println("Appointment with ID " + appointmentId + " not found.");
        }
    }

    // Get an appointment by ID
    public static Appointment getAppointmentById(String appointmentId, String filePath) {
        List<Appointment> appointments = readAppointments(filePath);
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    // Generate a unique Appointment ID
    public static String generateAppointmentId() {
        return String.format("AP%03d", appointmentIdCounter.getAndIncrement());
    }

    // Get the maximum appointment ID from appointments.txt
    private static int getMaxAppointmentId() {
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",", -1);
                if (details.length >= 1) {
                    String appointmentId = details[0];
                    if (appointmentId.startsWith("AP")) {
                        try {
                            int id = Integer.parseInt(appointmentId.substring(2));
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
            System.out.println("Error reading appointments file: " + e.getMessage());
        }
        return maxId;
    }
}


