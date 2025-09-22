package org.example.gui;


import org.example.models.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsPanel {
    private VBox mainPanel;
    private PieChart vehicleTypeChart;
    private GridPane statsGrid;
    private Label totalVehiclesLabel;
    private Label electricCountLabel;
    private Label hybridCountLabel;
    private Label combustionCountLabel;
    private Label avgYearLabel;
    private Label avgSpeedLabel;

    public StatisticsPanel() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        mainPanel = new VBox(15);
        mainPanel.getStyleClass().add("panel");
        mainPanel.setPrefHeight(350);

        // Gráfico circular
        vehicleTypeChart = new PieChart();
        vehicleTypeChart.setTitle("Distribución por Tipo de Vehículo");
        vehicleTypeChart.setPrefSize(300, 200);
        vehicleTypeChart.setLegendVisible(true);

        // Etiquetas de estadísticas
        totalVehiclesLabel = new Label("0");
        electricCountLabel = new Label("0");
        hybridCountLabel = new Label("0");
        combustionCountLabel = new Label("0");
        avgYearLabel = new Label("N/A");
        avgSpeedLabel = new Label("N/A");

        // Aplicar estilos a las etiquetas de valores
        totalVehiclesLabel.getStyleClass().add("stat-value");
        electricCountLabel.getStyleClass().add("stat-value-electric");
        hybridCountLabel.getStyleClass().add("stat-value-hybrid");
        combustionCountLabel.getStyleClass().add("stat-value-combustion");
        avgYearLabel.getStyleClass().add("stat-value");
        avgSpeedLabel.getStyleClass().add("stat-value");
    }

    private void setupLayout() {
        Label titleLabel = new Label("📊 Estadísticas de la Flota");
        titleLabel.getStyleClass().add("panel-title");

        // Grid para estadísticas numéricas
        statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(10);
        statsGrid.setPadding(new Insets(10));
        statsGrid.setAlignment(Pos.CENTER);

        setupStatsGrid();

        // Contenedor para el gráfico
        VBox chartContainer = new VBox(5);
        chartContainer.setAlignment(Pos.CENTER);
        chartContainer.getChildren().add(vehicleTypeChart);

        mainPanel.getChildren().addAll(titleLabel, statsGrid, chartContainer);
    }

    private void setupStatsGrid() {
        // Fila 1: Total de vehículos
        Label totalLabel = new Label("Total de Vehículos:");
        totalLabel.getStyleClass().add("stat-label");
        statsGrid.add(totalLabel, 0, 0);
        statsGrid.add(totalVehiclesLabel, 1, 0);

        // Fila 2: Vehículos eléctricos
        Label electricLabel = new Label("🔋 Eléctricos:");
        electricLabel.getStyleClass().add("stat-label");
        statsGrid.add(electricLabel, 0, 1);
        statsGrid.add(electricCountLabel, 1, 1);

        // Fila 3: Vehículos híbridos
        Label hybridLabel = new Label("🔋⛽ Híbridos:");
        hybridLabel.getStyleClass().add("stat-label");
        statsGrid.add(hybridLabel, 0, 2);
        statsGrid.add(hybridCountLabel, 1, 2);

        // Fila 4: Vehículos de combustión
        Label combustionLabel = new Label("⛽ Combustión:");
        combustionLabel.getStyleClass().add("stat-label");
        statsGrid.add(combustionLabel, 0, 3);
        statsGrid.add(combustionCountLabel, 1, 3);

        // Fila 5: Año promedio
        Label avgYearLabelText = new Label("Año Promedio:");
        avgYearLabelText.getStyleClass().add("stat-label");
        statsGrid.add(avgYearLabelText, 2, 0);
        statsGrid.add(avgYearLabel, 3, 0);

        // Fila 6: Velocidad promedio
        Label avgSpeedLabelText = new Label("Velocidad Promedio:");
        avgSpeedLabelText.getStyleClass().add("stat-label");
        statsGrid.add(avgSpeedLabelText, 2, 1);
        statsGrid.add(avgSpeedLabel, 3, 1);

        // Configurar restricciones de columna para mejor distribución
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.SOMETIMES);
        col1.setPrefWidth(120);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.SOMETIMES);
        col2.setPrefWidth(60);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.SOMETIMES);
        col3.setPrefWidth(120);

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setHgrow(Priority.SOMETIMES);
        col4.setPrefWidth(60);

        statsGrid.getColumnConstraints().addAll(col1, col2, col3, col4);
    }

    public void updateStatistics(List<Vehicle> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
            clearStatistics();
            return;
        }

        // Contar por tipo
        long electricCount = vehicles.stream().filter(v -> v instanceof Electric_Vehicle).count();
        long hybridCount = vehicles.stream().filter(v -> v instanceof Hybrid_Vehicle).count();
        long combustionCount = vehicles.stream().filter(v -> v instanceof Combustion_Vehicle).count();

        // Calcular promedios
        double avgYear = vehicles.stream().mapToInt(Vehicle::getYear).average().orElse(0);
        double avgSpeed = vehicles.stream().mapToInt(Vehicle::getMaximum_speed).average().orElse(0);

        // Actualizar etiquetas
        totalVehiclesLabel.setText(String.valueOf(vehicles.size()));
        electricCountLabel.setText(String.valueOf(electricCount));
        hybridCountLabel.setText(String.valueOf(hybridCount));
        combustionCountLabel.setText(String.valueOf(combustionCount));
        avgYearLabel.setText(avgYear > 0 ? String.format("%.0f", avgYear) : "N/A");
        avgSpeedLabel.setText(avgSpeed > 0 ? String.format("%.0f km/h", avgSpeed) : "N/A");

        // Actualizar gráfico circular
        updatePieChart(electricCount, hybridCount, combustionCount);
    }

    private void updatePieChart(long electricCount, long hybridCount, long combustionCount) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        if (electricCount > 0) {
            pieChartData.add(new PieChart.Data("Eléctricos (" + electricCount + ")", electricCount));
        }
        if (hybridCount > 0) {
            pieChartData.add(new PieChart.Data("Híbridos (" + hybridCount + ")", hybridCount));
        }
        if (combustionCount > 0) {
            pieChartData.add(new PieChart.Data("Combustión (" + combustionCount + ")", combustionCount));
        }

        vehicleTypeChart.setData(pieChartData);

        // Aplicar colores personalizados
        vehicleTypeChart.setOnMouseClicked(e -> {
            applyCustomColors();
        });

        // Aplicar colores inmediatamente
        vehicleTypeChart.applyCss();
        applyCustomColors();
    }

    private void applyCustomColors() {
        vehicleTypeChart.getData().forEach(data -> {
            String name = data.getName().toLowerCase();
            if (name.contains("eléctrico")) {
                data.getNode().setStyle("-fx-pie-color: #4CAF50;");
            } else if (name.contains("híbrido")) {
                data.getNode().setStyle("-fx-pie-color: #FF9800;");
            } else if (name.contains("combustión")) {
                data.getNode().setStyle("-fx-pie-color: #f44336;");
            }
        });
    }

    private void clearStatistics() {
        totalVehiclesLabel.setText("0");
        electricCountLabel.setText("0");
        hybridCountLabel.setText("0");
        combustionCountLabel.setText("0");
        avgYearLabel.setText("N/A");
        avgSpeedLabel.setText("N/A");
        vehicleTypeChart.setData(FXCollections.observableArrayList());
    }

    public VBox getPanel() {
        return mainPanel;
    }
}