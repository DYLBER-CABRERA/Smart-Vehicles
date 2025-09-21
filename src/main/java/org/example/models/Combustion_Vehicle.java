package org.example.models;

public class Combustion_Vehicle extends Vehicle implements Drivable{
    private double tank_capacity;
    private double fuel_level = 100.0;

    public Combustion_Vehicle(String id, String brand, String model, int year,
                              int maximum_speed, double tank_capacity) {
        super(id, brand, model, year, maximum_speed);
        this.tank_capacity = tank_capacity;
    }

    public String bunker() {
        fuel_level = 100.0;
        return String.format("Repostaje completado en %s %s\nCapacidad del tanque: %.1f litros\nNivel de combustible: %.1f%%",
                getBrand(), getModel(), tank_capacity, fuel_level);
    }

    @Override
    public String to_drive() {
        if (fuel_level > 0) {
            fuel_level -= 5; // Simular consumo
            return String.format("Conduciendo %s %s con motor de combustión\nNivel de combustible: %.1f%%",
                    getBrand(), getModel(), fuel_level);
        } else {
            return String.format("Sin combustible. %s %s necesita repostar", getBrand(), getModel());
        }
    }

    @Override
    public VehicleInfo display_general_information() {
        String specificInfo = String.format("Capacidad del Tanque: %.1f litros\nNivel de Combustible: %.1f%%",
                tank_capacity, fuel_level);
        return new VehicleInfo(getId(), getBrand(), getModel(), getYear(), getMaximum_speed(),
                "Vehículo de Combustión", specificInfo, "Solo conducible");
    }

    // Getters y Setters
    public double getTank_capacity() { return tank_capacity; }
    public void setTank_capacity(double tank_capacity) { this.tank_capacity = tank_capacity; }
    public double getFuel_level() { return fuel_level; }
    public void setFuel_level(double fuel_level) { this.fuel_level = fuel_level; }
}

