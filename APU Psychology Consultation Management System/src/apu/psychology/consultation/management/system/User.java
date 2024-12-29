/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apu.psychology.consultation.management.system;

/**
 *
 * @author behbe
 */
public abstract class User {
    protected String userId;
    protected String name;
    protected String email;
    protected String password;
    protected String role;

    // Constructor
    public User(String userId, String name, String email, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Update user profile
    public void updateProfile(String name, String email, String password) {
        setName(name);
        setEmail(email);
        setPassword(password);
    }

    // Abstract method to view dashboard
    public abstract void viewDashboard();
}

// Student class
class Student extends User {
    public Student(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Student"); // Call superclass constructor
    }

    @Override
    public void viewDashboard() {
        // Logic to display student dashboard
        System.out.println("Displaying Student Dashboard");
    }
}

// Lecturer class that extends User
class Lecturer extends User {

    public Lecturer(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Lecturer");
    }

    @Override
    public void viewDashboard() {
        // Logic to display lecturer dashboard
        System.out.println("Displaying Lecturer Dashboard");
        
    }
}

