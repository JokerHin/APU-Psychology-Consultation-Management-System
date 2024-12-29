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

public class Main {
    public static void main(String[] args) {
        // Set up the start window
        SwingUtilities.invokeLater(() -> { //make sure GUI runs correctly on the correct thread
            // no put invokelater also can, just to prevent accident happen
            LoginGUI loginGUI = new LoginGUI();
            loginGUI.setVisible(true);
        });
    }
}



