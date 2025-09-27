package org.example.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.util.Duration;
import org.example.models.*;
import org.example.interfaces.*;

public class DriveSimulationDialog extends Dialog<Void> {
    private final Vehicle vehicle;

    // Controles de UI
    private Label speedLabel;
    private Label batteryLabel;
    private Label fuelLabel;
    private Label modeLabel;
    private Label statusLabel;
    private Label temperatureLabel;
    private Label rangeLabel;
    private ProgressBar speedBar;
    private ProgressBar batteryBar;
    private ProgressBar fuelBar;

    // Estado del vehículo
    private int currentSpeed = 0;

    private Button autopilotBtn;
    private Button emergencyBtn;
    private Button chargeBtn;
    private Button refuelBtn;
    private Button changeModeBtn;
    private Button startEngineBtn;
    private Button fastChargeBtn;
    private boolean energyAlertShown = false;
    private Button accelerateBtn;
    private Button brakeBtn;

    public DriveSimulationDialog(Vehicle vehicle) {
        this.vehicle = vehicle;
        initializeDialog();
        createUI();
        setupSimulationTimer();
        updateVehicleSpecificControls();
        updateDisplays();
    }

    private void initializeDialog() {
        setTitle("Simulación de Conducción Avanzada - " + getVehicleTypeString());
        setHeaderText("Vehículo: " + vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getId() + ")");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
    }

    private void createUI() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setPrefWidth(600);

        // Panel de información del vehículo
        VBox infoPanel = createInfoPanel();

        // Panel de indicadores
        VBox indicatorPanel = createIndicatorPanel();

        // Panel de controles
        GridPane controlPanel = createControlPanel();

        // Panel de estado
        VBox statusPanel = createStatusPanel();

        mainContainer.getChildren().addAll(infoPanel, indicatorPanel, controlPanel, statusPanel);
        getDialogPane().setContent(mainContainer);

