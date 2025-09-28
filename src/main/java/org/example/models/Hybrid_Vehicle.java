package org.example.models;

import org.example.interfaces.Autonomous;

public class Hybrid_Vehicle extends Vehicle implements Autonomous {
    private double energy_efficiency;
    private String current_mode = "Híbrido";
    private double battery_level;
    private double fuel_level;
    private double battery_capacity = 15.0; // kWh típico para híbridos
    private double tank_capacity = 45.0; // Litros típico para híbridos
    private boolean autopilot_active = false;
    private boolean engine_running = false;
    private int transitions_count = 0;
    private double kilometers_driven = 0.0;
    private boolean eco_mode = false;
    private boolean sport_mode = false;
    private boolean charging = false;
    private double charging_speed = 2.5; // kWh por minuto de carga rápida
    private int battery_temperature = 25;



    public Hybrid_Vehicle(String id, String brand, String model, int year,
                          int maximum_speed, double energy_efficiency) {
        super(id, brand, model, year, maximum_speed);
        this.energy_efficiency = energy_efficiency;
        this.battery_capacity = battery_capacity * 0.8; //80% de la capacidad
        this.fuel_level = tank_capacity * 0.9; //tanque lleno inicialmente con 90%
    }

    public String change_operating_mode() {
        String previous_mode = current_mode;
        String[] modes = {"Eléctrico", "Combustión", "Híbrido"};
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(current_mode)) {
                current_mode = modes[(i + 1) % modes.length];
                break;
            }
        }
        // Verificar disponibilidad del modo
        if (current_mode.equals("Eléctrico") && battery_level < battery_capacity * 0.1) {
            current_mode = "Combustión";
        } else if (current_mode.equals("Combustión") && fuel_level < tank_capacity * 0.05) {
            current_mode = "Eléctrico";
        }

        transitions_count++;

        String mode_description =  getModeDescription();
        return String.format("🔄 CAMBIO DE MODO COMPLETADO\n" +
                        "Vehículo: %s %s\n" +
                        "Modo anterior: %s → Modo actual: %s\n" +
                        "%s\n" +
                        "Transiciones realizadas: %d",
                getBrand(), getModel(), previous_mode, current_mode,
                mode_description, transitions_count);
    }

    private String getModeDescription() {
        switch (current_mode) {
            case "Eléctrico":
                return "⚡ Motor eléctrico únicamente\n✅ Cero emisiones\n✅ Operación silenciosa";
            case "Combustión":
                engine_running = true;
                return "🔥 Motor de combustión únicamente\n✅ Máxima potencia\n⚠️ Mayor consumo";
            case "Híbrido":
                return "🔋⛽ Ambos motores coordinados\n✅ Eficiencia optimizada\n✅ Transiciones automáticas";
            default:
                return "Modo estándar";
        }
    }

    @Override
    public void accelerate() {
        double battery_used = 0;
        double fuel_used = 0;

        switch (current_mode) {
            case "Eléctrico":
                if (battery_level > 0) {
                    battery_used = eco_mode ? 0.03 : 0.05;
                    battery_level = Math.max(0, battery_level - battery_used);
                }
                break;
            case "Combustión":
                if (fuel_level > 0) {
                    fuel_used = eco_mode ? 0.04 : sport_mode ? 0.08 : 0.06;
                    fuel_level = Math.max(0, fuel_level - fuel_used);
                }
                break;
            case "Híbrido":
                // Lógica inteligente de distribución de potencia
                double battery_ratio = battery_level / battery_capacity;
                double fuel_ratio = fuel_level / tank_capacity;

                if (sport_mode) {
                    // Usar ambos motores para máxima potencia
                    battery_used = 0.04;
                    fuel_used = 0.05;
                } else if (eco_mode) {
                    // Priorizar eléctrico si hay suficiente batería
                    if (battery_ratio > 0.3) {
                        battery_used = 0.02;
                    } else {
                        fuel_used = 0.03;
                    }
                } else {
                    // Modo híbrido inteligente
                    battery_used = 0.02;
                    fuel_used = 0.03;
                }

                battery_level = Math.max(0, battery_level - battery_used);
                fuel_level = Math.max(0, fuel_level - fuel_used);
                break;
        }

        String power_source = battery_used > 0 && fuel_used > 0 ? "⚡🔥 Dual" :
                battery_used > 0 ? "⚡ Eléctrico" : "🔥 Combustión";

        System.out.printf("🚗 %s %s acelerando en modo %s\n" +
                        "Fuente: %s | Batería: %.1f%% | Combustible: %.1f%%\n",
                getBrand(), getModel(), current_mode, power_source,
                (battery_level / battery_capacity) * 100,
                (fuel_level / tank_capacity) * 100);
    }


