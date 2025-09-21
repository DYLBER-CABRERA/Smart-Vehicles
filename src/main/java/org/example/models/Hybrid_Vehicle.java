package org.example.models;

import org.example.interfaces.Autonomous;

public class Hybrid_Vehicle extends Vehicle implements Autonomous {
    private double energy_efficiency;
    private String current_mode = "Híbrido";
    private double battery_level = 80.0;
    private double fuel_level = 90.0;


    public Hybrid_Vehicle(String id, String brand, String model, int year,
                          int maximum_speed, double energy_efficiency) {
        super(id, brand, model, year, maximum_speed);
        this.energy_efficiency = energy_efficiency;
    }

    public String change_operating_mode() {
        String[] modes = {"Eléctrico", "Combustión", "Híbrido"};
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(current_mode)) {
                current_mode = modes[(i + 1) % modes.length];
                break;
            }
        }
        return String.format("Modo cambiado a: %s en %s %s\nEficiencia actual optimizada",
                current_mode, getBrand(), getModel());
    }

    @Override
    public String toDrive() {
        // Simular consumo según el modo
        if (current_mode.equals("Eléctrico")) {
            battery_level -= 3;
        } else if (current_mode.equals("Combustión")) {
            fuel_level -= 4;
        } else {
            battery_level -= 1.5;
            fuel_level -= 2;
        }

        return String.format("Conduciendo %s %s en modo %s\nBatería: %.1f%% | Combustible: %.1f%%",
                getBrand(), getModel(), current_mode, battery_level, fuel_level);
    }

    @Override
    public String autoPilot() {
        return String.format("Piloto automático activado en %s %s\nSistema híbrido inteligente operativo\nModo actual: %s",
                getBrand(), getModel(), current_mode);
    }

    @Override
    public VehicleInfo display_general_information() {
        String specificInfo = String.format("Eficiencia Energética: %.1f km/l\nModo Actual: %s\nBatería: %.1f%%\nCombustible: %.1f%%",
                energy_efficiency, current_mode, battery_level, fuel_level);
        return new VehicleInfo(getId(), getBrand(), getModel(), getYear(), getMaximum_speed(),
                "Vehículo Híbrido", specificInfo, "Conducible + Piloto Automático");
    }


    // Getters y Setters
    public double getEnergy_efficiency() { return energy_efficiency; }
    public void setEnergy_efficiency(double energy_efficiency) { this.energy_efficiency = energy_efficiency; }
    public String getCurrent_mode() { return current_mode; }
    public void setCurrent_mode(String current_mode) { this.current_mode = current_mode; }
    public double getBattery_level() { return battery_level; }
    public double getFuel_level() { return fuel_level; }
}



