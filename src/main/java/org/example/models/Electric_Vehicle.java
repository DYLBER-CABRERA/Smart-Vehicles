package org.example.models;
import org.example.interfaces.Autonomous_Advanced;


public class Electric_Vehicle extends Vehicle implements Autonomous_Advanced {
    private double battery_capacity;
    private double current_charge = 100.0;
    private boolean emergency_mode = false;
    private boolean autopilot_active = false;
    private double energy_consumption_rate = 0.18; // kWh/km promedio
    private int battery_temperature = 25;
    private double kilometers_driven = 0.0;
    private boolean charging = false;
    private double charging_speed = 2.5; // kWh por minuto de carga rápida
    private boolean preconditioning = false;
    private boolean engine_running = false;

    public Electric_Vehicle(String id, String brand, String model, int year,
                            int maximum_speed, double battery_capacity) {
        super(id, brand, model, year, maximum_speed);
        this.battery_capacity = battery_capacity;
        this.current_charge = battery_capacity; // Batería llena inicialmente
    }

    // metodo para cargar la batería
    public String battery_charging() {
        // Simula carga completa instantánea para simplificar
        if (current_charge >= battery_capacity) {
            return "🔋 Batería ya está completamente cargada";
        }

        // Iniciar carga
        charging = true;
        // Calcular energía necesaria para carga completa
        double charge_needed = battery_capacity - current_charge;
        // me dice si la carga actual es igual a la capacidad de la batería
        current_charge = battery_capacity;
        // Finalizar carga
        charging = false;

        // Precondicionamiento automático después de carga completa
        preconditioning = true;
        // temperatura de la bateria
        battery_temperature = 30;

        return String.format("⚡ CARGA COMPLETADA ⚡\n" +
                        "Vehículo: %s %s\n" +
                        "Energía agregada: %.2f kWh\n" +
                        "Capacidad total: %.1f kWh\n" +
                        "Nivel actual: 100%%\n" +
                        "Autonomía estimada: %.0f km\n" +
                        "🌡️ Batería preacondicionada para óptimo rendimiento",
                getBrand(), getModel(), charge_needed, battery_capacity,
                current_charge / energy_consumption_rate);
    }

    // Métodos para encender y apagar el motor
    public String startEngine() {
        //si el nivel de combustible es 0, no se puede encender
        if (engine_running) {
            return "⚠️ El motor ya está apagado.";
        }
        else if (current_charge <= 0) {
            return "🔋 Batería agotada. No se puede encender el motor.";
        }
        //si el motor esta apagado
            engine_running = true; // motor encendido
            current_charge -= 0.1; // Consumo al encender
            return String.format("🔥 MOTOR ENCENDIDO\n%s %s listo para conducir\n" +
                            "Combustible: %.1f%%",
                    getBrand(), getModel(),
                    (current_charge / battery_capacity) * 100);

    }


    // Método para apagar el motor
    public String stopEngine() {
        //si el motor esta encendido
        if (engine_running) {
            engine_running = false; // motor apagado
            return String.format("🛑 Motor apagado\n%s %s en reposo\n" +
                            "Kilómetros recorridos en esta sesión: %.1f km",
                    getBrand(), getModel(), kilometers_driven);
        }
        return "⚠️ El motor ya está apagado";
    }


    // metodo para carga rápida
    public String startFastCharging() {
        //si la carga actual es mayor o igual a la capacidad de la batería
        if (current_charge >= battery_capacity * 0.9) {
            return "⚠️ Batería casi llena. Carga lenta recomendada";
        }

        charging = true; // cargando

        double charge_added = Math.min(charging_speed * 10, battery_capacity - current_charge); // carga agregada
        current_charge += charge_added;//carga actual se le acumula la carga agregada
        battery_temperature = Math.min(45, battery_temperature + 5); // aumento de temperatura de la batería

        return String.format("⚡ CARGA RÁPIDA ACTIVA\n" +
                        "Energía agregada: %.2f kWh\n" +
                        "Carga actual: %.1f%% (%.1f kWh)\n" +
                        "Temperatura batería: %d°C\n" +
                        "Tiempo estimado para carga completa: %.0f min",
                charge_added, (current_charge / battery_capacity) * 100,
                current_charge, battery_temperature,
                (battery_capacity - current_charge) / charging_speed);
    }

