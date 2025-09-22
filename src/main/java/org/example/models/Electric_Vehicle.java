package org.example.models;
import org.example.interfaces.Autonomous_Advanced;
import org.example.models.Vehicle;

class Electric_Vehicle extends Vehicle implements Autonomous_Advanced {
    private double battery_capacity;
    private double current_charge = 100.0;
    private boolean emergency_mode = false;

    public Electric_Vehicle(String id, String brand, String model, int year,
                            int maximum_speed, double battery_capacity) {
        super(id, brand, model, year, maximum_speed);
        this.battery_capacity = battery_capacity;
    }

    public String battery_charging() {
        current_charge = 100.0;
        return String.format("Carga completada para %s %s\nCapacidad de batería: %.1f kWh\nCarga actual: %.1f%%",
                getBrand(), getModel(), battery_capacity, current_charge);
    }

    @Override
    public String toDrive(){
        if (current_charge > 0) {
            current_charge -= 3;
            return String.format("Conduciendo %s %s con motor eléctrico silencioso\nCarga restante: %.1f%%\nAutonomía estimada: %.0f km",
                    getBrand(), getModel(), current_charge, (current_charge * 4)); // 4km por %
        } else {
            return String.format("Batería agotada. %s %s necesita carga", getBrand(), getModel());
        }
    }

    @Override
    public String autopilot() {
        return String.format("Piloto automático avanzado activado en %s %s\nSensores eléctricos de alta precisión: OPERATIVOS\nSistemas de navegación autónoma: ACTIVOS",
                getBrand(), getModel());
    }

    @Override
    public String emergency_assistance() {
        emergency_mode = true;
        return String.format("🚨 SISTEMA DE EMERGENCIA ACTIVADO 🚨\nVehículo: %s %s\n" +
                        "✓ Frenado automático de emergencia\n" +
                        "✓ Llamada automática a servicios de emergencia\n" +
                        "✓ Activación de luces de emergencia\n" +
                        "✓ Navegación automática a centro médico\n" +
                        "✓ Transmisión de datos vitales\n" +
                        "Estado: EMERGENCIA ACTIVA",
                getBrand(), getModel());
    }

    public String deactivate_emergency() {
        emergency_mode = false;
        return "Sistema de emergencia desactivado. Vehículo en modo normal.";
    }

    @Override
    public VehicleInfo display_general_information() {
        String emergencyStatus = emergency_mode ? "🚨 MODO EMERGENCIA" : "Normal";
        String specificInfo = String.format("Capacidad de Batería: %.1f kWh\nCarga Actual: %.1f%%\nAutonomía: %.0f km\nEstado: %s",
                battery_capacity, current_charge, (current_charge * 4), emergencyStatus);
        return new VehicleInfo(getId(), getBrand(), getModel(), getYear(), getMaximum_speed(),
                "Vehículo Eléctrico", specificInfo, "Conducible + Piloto Automático + Asistencia Emergencias");
    }

    // Getters y Setters
    public double getBattery_capacity() { return battery_capacity; }
    public void setBattery_capacity(double battery_capacity) { this.battery_capacity = battery_capacity; }
    public double getCurrent_charge() { return current_charge; }
    public void setCurrent_charge(double current_charge) { this.current_charge = current_charge; }
    public boolean isEmergency_mode() { return emergency_mode; }
}