// Metodo para frenar
    @Override
    public void brake() {
        if (!current_mode.equals("Combustión")) {
            // Regeneración de energía (excepto en modo solo combustión)
            double energy_recovered = sport_mode ? 0.04 : 0.03;
            battery_level = Math.min(battery_capacity, battery_level + energy_recovered);

            System.out.printf("🔋 %s %s frenando con regeneración\n" +
                            "Energía recuperada | Batería: %.1f%% | Combustible: %.1f%%\n",
                    getBrand(), getModel(),
                    (battery_level / battery_capacity) * 100,
                    (fuel_level / tank_capacity) * 100);
        } else {
            System.out.printf("🛑 %s %s frenando (modo combustión)\n", getBrand(), getModel());
        }
    }

    // Método para conducir
@Override
    public String toDrive() {
        if (battery_level <= 0 && fuel_level <= 0) {
            return String.format("❌ ENERGÍA AGOTADA\n%s %s necesita recarga y/o repostaje",
                    getBrand(), getModel());
        }

        // Auto-cambio inteligente de modo si es necesario
        String auto_switch_message = "";
        if (current_mode.equals("Eléctrico") && battery_level < battery_capacity * 0.1 && fuel_level > tank_capacity * 0.1) {
            current_mode = "Combustión";
            auto_switch_message = "\n🔄 Cambio automático a modo Combustión (batería baja)";
        } else if (current_mode.equals("Combustión") && fuel_level < tank_capacity * 0.1 && battery_level > battery_capacity * 0.1) {
            current_mode = "Eléctrico";
            auto_switch_message = "\n🔄 Cambio automático a modo Eléctrico (combustible bajo)";
        }

        double distance = Math.random() * 12 + 8; // 8-20 km simulados
        double battery_consumption = 0;
        double fuel_consumption = 0;

        // Cálculo de consumo según modo y condiciones
        switch (current_mode) {
            case "Eléctrico":
                battery_consumption = distance * 0.15; // kWh/km
                break;
            case "Combustión":
                fuel_consumption = distance * 0.08; // L/km
                break;

            default: // Híbrido
                battery_consumption = distance * 0.08;
                fuel_consumption = distance * 0.06;
        }

        battery_level = Math.max(0, battery_level - battery_consumption);
        fuel_level = Math.max(0, fuel_level - fuel_consumption);
        kilometers_driven += distance;

        String driving_status = autopilot_active ? "🤖 PILOTO AUTOMÁTICO" : "👤 CONDUCCIÓN MANUAL";
        String efficiency_icon = eco_mode ? "🌱" : sport_mode ? "🏁" : "⚖️";

        return String.format("🚗 CONDUCIENDO %s %s \n" +
                        "%s | Modo: %s %s\n" +
                        "Distancia recorrida: %.1f km\n" +
                        "Batería: %.1f%% | Combustible: %.1f%%\n" +
                        "Eficiencia: %.1f km/l\n%s",
                getBrand(), getModel(),
                driving_status, current_mode, efficiency_icon,
                distance,
                (battery_level / battery_capacity) * 100,
                (fuel_level / tank_capacity) * 100,
                energy_efficiency,
                auto_switch_message);
    }
// Metodo para repostar
    public String refuel() {
        double fuel_added = tank_capacity - fuel_level;
        fuel_level = tank_capacity;
        return String.format("⛽ Repostaje completado\nCombustible agregado: %.2f L\nTanque lleno: %.1f L",
                fuel_added, tank_capacity);
    }
// Metodo para recargar batería
    public String chargeBattery() {
        double charge_added = battery_capacity - battery_level;
        battery_level = battery_capacity;
        return String.format("🔋 Carga completada\nEnergía agregada: %.2f kWh\nBatería llena: %.1f kWh",
                charge_added, battery_capacity);
    }
// Metodo para encender el motor
    public String startEngine() {
        if (engine_running) {
            return "⚠️ El motor ya está encendido.";
        }
        if (battery_level <= 0 && fuel_level <= 0) {
            return "🔋 Batería y combustible agotados. No se puede encender el motor.";
        }
        engine_running = true;
        if (battery_level > 0) battery_level = Math.max(0, battery_level - 0.1);
        if (fuel_level > 0) fuel_level = Math.max(0, fuel_level - 0.05);
        return String.format("🔥 MOTOR ENCENDIDO\n%s %s listo para conducir\nBatería: %.1f%% | Combustible: %.1f%%",
                getBrand(), getModel(),
                (battery_level / battery_capacity) * 100,
                (fuel_level / tank_capacity) * 100);
    }
