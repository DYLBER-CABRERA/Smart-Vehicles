package org.example.services;


import org.example.models.Vehicle;
import java.util.*;

public class FleetManager {
    private Map<String, Vehicle> vehicles;

    public FleetManager() {
        this.vehicles = new LinkedHashMap<>(); // Mantiene el orden de inserción
    }

    /**
     * Agrega un vehículo a la flota
     */
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getId() == null) {
            return false;
        }

        if (vehicles.containsKey(vehicle.getId())) {
            // Ya existe un vehículo con ese ID
            return false;
        }

        vehicles.put(vehicle.getId(), vehicle);
        return true;
    }

    /**
     * Elimina un vehículo de la flota
     */
    public boolean removeVehicle(String vehicleId) {
        if (vehicleId == null) {
            return false;
        }

        return vehicles.remove(vehicleId) != null;
    }

    public Vehicle updateVehicle(String vehicleId, Vehicle updatedVehicle) {
        if (vehicleId == null || updatedVehicle == null || !vehicles.containsKey(vehicleId)) {
            return null;
        }

        vehicles.put(vehicleId, updatedVehicle);
        return updatedVehicle;
    }

    /**
     * Obtiene un vehículo por su ID
     */
    public Vehicle getVehicleById(String vehicleId) {
        return vehicles.get(vehicleId);
    }

    /**
     * Obtiene todos los vehículos de la flota
     */
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    /**
     * Obtiene la cantidad total de vehículos
     */
    public int getTotalVehicles() {
        return vehicles.size();
    }

    /**
     * Verifica si existe un vehículo con el ID dado
     */
    public boolean existsVehicle(String vehicleId) {
        return vehicles.containsKey(vehicleId);
    }

    /**
     * Limpia toda la flota
     */
    public void clearFleet() {
        vehicles.clear();
    }

    /**
     * Obtiene todos los IDs de vehículos
     */
    public Set<String> getAllVehicleIds() {
        return new HashSet<>(vehicles.keySet());
    }

    /**
     * Simula el comportamiento de todos los vehículos
     */
    public void simulateAllVehicles() {
        System.out.println("=== SIMULACIÓN DE LA FLOTA ===");
        for (Vehicle vehicle : vehicles.values()) {
            System.out.println("\n--- " + vehicle.getBrand() + " " + vehicle.getModel() + " ---");

            // Mostrar información general
            System.out.println(vehicle.display_general_information());

            // Acelerar y frenar (todos los vehículos)
            vehicle.accelerate();
            vehicle.brake();

            System.out.println("Simulación completada para " + vehicle.getBrand() + " " + vehicle.getModel());
        }
        System.out.println("\n=== FIN DE LA SIMULACIÓN ===");
    }
}