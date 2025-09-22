package org.example;

import org.example.gui.MainWindow;
import javafx.application.Application;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("🚗 Iniciando SmartDrive Fleet Management System...");

        // Lanzar la aplicación JavaFX
        Application.launch(MainWindow.class, args);
    }
}