        // Botones del diálogo
        getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        Button closeButton = (Button) getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setText("Finalizar Simulación");
    }

    private VBox createInfoPanel() {
        VBox panel = new VBox(5);
        panel.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #4169e1; -fx-border-radius: 8; -fx-padding: 10;");

        Label titleLabel = new Label("Información del Vehículo");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        String vehicleType = getVehicleTypeString();
        Label typeLabel = new Label("Tipo: " + vehicleType);
        Label maxSpeedLabel = new Label("Velocidad Máxima: " + vehicle.getMaximum_speed() + " km/h");
        Label capabilitiesLabel = new Label("Capacidades: " + getCapabilitiesString());

        panel.getChildren().addAll(titleLabel, typeLabel, maxSpeedLabel, capabilitiesLabel);
        return panel;
    }

    private VBox createIndicatorPanel() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-padding: 15;");

        Label titleLabel = new Label("Panel de Instrumentos");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Velocímetro
        HBox speedBox = new HBox(10);
        speedBox.setAlignment(Pos.CENTER_LEFT);
        speedLabel = new Label("Velocidad: 0 km/h");
        speedBar = new ProgressBar(0);
        speedBar.setPrefWidth(200);
        speedBox.getChildren().addAll(speedLabel, speedBar);

        // Temperatura (para vehículos de combustión)
        HBox tempBox = new HBox(10);
        tempBox.setAlignment(Pos.CENTER_LEFT);
        temperatureLabel = new Label("Temperatura: 20°C");
        tempBox.getChildren().add(temperatureLabel);

        // Autonomía
        HBox rangeBox = new HBox(10);
        rangeBox.setAlignment(Pos.CENTER_LEFT);
        rangeLabel = new Label("Autonomía: -- km");
        rangeBox.getChildren().add(rangeLabel);

        // Batería (para vehículos eléctricos e híbridos)
        HBox batteryBox = new HBox(10);
        batteryBox.setAlignment(Pos.CENTER_LEFT);
        batteryLabel = new Label("Batería: 100%");
        batteryBar = new ProgressBar(1.0);
        batteryBar.setPrefWidth(200);
        batteryBar.setStyle("-fx-accent: #4caf50;");
        batteryBox.getChildren().addAll(batteryLabel, batteryBar);

        // Combustible (para vehículos de combustión e híbridos)
        HBox fuelBox = new HBox(10);
        fuelBox.setAlignment(Pos.CENTER_LEFT);
        fuelLabel = new Label("Combustible: 100%");
        fuelBar = new ProgressBar(1.0);
        fuelBar.setPrefWidth(200);
        fuelBar.setStyle("-fx-accent: #ff9800;");
        fuelBox.getChildren().addAll(fuelLabel, fuelBar);

        // Modo actual (para híbridos)
        modeLabel = new Label("Modo: Normal");
        modeLabel.setStyle("-fx-font-weight: bold;");

        panel.getChildren().addAll(titleLabel, speedBox, rangeBox);

        // Agregar indicadores específicos según tipo de vehículo
        if (vehicle instanceof Electric_Vehicle || vehicle instanceof Hybrid_Vehicle) {
            panel.getChildren().add(batteryBox);
        }
        if (vehicle instanceof Combustion_Vehicle || vehicle instanceof Hybrid_Vehicle) {
            panel.getChildren().add(fuelBox);
        }
        if (vehicle instanceof Combustion_Vehicle) {
            panel.getChildren().add(tempBox);
        }
        if (vehicle instanceof Hybrid_Vehicle) {
            panel.getChildren().add(modeLabel);
        }

        return panel;
    }

    private GridPane createControlPanel() {
        GridPane panel = new GridPane();
        panel.setHgap(10);
        panel.setVgap(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 8;");

        Label titleLabel = new Label("Controles de Conducción");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        panel.add(titleLabel, 0, 0, 4, 1);

        // Controles básicos
        // Botones de control
        accelerateBtn = new Button("Acelerar");
        accelerateBtn.setPrefWidth(120);
        accelerateBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        accelerateBtn.setOnAction(e -> accelerate());

        brakeBtn = new Button("Frenar");
        brakeBtn.setPrefWidth(120);
        brakeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        brakeBtn.setOnAction(e -> brake());

        // Control específico de motor (combustión)
        startEngineBtn = new Button("Motor");
        startEngineBtn.setPrefWidth(120);
        startEngineBtn.setStyle("-fx-background-color: #795548; -fx-text-fill: white;");
        startEngineBtn.setOnAction(e -> toggleEngine());

        panel.add(accelerateBtn, 0, 1);
        panel.add(brakeBtn, 1, 1);
        panel.add(startEngineBtn, 2, 1);

        // Controles avanzados
        autopilotBtn = new Button("Piloto Automático");
        autopilotBtn.setPrefWidth(140);
        autopilotBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        autopilotBtn.setOnAction(e -> toggleAutopilot());

        emergencyBtn = new Button("Emergencia");
        emergencyBtn.setPrefWidth(140);
        emergencyBtn.setStyle("-fx-background-color: #ff5722; -fx-text-fill: white;");
        emergencyBtn.setOnAction(e -> toggleEmergency());

        panel.add(autopilotBtn, 0, 2);
        panel.add(emergencyBtn, 1, 2);

        // Controles de energía/combustible
        chargeBtn = new Button("Cargar");
        chargeBtn.setPrefWidth(120);
        chargeBtn.setStyle("-fx-background-color: #8bc34a; -fx-text-fill: white;");
        chargeBtn.setOnAction(e -> chargeBattery());

        fastChargeBtn = new Button("Carga Rápida");
        fastChargeBtn.setPrefWidth(120);
        fastChargeBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        fastChargeBtn.setOnAction(e -> fastCharge());

        refuelBtn = new Button("Repostar");
        refuelBtn.setPrefWidth(120);
        refuelBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
        refuelBtn.setOnAction(e -> refuel());

        changeModeBtn = new Button("Cambiar Modo");
        changeModeBtn.setPrefWidth(140);
        changeModeBtn.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white;");
        changeModeBtn.setOnAction(e -> changeMode());

        panel.add(chargeBtn, 0, 3);
        panel.add(fastChargeBtn, 1, 3);
        panel.add(refuelBtn, 2, 3);
        panel.add(changeModeBtn, 3, 3);

        return panel;
    }

    private VBox createStatusPanel() {
        VBox panel = new VBox(5);
        panel.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-padding: 10;");
        panel.setPrefHeight(120);

        Label titleLabel = new Label("Estado de la Simulación");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        statusLabel = new Label("Sistema listo para conducir");
        statusLabel.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane(statusLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(80);

        panel.getChildren().addAll(titleLabel, scrollPane);
        return panel;
    }

    private void updateVehicleSpecificControls() {
        // Habilitar/deshabilitar botones según el tipo de vehículo
        if (!(vehicle instanceof Autonomous)) {
            autopilotBtn.setDisable(true);
            autopilotBtn.setText("No Disponible");
        }

        if (!(vehicle instanceof Autonomous_Advanced)) {
            emergencyBtn.setDisable(true);
            emergencyBtn.setText("No Disponible");
        }

        if (!(vehicle instanceof Electric_Vehicle || vehicle instanceof Hybrid_Vehicle)) {
            chargeBtn.setDisable(true);
            chargeBtn.setText("No Disponible");
            fastChargeBtn.setDisable(true);
            fastChargeBtn.setText("No Disponible");
        }

        if (!(vehicle instanceof Combustion_Vehicle || vehicle instanceof Hybrid_Vehicle)) {
            refuelBtn.setDisable(true);
            refuelBtn.setText("No Disponible");
        }

        if (!(vehicle instanceof Hybrid_Vehicle)) {
            changeModeBtn.setDisable(true);
            changeModeBtn.setText("No Disponible");
        }

        if (!(vehicle instanceof Combustion_Vehicle)) {
            startEngineBtn.setDisable(true);
            startEngineBtn.setText("No Disponible");
        }
    }

    private void setupSimulationTimer() {
        Timeline simulationTimer = new Timeline(new KeyFrame(Duration.seconds(2), e -> updateSimulation()));
        simulationTimer.setCycleCount(Timeline.INDEFINITE);
        simulationTimer.play();
    }

    private void updateSimulation() {
        // Actualizar displays
        updateDisplays();

        // Simulación de conducción si hay velocidad
        if (currentSpeed > 0) {
            if (vehicle instanceof Drivable) {
                String driveResult = ((Drivable) vehicle).toDrive();
                if (driveResult.contains("agotada") || driveResult.contains("Cambio automático")) {
                    appendToStatus(driveResult);
                }
            }
        }
        if (vehicle instanceof Hybrid_Vehicle) {
            Hybrid_Vehicle hv = (Hybrid_Vehicle) vehicle;
            if (hv.getBattery_percentage() <= 0 && hv.getFuel_percentage() <= 0 && !energyAlertShown) {
                energyAlertShown = true;
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Sin energía");
                    alert.setHeaderText(null);
                    alert.setContentText("❌ Sin energía: batería y combustible agotados. Recargue o reposte para continuar.");
                    alert.showAndWait();
                });
            }
        }
        // Alerta para vehículo eléctrico
        else if (vehicle instanceof Electric_Vehicle) {
            Electric_Vehicle ev = (Electric_Vehicle) vehicle;
            if (ev.getBattery_percentage() <= 0 && !energyAlertShown) {
                energyAlertShown = true;
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Sin energía");
                    alert.setHeaderText(null);
                    alert.setContentText("❌ Sin energía: batería agotada. Recargue para continuar.");
                    alert.showAndWait();
                });
            }
        }
        // Alerta para vehículo de combustión
        else if (vehicle instanceof Combustion_Vehicle) {
            Combustion_Vehicle cv = (Combustion_Vehicle) vehicle;
            if (cv.getFuel_percentage() <= 0 && !energyAlertShown) {
                energyAlertShown = true;
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Sin energía");
                    alert.setHeaderText(null);
                    alert.setContentText("❌ Sin energía: combustible agotado. Reposte para continuar.");
                    alert.showAndWait();
                });
            }
        }
    }


    private void updateDisplays() {
        // Actualizar velocímetro
        speedLabel.setText("Velocidad: " + currentSpeed + " km/h");
        double speedPercentage = (double) currentSpeed / vehicle.getMaximum_speed();
        speedBar.setProgress(speedPercentage);

        if (speedPercentage > 0.8) {
            speedBar.setStyle("-fx-accent: #f44336;");
        } else if (speedPercentage > 0.6) {
            speedBar.setStyle("-fx-accent: #ff9800;");
        } else {
            speedBar.setStyle("-fx-accent: #4caf50;");
        }

        // Actualizar indicadores y botón de motor según tipo
        boolean engineOn = false;
        if (vehicle instanceof Electric_Vehicle) {
            Electric_Vehicle ev = (Electric_Vehicle) vehicle;
            double batteryPercent = ev.getBattery_percentage();
            batteryLabel.setText(String.format("Batería: %.1f%%", batteryPercent));
            batteryBar.setProgress(batteryPercent / 100.0);

            if (batteryPercent < 20) {
                batteryBar.setStyle("-fx-accent: #f44336;");
            } else if (batteryPercent < 50) {
                batteryBar.setStyle("-fx-accent: #ff9800;");
            } else {
                batteryBar.setStyle("-fx-accent: #4caf50;");
            }

            rangeLabel.setText(String.format("Autonomía: %.0f km", ev.getRange()));

            // Actualizar botón de motor para eléctricos
            if (ev.isEngine_running()) {
                startEngineBtn.setText("Apagar Motor");
                startEngineBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                engineOn = true;
            } else {
                startEngineBtn.setText("Encender Motor");
                startEngineBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
            }
            startEngineBtn.setDisable(false);
            startEngineBtn.setVisible(true);

        } else if (vehicle instanceof Hybrid_Vehicle) {
            Hybrid_Vehicle hv = (Hybrid_Vehicle) vehicle;
            double batteryPercent = hv.getBattery_percentage();
            double fuelPercent = hv.getFuel_percentage();

            batteryLabel.setText(String.format("Batería: %.1f%%", batteryPercent));
            batteryBar.setProgress(batteryPercent / 100.0);

            fuelLabel.setText(String.format("Combustible: %.1f%%", fuelPercent));
            fuelBar.setProgress(fuelPercent / 100.0);

            modeLabel.setText("Modo: " + hv.getCurrent_mode());
            rangeLabel.setText(String.format("Autonomía: %.0f km", hv.getTotalRange()));

            // Actualizar botón de motor para híbridos
            if (hv.isEngine_running()) {
                startEngineBtn.setText("Apagar Motor");
                startEngineBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                engineOn = true;
            } else {
                startEngineBtn.setText("Encender Motor");
                startEngineBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
            }
            startEngineBtn.setDisable(false);
            startEngineBtn.setVisible(true);

        } else if (vehicle instanceof Combustion_Vehicle) {
            Combustion_Vehicle cv = (Combustion_Vehicle) vehicle;
            double fuelPercent = cv.getFuel_percentage();

            fuelLabel.setText(String.format("Combustible: %.1f%%", fuelPercent));
            fuelBar.setProgress(fuelPercent / 100.0);

            temperatureLabel.setText(String.format("Temperatura: %d°C", cv.getEngine_temperature()));
            rangeLabel.setText(String.format("Autonomía: %.0f km", cv.getRange()));

            // Actualizar botón de motor para combustión
            if (cv.isEngine_running()) {
                startEngineBtn.setText("Apagar Motor");
                startEngineBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                engineOn = true;
            } else {
                startEngineBtn.setText("Encender Motor");
                startEngineBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
            }
            startEngineBtn.setDisable(false);
            startEngineBtn.setVisible(true);
        }

        // Habilitar o deshabilitar los botones de acelerar y frenar según el estado del motor
        // Asegúrate de tener referencias a accelerateBtn y brakeBtn como atributos de la clase
        accelerateBtn.setDisable(!engineOn);
        brakeBtn.setDisable(!engineOn);
    }

    private void accelerate() {
        if (currentSpeed < vehicle.getMaximum_speed()) {
            int increment = 15;
            currentSpeed = Math.min(currentSpeed + increment, vehicle.getMaximum_speed());
            vehicle.accelerate();
            appendToStatus("Acelerando a " + currentSpeed + " km/h");
        }
    }

    private void brake() {
        if (currentSpeed > 0) {
            int decrement = 20;
            currentSpeed = Math.max(0, currentSpeed - decrement);
            vehicle.brake();
            appendToStatus("Frenando a " + currentSpeed + " km/h");
        }
    }

    private void toggleEngine() {
        String result = "";
        if (vehicle instanceof Combustion_Vehicle) {
            Combustion_Vehicle cv = (Combustion_Vehicle) vehicle;
            if (cv.isEngine_running()) {
                result = cv.stopEngine();
                currentSpeed = 0;
            } else {
                result = cv.startEngine();
            }
        } else if (vehicle instanceof Electric_Vehicle) {
            Electric_Vehicle ev = (Electric_Vehicle) vehicle;
            if (ev.isEngine_running()) {
                result = ev.stopEngine();
                currentSpeed = 0;
            } else {
                result = ev.startEngine();
            }
        } else if (vehicle instanceof Hybrid_Vehicle) {
            Hybrid_Vehicle hv = (Hybrid_Vehicle) vehicle;
            if (hv.isEngine_running()) {
                result = hv.stopEngine();
                currentSpeed = 0;
            } else {
                result = hv.startEngine();
            }
        }
        appendToStatus(result);
        updateDisplays(); // Asegura que el botón se actualice tras el cambio de estado
    }

    private void toggleAutopilot() {
        if (vehicle instanceof Autonomous) {
            String result = ((Autonomous) vehicle).autoPilot();
            appendToStatus(result);

            // Actualizar botón
            if (result.contains("ACTIVADO")) {
                autopilotBtn.setText("Desactivar Piloto");
                autopilotBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
            } else {
                autopilotBtn.setText("Piloto Automático");
                autopilotBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
            }
        }
    }

    private void toggleEmergency() {
        if (vehicle instanceof Autonomous_Advanced) {
            Electric_Vehicle ev = (Electric_Vehicle) vehicle;
            String result;

            if (ev.isEmergency_mode()) {
                result = ev.deactivate_emergency();
                emergencyBtn.setText("Emergencia");
                emergencyBtn.setStyle("-fx-background-color: #ff5722; -fx-text-fill: white;");
            } else {
                result = ev.emergencyAssistance();
                emergencyBtn.setText("Desactivar Emergencia");
                emergencyBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
                currentSpeed = 0; // Detener en emergencia
            }
            appendToStatus(result);
        }
    }