    // Método para Conducir el vehículo en la cual es una interfaz implementada
    @Override
    public String toDrive(){
        //si la carga actual es menor o igual a 0
        if (current_charge <= 0) {
            return String.format("🔋 BATERÍA AGOTADA\n%s %s necesita carga urgente\n" +
                    "Active el modo de emergencia si es necesario", getBrand(), getModel());
        }

        double distance = Math.random() * 15 + 10; // 10-25 km simulados
        double energy_used = distance * energy_consumption_rate; // kWh consumidos

        // Eficiencia mejorada con preacondicionamiento
        if (preconditioning) {
            energy_used *= 0.9; // 10% más eficiente
        }

        // Penalización por temperatura alta
        if (battery_temperature > 35) {
            energy_used *= 1.15; // 15% menos eficiente
        }

        current_charge = Math.max(0, current_charge - energy_used); // actualizar carga
        kilometers_driven += distance; // actualizar kilómetros
        battery_temperature = (int) Math.max(20, Math.min(40, battery_temperature + (distance / 10))); // ajustar temperatura

        String driving_mode = autopilot_active ? "🤖 PILOTO AUTOMÁTICO" : "👤 CONDUCCIÓN MANUAL";
        String efficiency = preconditioning ? " (Optimizado)" :
                battery_temperature > 35 ? " (Reducido)" : "";

        return String.format("⚡ CONDUCIENDO %s %s\n" +
                        "%s\n" +
                        "Distancia recorrida: %.1f km\n" +
                        "Consumo: %.2f kWh%s\n" +
                        "Batería restante: %.1f%% (%.1f kWh)\n" +
                        "Temperatura: %d°C\n" +
                        "Autonomía restante: %.0f km",
                getBrand(), getModel(), driving_mode, distance, energy_used, efficiency,
                (current_charge / battery_capacity) * 100, current_charge,
                battery_temperature, current_charge / energy_consumption_rate);
    }

    @Override
    // metodo para acelerar el vehículo
    public void accelerate() {
        //si la carga actual es mayor a 0
        if (current_charge > 0) {
            // energia usada
            double energy_used = 0.05; // kWh por aceleración
            current_charge = Math.max(0, current_charge - energy_used);  // actualizar carga actual
            battery_temperature = Math.min(40, battery_temperature + 1); // aumentar temperatura

            String efficiency_note = battery_temperature > 35 ? " (Rendimiento reducido por temperatura)" :
                    preconditioning ? " (Rendimiento optimizado)" : ""; // nota de eficiencia

            System.out.printf("⚡ %s %s acelerando silenciosamente\n" +
                            "Batería: %.1f%% | Temp: %d°C%s\n",
                    getBrand(), getModel(),
                    (current_charge / battery_capacity) * 100,
                    battery_temperature, efficiency_note);
        } else {
            System.out.println("🔋 Batería agotada. Vehículo en modo seguro");
        }
    }

    // metodo para frenar el vehículo
    @Override
    public void brake() {
        //si la carga actual es menor al 95% de la capacidad de la batería
        if (current_charge < battery_capacity * 0.95) {
            // Regeneración de energía al frenar
            double energy_recovered = 0.02;
            current_charge = Math.min(battery_capacity, current_charge + energy_recovered); // actualizar carga

            System.out.printf("🔋 %s %s frenando con regeneración\n" +
                            "Energía recuperada: %.2f kWh | Batería: %.1f%%\n",
                    getBrand(), getModel(), energy_recovered,
                    (current_charge / battery_capacity) * 100);
        } else {
            System.out.printf("🛑 %s %s frenando\n", getBrand(), getModel());
        }

        battery_temperature = Math.max(20, battery_temperature - 1);
    }

    // método para asistencia en emergencias
    @Override
    public String emergencyAssistance() {
        emergency_mode = true; // activar modo emergencia
        autopilot_active = false; // desactivar piloto automático

        // Reservar energía para funciones de emergencia
        double emergency_reserve = battery_capacity * 0.05;
        // si la carga actual es menor a la reserva de emergencia
        if (current_charge < emergency_reserve) {
            // usar toda la carga disponible
            current_charge = emergency_reserve;
        }

        return String.format("🚨 SISTEMA DE EMERGENCIA ACTIVADO 🚨\n" +
                        "Vehículo: %s %s\n" +
                        "✅ Frenado automático de emergencia\n" +
                        "✅ Llamada automática a servicios (911)\n" +
                        "✅ Activación de luces de emergencia\n" +
                        "✅ GPS enviado a contactos de emergencia\n" +
                        "✅ Cámaras grabando incidente\n" +
                        "✅ Climatización de emergencia activa\n" +
                        "🔋 Reserva de energía: %.1f%% disponible\n" +
                        "📍 Ubicación transmitida cada 30 segundos",
                getBrand(), getModel(),
                (current_charge / battery_capacity) * 100);

    }

