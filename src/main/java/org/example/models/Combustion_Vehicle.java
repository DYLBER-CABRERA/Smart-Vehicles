package org.example.models;

import org.example.interfaces.Drivable;

public class Combustion_Vehicle extends Vehicle implements Drivable {
    private double tank_capacity;
    private double fuel_level;
    private boolean engine_running = false;
    private double fuel_consumption_rate = 0.15; // L/km promedio
    private int engine_temperature = 20; // Temperatura inicial
    private double kilometers_driven = 0.0;

    public Combustion_Vehicle(String id, String brand, String model, int year,
                              int maximum_speed, double tank_capacity) {
        super(id, brand, model, year, maximum_speed);
        this.tank_capacity = tank_capacity;
        this.fuel_level = tank_capacity; //tanque lleno inicialmente
    }

    // Método para tanquear
    public String bunker() {
        // Si el motor está encendido, no se puede tanquear
      if(engine_running) {
          return "❌ Apague el motor antes de tanquear por seguridad";
      }

        double fuel_added = tank_capacity - fuel_level; //me dice cuanto le falta para estar lleno
        fuel_level = tank_capacity; //lleno
        return String.format("⛽ REPOSTAJE COMPLETADO ⛽\n" +
                        "Vehículo: %s %s\n" +
                        "Combustible agregado: %.2f litros\n" +
                        "Capacidad total: %.1f litros\n" +
                        "Nivel actual: 100%%\n" +
                        "Autonomía estimada: %.0f km",
                getBrand(), getModel(), fuel_added, tank_capacity,
                fuel_level / fuel_consumption_rate);
    }

    // Métodos para encender y apagar el motor
    public String startEngine() {
        //si el nivel de combustible es 0, no se puede encender
        if (fuel_level <= 0) {
            return "❌ Sin combustible. No se puede encender el motor";
        }

         //si el motor esta apagado
        if (!engine_running) {
            engine_running = true; // motor encendido
            engine_temperature = 90; // temperatura de operación
            fuel_level -= 0.1; // Consumo al encender
            return String.format("🔥 MOTOR ENCENDIDO\n%s %s listo para conducir\n" +
                            "Temperatura del motor: %d°C\n" +
                            "Combustible: %.1f%%",
                    getBrand(), getModel(), engine_temperature,
                    (fuel_level / tank_capacity) * 100);
        }
        return "⚠️ El motor ya está encendido";
    }

    // Método para apagar el motor
    public String stopEngine() {
        //si el motor esta encendido
        if (engine_running) {
            engine_running = false; // motor apagado
            engine_temperature = 20; // temperatura ambiente
            return String.format("🛑 Motor apagado\n%s %s en reposo\n" +
                            "Kilómetros recorridos en esta sesión: %.1f km",
                    getBrand(), getModel(), kilometers_driven);
        }
        return "⚠️ El motor ya está apagado";
    }

    // Implementación de métodos de la interfaz Drivable
    @Override
    public void accelerate() {
        // No se puede acelerar si el motor está apagado
        if (!engine_running) {
            System.out.println("⚠️ Encienda el motor primero");
            return;
        }

            // nivel de combustible es mayor a 0
        if (fuel_level > 0) {
            // Consumo adicional por aceleración
            double extra_consumption = 0.05;
            fuel_level = Math.max(0, fuel_level - extra_consumption); // evitar negativo
            engine_temperature = Math.min(120, engine_temperature + 2); // calentamiento del motor

            System.out.printf("🚗 %s %s acelerando con motor de combustión\n" +
                            "Temperatura: %d°C | Combustible: %.1f%%\n",
                    getBrand(), getModel(), engine_temperature,
                    (fuel_level / tank_capacity) * 100);
        } else {
            engine_running = false;
            System.out.println("❌ Motor apagado por falta de combustible");
        }
    }