// Metodo para carga rapida
    private void fastCharge() {
        if (vehicle instanceof Electric_Vehicle) {
            String result = ((Electric_Vehicle) vehicle).startFastCharging();
            appendToStatus(result);
        }else if (vehicle instanceof Hybrid_Vehicle) {
            String result = ((Hybrid_Vehicle) vehicle).startFastCharging();
            appendToStatus(result);

        }
    }

    private void refuel() {
        String result = "";
        if (vehicle instanceof Combustion_Vehicle) {
            result = ((Combustion_Vehicle) vehicle).bunker();
            Combustion_Vehicle cv = (Combustion_Vehicle) vehicle;
            if (cv.getFuel_percentage() > 0) {
                energyAlertShown = false;
            }
        } else if (vehicle instanceof Hybrid_Vehicle) {
            result = ((Hybrid_Vehicle) vehicle).refuel();
            Hybrid_Vehicle hv = (Hybrid_Vehicle) vehicle;
            if (hv.getBattery_percentage() > 0 || hv.getFuel_percentage() > 0) {
                energyAlertShown = false;
            }
        }
        appendToStatus(result);
    }

    private void changeMode() {
        if (vehicle instanceof Hybrid_Vehicle) {
            String result = ((Hybrid_Vehicle) vehicle).change_operating_mode();
            appendToStatus(result);
        }
    }

    private void appendToStatus(String text) {
        String currentText = statusLabel.getText();
        String newText = java.time.LocalTime.now().toString().substring(0, 8) + " - " + text + "\n" + currentText;

        // Limitar a las últimas 5 líneas para evitar overflow
        String[] lines = newText.split("\n");
        if (lines.length > 5) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                sb.append(lines[i]).append("\n");
            }
            newText = sb.toString();
        }

        statusLabel.setText(newText);
    }

    private String getVehicleTypeString() {
        if (vehicle instanceof Electric_Vehicle) return "Vehículo Eléctrico";
        if (vehicle instanceof Hybrid_Vehicle) return "Vehículo Híbrido";
        if (vehicle instanceof Combustion_Vehicle) return "Vehículo de Combustión";
        return "Vehículo";
    }

    private String getCapabilitiesString() {
        StringBuilder capabilities = new StringBuilder();
        capabilities.append("Conducible");

        if (vehicle instanceof Autonomous) {
            capabilities.append(" + Piloto Automático");
        }
        if (vehicle instanceof Autonomous_Advanced) {
            capabilities.append(" + Asistencia de Emergencias");
        }

        return capabilities.toString();
    }



    private void chargeBattery() {
        String result = "";
        if (vehicle instanceof Electric_Vehicle) {
            result = ((Electric_Vehicle) vehicle).battery_charging();
            Electric_Vehicle ev = (Electric_Vehicle) vehicle;
            if (ev.getBattery_percentage() > 0) {
                energyAlertShown = false;
            }
        } else if (vehicle instanceof Hybrid_Vehicle) {
            result = ((Hybrid_Vehicle) vehicle).chargeBattery();
            Hybrid_Vehicle hv = (Hybrid_Vehicle) vehicle;
            if (hv.getBattery_percentage() > 0 || hv.getFuel_percentage() > 0) {
                energyAlertShown = false;
            }
        }
        appendToStatus(result);
    }



}