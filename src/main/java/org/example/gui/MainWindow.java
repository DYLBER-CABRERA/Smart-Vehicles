package org.example.gui;


import org.example.models.*;
import org.example.interfaces.*;
import org.example.services.FleetManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainWindow extends Application {
    private FleetManager fleetManager;
    private TableView<VehicleInfo> vehicleTable;
    private ObservableList<VehicleInfo> vehicleData;
    private TextArea resultArea;
    private StatisticsPanel statisticsPanel;

    @Override
    public void start(Stage primaryStage) {
        // Inicializar el gestor de flota
        // Suprimir todas las advertencias de CSS de JavaFX
        java.util.logging.Logger cssLogger = java.util.logging.Logger.getLogger("javafx.scene.CssStyleHelper");
        cssLogger.setLevel(java.util.logging.Level.OFF);

// También suprimir advertencias relacionadas
        java.util.logging.Logger.getLogger("javafx.css").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("com.sun.javafx.css").setLevel(java.util.logging.Level.OFF);
        fleetManager = new FleetManager();
        vehicleData = FXCollections.observableArrayList();

        // Configurar la ventana principal
        primaryStage.setTitle("SmartDrive - Sistema de Gestión de Vehículos Inteligentes");
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);

        // Crear la interfaz
        VBox root = createMainLayout();

        // Cargar algunos vehículos de ejemplo
        loadSampleVehicles();

        Scene scene = new Scene(root, 1200, 800);

        // Código de diagnóstico - agregar antes de cargar el CSS
        System.out.println("Intentando cargar CSS desde: " + getClass().getResource("/"));
        System.out.println("Recursos disponibles:");
        try {
            scene.getStylesheets().add(getClass().getResource("/org/example/styles.css").toExternalForm());

            System.out.println("CSS cargado correctamente");
        } catch (Exception e) {
            System.out.println("Warning: Error cargando CSS - " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Maximiza la ventana al iniciar
        primaryStage.show();
    }

    private VBox createMainLayout() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getStyleClass().add("main-container");

        // Header
        Label titleLabel = new Label("🚗 SmartDrive Fleet Management System");
        titleLabel.getStyleClass().add("title");

        // Toolbar
        HBox toolbar = createToolbar();

        // Contenido principal
        HBox mainContent = new HBox(15);

        // Panel izquierdo - Lista de vehículos
        VBox leftPanel = createVehicleListPanel();
        leftPanel.setPrefWidth(600);

        // Panel derecho - Detalles y estadísticas
        VBox rightPanel = createRightPanel();
        rightPanel.setPrefWidth(550);

        mainContent.getChildren().addAll(leftPanel, rightPanel);

        // Área de resultados
        VBox resultPanel = createResultPanel();


        root.getChildren().addAll(titleLabel, toolbar, mainContent, resultPanel);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        return root;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar");

        Button addElectricBtn = new Button("➕ Vehículo Eléctrico");
        addElectricBtn.getStyleClass().add("electric-btn");
        addElectricBtn.setOnAction(e -> openVehicleDialog("Electric"));

        Button addHybridBtn = new Button("➕ Vehículo Híbrido");
        addHybridBtn.getStyleClass().add("hybrid-btn");
        addHybridBtn.setOnAction(e -> openVehicleDialog("Hybrid"));

        Button addCombustionBtn = new Button("➕ Vehículo Combustión");
        addCombustionBtn.getStyleClass().add("combustion-btn");
        addCombustionBtn.setOnAction(e -> openVehicleDialog("Combustion"));

        Button UpdateBtn = new Button("🔄 Actualizar vehiculo");
        UpdateBtn.getStyleClass().add("update-btn");
        UpdateBtn.setOnAction(e -> UpdateVehicle());

        Button removeBtn = new Button("🗑️ Eliminar");
        removeBtn.getStyleClass().add("remove-btn");
        removeBtn.setOnAction(e -> removeSelectedVehicle());

        Button clearBtn = new Button("Limpiar Resultados");
        clearBtn.getStyleClass().add("clear-btn");
        clearBtn.setOnAction(e -> resultArea.clear());

        toolbar.getChildren().addAll(
                addElectricBtn, addHybridBtn, addCombustionBtn,
                new Separator(), removeBtn, UpdateBtn, new Separator(), clearBtn
        );

        return toolbar;
    }

    private VBox createVehicleListPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("panel");

        Label titleLabel = new Label("🚗 Flota de Vehículos");
        titleLabel.getStyleClass().add("panel-title");

        // Crear tabla
        vehicleTable = new TableView<>();
        vehicleTable.setItems(vehicleData);
        vehicleTable.getStyleClass().add("vehicle-table");

        // Configurar columnas
        TableColumn<VehicleInfo, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<VehicleInfo, String> brandCol = new TableColumn<>("Marca");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        brandCol.setPrefWidth(100);

        TableColumn<VehicleInfo, String> modelCol = new TableColumn<>("Modelo");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        modelCol.setPrefWidth(120);

        TableColumn<VehicleInfo, Integer> yearCol = new TableColumn<>("Año");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        yearCol.setPrefWidth(70);

        TableColumn<VehicleInfo, String> typeCol = new TableColumn<>("Tipo");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(150);

        TableColumn<VehicleInfo, Integer> speedCol = new TableColumn<>("Vel. Max");
        speedCol.setCellValueFactory(new PropertyValueFactory<>("maximumSpeed"));
        speedCol.setPrefWidth(80);

        vehicleTable.getColumns().addAll(idCol, brandCol, modelCol, yearCol, typeCol, speedCol);

        // Listener para selección
        vehicleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showVehicleDetails(newSelection);
                    }
                }
        );

        panel.getChildren().addAll(titleLabel, vehicleTable);
        VBox.setVgrow(vehicleTable, Priority.ALWAYS);

        return panel;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(15);

        // Panel de estadísticas
        statisticsPanel = new StatisticsPanel();

        // Panel de acciones
        VBox actionPanel = createActionPanel();

        panel.getChildren().addAll(statisticsPanel.getPanel(), actionPanel);

        return panel;
    }

    private VBox createActionPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("panel");
        panel.setPrefHeight(250);

        Label titleLabel = new Label("🎮 Acciones del Vehículo");
        titleLabel.getStyleClass().add("panel-title");

        // Botones de acción
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);

        Button driveBtn = new Button("🚗 Iniciar Conducción");
        driveBtn.getStyleClass().add("action-btn");
        driveBtn.setOnAction(e -> performAction("drive"));

        Button accelerateBtn = new Button("⚡ Acelerar");
        accelerateBtn.getStyleClass().add("action-btn");
        accelerateBtn.setOnAction(e -> performAction("accelerate"));

        Button brakeBtn = new Button("🛑 Frenar");
        brakeBtn.getStyleClass().add("action-btn");
        brakeBtn.setOnAction(e -> performAction("brake"));

        actionButtons.getChildren().addAll(driveBtn);
                //accelerateBtn, brakeBtn);

        // Botones específicos según tipo
        HBox specificButtons = new HBox(10);
        specificButtons.setAlignment(Pos.CENTER);

        Button autopilotBtn = new Button("🤖 Piloto Automático");
        autopilotBtn.getStyleClass().add("autopilot-btn");
        autopilotBtn.setOnAction(e -> performAction("autopilot"));

        Button emergencyBtn = new Button("🚨 Emergencia");
        emergencyBtn.getStyleClass().add("emergency-btn");
        emergencyBtn.setOnAction(e -> performAction("emergency"));

        Button chargeBtn = new Button("🔋 Cargar");
        chargeBtn.getStyleClass().add("charge-btn");
        chargeBtn.setOnAction(e -> performAction("charge"));

        Button fuelBtn = new Button("⛽ Repostar");
        fuelBtn.getStyleClass().add("fuel-btn");
        fuelBtn.setOnAction(e -> performAction("fuel"));

        Button modeBtn = new Button("🔄 Cambiar Modo");
        modeBtn.getStyleClass().add("mode-btn");
        modeBtn.setOnAction(e -> performAction("mode"));

        //specificButtons.getChildren().addAll(autopilotBtn, emergencyBtn, chargeBtn, fuelBtn, modeBtn);

        panel.getChildren().addAll(titleLabel, actionButtons, specificButtons);

        return panel;
    }

    private VBox createResultPanel() {
        VBox panel = new VBox(5);
        panel.getStyleClass().add("panel");
        panel.setPrefHeight(250); // Cambiar de 200 a 250 o más
        panel.setMinHeight(200);  // Agregar altura mínima

        Label titleLabel = new Label("📋 Resultados de Acciones");
        titleLabel.getStyleClass().add("panel-title");

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(10);
        resultArea.getStyleClass().add("result-area");

        ScrollPane scrollPane = new ScrollPane(resultArea);
        scrollPane.setFitToWidth(true);

        panel.getChildren().addAll(titleLabel, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return panel;
    }

    private void openVehicleDialog(String type) {
        VehicleFormDialog dialog = new VehicleFormDialog(type);
        dialog.showAndWait().ifPresent(vehicle -> {
            fleetManager.addVehicle(vehicle);
            refreshVehicleList();
            refreshStatistics();
            appendToResults("✅ Vehículo agregado: " + vehicle.getBrand() + " " + vehicle.getModel());
        });
    }


    // En MainWindow.java
    private void UpdateVehicle() {
        VehicleInfo selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Vehicle vehicle = fleetManager.getVehicleById(selected.getId());
            if (vehicle != null) {
                String type = "";
                if (vehicle instanceof Electric_Vehicle) type = "Electric";
                else if (vehicle instanceof Hybrid_Vehicle) type = "Hybrid";
                else if (vehicle instanceof Combustion_Vehicle) type = "Combustion";

                VehicleFormDialog dialog = new VehicleFormDialog(type, vehicle);
                dialog.showAndWait().ifPresent(updatedVehicle -> {
                    fleetManager.updateVehicle(selected.getId(), updatedVehicle);
                    refreshVehicleList();
                    refreshStatistics();
                    appendToResults("✅ Vehículo actualizado: " + updatedVehicle.getBrand() + " " + updatedVehicle.getModel());
                });
            }
        } else {
            appendToResults("⚠️ Seleccione un vehículo para actualizar");
        }
    }

    private void removeSelectedVehicle() {
        VehicleInfo selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Eliminar vehículo?");
            alert.setContentText("¿Está seguro de que desea eliminar " + selected.getBrand() + " " + selected.getModel() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    fleetManager.removeVehicle(selected.getId());
                    refreshVehicleList();
                    refreshStatistics();
                    appendToResults("🗑️ Vehículo eliminado: " + selected.getBrand() + " " + selected.getModel());
                }
            });
        }
    }

    private void performAction(String action) {
        VehicleInfo selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            appendToResults("⚠️ Seleccione un vehículo primero");
            return;
        }

        Vehicle vehicle = fleetManager.getVehicleById(selected.getId());
        if (vehicle == null) {
            appendToResults("❌ Error: Vehículo no encontrado");
            return;
        }

        String result = "";
        try {
            switch (action) {
                case "drive":
                    if (vehicle instanceof Drivable) {
                        DriveSimulationDialog simDialog = new DriveSimulationDialog(vehicle);
                        simDialog.showAndWait();
                        result = "🚗 Simulación de conducción finalizada para " + vehicle.getBrand() + " " + vehicle.getModel();
                    } else{
                        result = "❌ Este vehículo no puede conducir";
                    }
                    break;

                case "accelerate":
                    vehicle.accelerate();
                    result = "⚡ " + vehicle.getBrand() + " " + vehicle.getModel() + " está acelerando...";
                    break;
                case "brake":
                    vehicle.brake();
                    result = "🛑 " + vehicle.getBrand() + " " + vehicle.getModel() + " está frenando...";
                    break;
                case "autopilot":
                    if (vehicle instanceof Autonomous) {
                        result = ((Autonomous) vehicle).autoPilot();
                    } else {
                        result = "❌ Este vehículo no tiene piloto automático";
                    }
                    break;
                case "emergency":
                    if (vehicle instanceof Autonomous_Advanced) {
                        result = ((Autonomous_Advanced) vehicle).emergencyAssistance();
                    } else {
                        result = "❌ Este vehículo no tiene asistencia de emergencia";
                    }
                    break;
                case "charge":
                    if (vehicle instanceof Electric_Vehicle) {
                        result = ((Electric_Vehicle) vehicle).battery_charging();
                    } else {
                        result = "❌ Solo los vehículos eléctricos pueden cargar batería";
                    }
                    break;
                case "fuel":
                    if (vehicle instanceof Combustion_Vehicle) {
                        result = ((Combustion_Vehicle) vehicle).bunker();
                    } else {
                        result = "❌ Solo los vehículos de combustión pueden repostar";
                    }
                    break;
                case "mode":
                    if (vehicle instanceof Hybrid_Vehicle) {
                        result = ((Hybrid_Vehicle) vehicle).change_operating_mode();
                    } else {
                        result = "❌ Solo los vehículos híbridos pueden cambiar modo";
                    }
                    break;
                default:
                    result = "❌ Acción no reconocida";
            }
        } catch (Exception e) {
            result = "❌ Error ejecutando acción: " + e.getMessage();
        }

        appendToResults(result);
        refreshVehicleList(); // Actualizar datos que pueden haber cambiado
    }

    private void showVehicleDetails(VehicleInfo vehicleInfo) {
        Vehicle vehicle = fleetManager.getVehicleById(vehicleInfo.getId());
        if (vehicle != null) {
            VehicleInfo info = vehicle.display_general_information();
            appendToResults("📋 INFORMACIÓN DETALLADA:\n" + info.toString() + "\n" + "=".repeat(50));
        }
    }

    private void refreshVehicleList() {
        vehicleData.clear();
        for (Vehicle vehicle : fleetManager.getAllVehicles()) {
            vehicleData.add(vehicle.display_general_information());
        }
    }

    private void refreshStatistics() {
        if (statisticsPanel != null) {
            statisticsPanel.updateStatistics(fleetManager.getAllVehicles());
        }
    }

    private void appendToResults(String text) {
        resultArea.appendText(java.time.LocalTime.now().toString().substring(0, 8) + " - " + text + "\n");
    }

    private void loadSampleVehicles() {
        // Vehículos eléctricos
        fleetManager.addVehicle(new Electric_Vehicle("EV001", "Tesla", "Model S", 2023, 250, 100.0));
        fleetManager.addVehicle(new Electric_Vehicle("EV002", "Nissan", "Leaf", 2022, 150, 62.0));

        // Vehículos híbridos
        fleetManager.addVehicle(new Hybrid_Vehicle("HV001", "Toyota", "Prius", 2021, 180, 25.5));
        fleetManager.addVehicle(new Hybrid_Vehicle("HV002", "Honda", "Insight", 2020, 170, 22.8));

        // Vehículos de combustión
        fleetManager.addVehicle(new Combustion_Vehicle("CV001", "Ford", "Mustang", 2022, 280, 61.0));
        fleetManager.addVehicle(new Combustion_Vehicle("CV002", "Chevrolet", "Camaro", 2021, 275, 65.0));

        refreshVehicleList();
        refreshStatistics();

        appendToResults("🚗 Sistema SmartDrive iniciado con " + fleetManager.getAllVehicles().size() + " vehículos de ejemplo");
    }

    public static void main(String[] args) {
        java.util.logging.Logger.getGlobal().setLevel(java.util.logging.Level.SEVERE);
        launch(args);
    }
}