package org.example.models;

public abstract class Vehicle {
    protected String id;
    protected String brand;
    protected String model;
    protected int year;
    protected int maximum_speed;

// constructor
// Constructor
public Vehicle(String id, String brand, String model, int year, int maximum_speed) {
    this.id = id;
    this.brand = brand;
    this.model = model;
    this.year = year;
    this.maximum_speed = maximum_speed;
}

    // Métodos públicos
    public void accelerate() {
        System.out.println(brand + " " + model + " está acelerando...");
    }

    public void brake() {
        System.out.println(brand + " " + model + " está frenando...");
    }

    public Object display_general_information() {
        System.out.println("=== INFORMACIÓN GENERAL ===");
        System.out.println("ID: " + id);
        System.out.println("Marca: " + brand);
        System.out.println("Modelo: " + model);
        System.out.println("Año: " + year);
        System.out.println("Velocidad Máxima: " + maximum_speed + " km/h");
        return null; // Retorna Object como indica el diagrama
    }

    // Getters
    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public int getMaximum_speed() { return maximum_speed; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setYear(int year) { this.year = year; }
    public void setMaximum_speed(int maximum_speed) { this.maximum_speed = maximum_speed; }
}