    // metodo para desactivar el modo emergencia
    public String deactivate_emergency() {
        if (!emergency_mode) {
            return "⚠️ Modo emergencia no está activo";
        }

        //modo emergencia desactivado
        emergency_mode = false;
        return String.format("✅ EMERGENCIA DESACTIVADA\n" +
                "%s %s volviendo a modo normal\n" +
                "Sistemas restaurados correctamente", getBrand(), getModel());
    }

    // método para activar/desactivar el piloto automático
    @Override
    public String autoPilot() {
        //si la carga actual es menor al 20% de la capacidad de la batería
        if (current_charge < battery_capacity * 0.20) {
            return "⚠️ Batería insuficiente para piloto automático (mínimo 20%)";
        }

        if (emergency_mode) {
            return "❌ Piloto automático no disponible en modo emergencia";
        }

        autopilot_active = !autopilot_active; // asignar el valor contrario

            // si el piloto automático está activo
        if (autopilot_active) {
            preconditioning = true; // activar preacondicionamiento
            return String.format("🤖 PILOTO AUTOMÁTICO ACTIVADO\n" +
                            "Vehículo: %s %s\n" +
                            "✅ Conducción autónoma nivel 4\n" +
                            "✅ Monitoreo 360° activo\n" +
                            "✅ IA predictiva funcionando\n" +
                            "✅ Sistemas de seguridad en máxima alerta\n" +
                            "🔋 Consumo optimizado automáticamente\n" +
                            "📡 Conectado a red de vehículos inteligentes",
                    getBrand(), getModel());
        } else {
            return String.format("👤 Control manual restaurado en %s %s\n" +
                    "Piloto automático desactivado", getBrand(), getModel());
        }
    }
 //  método para mostrar la información general del vehículo
    @Override
    public VehicleInfo display_general_information() {
        // estado del modo si está en emergencia, piloto automático o manual
        String mode_status = emergency_mode ? "🚨 EMERGENCIA" :
                autopilot_active ? "🤖 Piloto Automático" : "👤 Manual";
        // estado de la salud de la batería si está caliente, optimizada o normal
        String battery_health = battery_temperature > 35 ? "⚠️ Caliente" :
                preconditioning ? "✅ Optimizada" : "🌡️ Normal";

        String specificInfo = String.format(
                "⚡ INFORMACIÓN TÉCNICA:\n" +
                        "Capacidad Batería: %.1f kWh\n" +
                        "Carga Actual: %.1f kWh (%.1f%%)\n" +
                        "Consumo Promedio: %.2f kWh/100km\n" +
                        "Autonomía Restante: %.0f km\n" +
                        "Temperatura Batería: %d°C (%s)\n" +
                        "Modo Actual: %s\n" +
                        "Kilómetros Totales: %.1f km\n" +
                        "Estado Carga: %s",
                battery_capacity, current_charge, (current_charge / battery_capacity) * 100,
                energy_consumption_rate * 100, current_charge / energy_consumption_rate,
                battery_temperature, battery_health, mode_status, kilometers_driven,
                charging ? "🔌 Cargando" : "🔋 Listo");

        return new VehicleInfo(getId(), getBrand(), getModel(), getYear(), getMaximum_speed(),
                "Vehículo Eléctrico", specificInfo,
                "Conducible + Piloto Automático + Asistencia Emergencias");
    }

    // Getters y Setters
    public double getBattery_capacity() { return battery_capacity; }
    public void setBattery_capacity(double battery_capacity) { this.battery_capacity = battery_capacity; }
    public double getCurrent_charge() { return current_charge; }
    public void setCurrent_charge(double current_charge) {
        this.current_charge = Math.max(0, Math.min(battery_capacity, current_charge));
    }
    public boolean isEmergency_mode() { return emergency_mode; }
    public boolean isAutopilot_active() { return autopilot_active; }
    public int getBattery_temperature() { return battery_temperature; }
    public double getKilometers_driven() { return kilometers_driven; }
    public boolean isCharging() { return charging; }
    public boolean isEngine_running() { return engine_running; }
    public double getBattery_percentage() { return (current_charge / battery_capacity) * 100; }
    public double getRange() { return current_charge / energy_consumption_rate; }
    public boolean isPreconditioning() { return preconditioning; }
}