// Metodo para apagar el motor
    public String stopEngine() {
        if (engine_running) {
            engine_running = false;
            return String.format("🛑 Motor apagado\n%s %s en reposo\nKilómetros recorridos en esta sesión: %.1f km",
                    getBrand(), getModel(), kilometers_driven);
        }
        return "⚠️ El motor ya está apagado.";
    }


    // metodo para carga rápida
    public String startFastCharging() {
        if (battery_level <= 0 && fuel_level <= 0) {
            return "NO_ENERGY";
        }
        //si la carga actual es mayor o igual a la capacidad de la batería
        else if (battery_level >= battery_capacity * 0.9) {
            return "⚠️ Batería casi llena. Carga lenta recomendada";
        }

        charging = true; // cargando

        double fastChargeAmount = battery_capacity * 0.2;
        double charge_added = Math.min(fastChargeAmount, battery_capacity - battery_level); // carga agregada
        battery_level += charge_added;//carga actual se le acumula la carga agregada
        battery_temperature = Math.min(45, battery_temperature + 5); // aumento de temperatura de la batería

        return String.format("⚡ CARGA RÁPIDA ACTIVA\n" +
                        "Energía agregada: %.2f kWh\n" +
                        "Carga actual: %.1f%% (%.1f kWh)\n" +
                        "Temperatura batería: %d°C\n" +
                        "Tiempo estimado para carga completa: %.0f min",
                charge_added, (battery_level / battery_capacity) * 100,
                battery_level, battery_temperature,
                (battery_capacity -  battery_level) / charging_speed);
    }


    @Override
    public String autoPilot() {
        if ((battery_level < battery_capacity * 0.1) && (fuel_level < tank_capacity * 0.1)) {
            return "⚠️ Energía insuficiente para piloto automático";
        }

        autopilot_active = !autopilot_active;

        if (autopilot_active) {
            return String.format("🤖 PILOTO AUTOMÁTICO ACTIVADO\n" +
                            "Vehículo: %s %s\n" +
                            "✅ Sistema híbrido inteligente activo\n" +
                            "✅ Gestión automática de energía\n" +
                            "✅ Optimización de modo según condiciones\n" +
                            "Modo actual: %s\n" +
                            "Eficiencia máxima garantizada",
                    getBrand(), getModel(), current_mode);
        } else {
            return String.format("👤 Control manual restaurado en %s %s\n" +
                            "Piloto automático desactivado\nModo actual: %s",
                    getBrand(), getModel(), current_mode);
        }
    }

    @Override
    public VehicleInfo display_general_information() {
        String mode_details = getModeDescription().replace("\n", " | ");
        String system_status = autopilot_active ? "🤖 Piloto Automático" : "👤 Manual";
        String efficiency_mode = eco_mode ? "🌱 ECO" : sport_mode ? "🏁 SPORT" : "⚖️ Normal";

        String specificInfo = String.format(
                "🔋⛽ INFORMACIÓN HÍBRIDA:\n" +
                        "Eficiencia Energética: %.1f km/l equivalente\n" +
                        "Capacidad Batería: %.1f kWh (%.1f%%)\n" +
                        "Capacidad Tanque: %.1f L (%.1f%%)\n" +
                        "Modo Actual: %s (%s)\n" +
                        "Sistema: %s\n" +
                        "Transiciones: %d cambios\n" +
                        "Kilómetros: %.1f km\n" +
                        "Autonomía Eléctrica: %.0f km\n" +
                        "Autonomía Combustible: %.0f km\n" +
                        "Autonomía Total: %.0f km",
                energy_efficiency, battery_capacity, (battery_level / battery_capacity) * 100,
                tank_capacity, (fuel_level / tank_capacity) * 100,
                current_mode, efficiency_mode, system_status, transitions_count,
                kilometers_driven, battery_level / 0.15, fuel_level / 0.08,
                (battery_level / 0.15) + (fuel_level / 0.08));

        return new VehicleInfo(getId(), getBrand(), getModel(), getYear(), getMaximum_speed(),
                "Vehículo Híbrido", specificInfo,
                "Conducible + Piloto Automático + Sistema Dual");
    }

    // Getters y Setters mejorados
    public double getEnergy_efficiency() { return energy_efficiency; }
    public void setEnergy_efficiency(double energy_efficiency) { this.energy_efficiency = energy_efficiency; }
    public String getCurrent_mode() { return current_mode; }
    public void setCurrent_mode(String current_mode) { this.current_mode = current_mode; }
    public double getBattery_level() { return battery_level; }
    public void setBattery_level(double battery_level) {
        this.battery_level = Math.max(0, Math.min(battery_capacity, battery_level));
    }
    public double getFuel_level() { return fuel_level; }
    public void setFuel_level(double fuel_level) {
        this.fuel_level = Math.max(0, Math.min(tank_capacity, fuel_level));
    }
    public double getBattery_capacity() { return battery_capacity; }
    public double getTank_capacity() { return tank_capacity; }
    public boolean isAutopilot_active() { return autopilot_active; }
    public boolean isEngine_running() { return engine_running; }
    public int getTransitions_count() { return transitions_count; }
    public double getKilometers_driven() { return kilometers_driven; }
    public boolean isEco_mode() { return eco_mode; }
    public boolean isSport_mode() { return sport_mode; }
    public double getBattery_percentage() { return (battery_level / battery_capacity) * 100; }
    public double getFuel_percentage() { return (fuel_level / tank_capacity) * 100; }
    public double getTotalRange() { return (battery_level / 0.15) + (fuel_level / 0.08); }
}