    // Metodo para frenar
    @Override
    public void brake() {
        // No se puede frenar si el motor está apagado
        if (engine_running) {
            engine_temperature = Math.max(60, engine_temperature - 1);
        }
        System.out.printf("🛑 %s %s frenando\n" +
                        "Temperatura del motor: %d°C\n",
                getBrand(), getModel(), engine_temperature);
    }

    // Método para conducir
    @Override
    public String toDrive() {
        // Si el motor está apagado, intentar encenderlo
        if (!engine_running) {
            return startEngine() + "\n🚗 Iniciando conducción...";
        }
        // Sí hay combustible, simular conducción
        if (fuel_level > 0) {
            double distance = Math.random() * 10 + 5; // 5-15 km simulados
            double consumption = distance * fuel_consumption_rate; // consumo basado en distancia
            fuel_level = Math.max(0, fuel_level - consumption); // evitar negativo
            kilometers_driven += distance; // actualizar km totales

            // Calentamiento del motor durante conducción
            engine_temperature = Math.min(95, engine_temperature + 1);

            return String.format("🚗 CONDUCIENDO %s %s\n" +
                            "Distancia recorrida: %.1f km\n" +
                            "Consumo: %.2f litros\n" +
                            "Combustible restante: %.1f%% (%.1f L)\n" +
                            "Temperatura motor: %d°C\n" +
                            "Autonomía restante: %.0f km",
                    getBrand(), getModel(), distance, consumption,
                    (fuel_level / tank_capacity) * 100, fuel_level,
                    engine_temperature, fuel_level / fuel_consumption_rate);
        } else {
            engine_running = false;
            return String.format("❌ SIN COMBUSTIBLE\n%s %s se ha detenido\n" +
                    "Necesita repostar para continuar", getBrand(), getModel());
        }
    }

    // Método para obtener el estado del motor
    public String getEngineStatus() {
        // Si el motor está encendido, mostrar temperatura y estado
        if (engine_running) {
            String temp_status = engine_temperature > 100 ? "🔥 CALIENTE" :
                    engine_temperature > 85 ? "🌡️ Normal" : "❄️ Frío";
            return String.format("✅ Motor encendido - %s (%d°C)", temp_status, engine_temperature);
        }
        return "⭕ Motor apagado";
    }

    // Implementación del método para mostrar información general
    @Override
    public VehicleInfo display_general_information() {
        String specificInfo = String.format(
                "🔧 INFORMACIÓN TÉCNICA:\n" +
                        "Capacidad del Tanque: %.1f litros\n" +
                        "Combustible Actual: %.1f L (%.1f%%)\n" +
                        "Consumo Promedio: %.2f L/100km\n" +
                        "Autonomía Restante: %.0f km\n" +
                        "Estado del Motor: %s\n" +
                        "Kilómetros Totales: %.1f km",
                tank_capacity, fuel_level, (fuel_level / tank_capacity) * 100,
                fuel_consumption_rate * 100, fuel_level / fuel_consumption_rate,
                getEngineStatus(), kilometers_driven);

        return new VehicleInfo(getId(), getBrand(), getModel(), getYear(), getMaximum_speed(),
                "Vehículo de Combustión", specificInfo, "Solo conducible");
    }

    // Getters y Setters
    public double getTank_capacity() { return tank_capacity; }
    public void setTank_capacity(double tank_capacity) { this.tank_capacity = tank_capacity; }
    public double getFuel_level() { return fuel_level; }
    public void setFuel_level(double fuel_level) {
        this.fuel_level = Math.max(0, Math.min(tank_capacity, fuel_level));
    }
    public boolean isEngine_running() { return engine_running; }
    public int getEngine_temperature() { return engine_temperature; }
    public double getKilometers_driven() { return kilometers_driven; }
    public double getFuel_percentage() { return (fuel_level / tank_capacity) * 100; }
    public double getRange() { return fuel_level / fuel_consumption_rate; }
}

