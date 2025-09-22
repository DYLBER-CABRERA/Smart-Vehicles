package org.example.gui;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.models.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import java.util.Optional;

public class VehicleFormDialog extends Dialog<Vehicle> {
    private String vehicleType;
    private TextField idField;
    private ComboBox<String> brandComboBox;
    private TextField modelField;
    private Spinner<Integer> yearSpinner;
    private Spinner<Integer> speedSpinner;
    private TextField specificField; // Para capacidad batería, eficiencia o capacidad tanque

    public VehicleFormDialog(String vehicleType) {
        this.vehicleType = vehicleType;
        initDialog();
        createForm();
        setupButtons();
        setupResultConverter();
    }

    private void initDialog() {
        setTitle("Agregar " + getVehicleTypeName());
        setHeaderText("Complete los datos del nuevo vehículo " + vehicleType.toLowerCase());
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);

        // Configurar ícono según tipo
        String icon = getVehicleIcon();
        if (!icon.isEmpty()) {
            setGraphic(new Label(icon));
        }
    }

    private void createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Campos comunes
        idField = new TextField();
        idField.setPromptText("Ej: EV001, HV001, CV001");

        ObservableList<String> marcas = FXCollections.observableArrayList(
                "Tesla", "Nissan", "Toyota", "Honda", "Ford", "Chevrolet", "BMW", "Audi", "Mercedes-Benz", "Volkswagen", "Hyundai", "Kia", "Mazda", "Subaru", "Jeep"
        );
        brandComboBox = new ComboBox<>(marcas);
        brandComboBox.setPromptText("Seleccione una marca");
        brandComboBox.setEditable(true);

        modelField = new TextField();
        modelField.setPromptText("Ej: Model S, Prius, Mustang");

        yearSpinner = new Spinner<>(1990, 2025, 2023);
        yearSpinner.setEditable(true);
        yearSpinner.setPrefWidth(120);

        speedSpinner = new Spinner<>(80, 400, 180);
        speedSpinner.setEditable(true);
        speedSpinner.setPrefWidth(120);

        // Campo específico según tipo
        specificField = new TextField();
        String specificLabel = getSpecificFieldLabel();
        specificField.setPromptText(getSpecificFieldPrompt());

        // Agregar campos al grid
        grid.add(new Label("ID del Vehículo:"), 0, 0);
        grid.add(idField, 1, 0);

        grid.add(new Label("Marca:"), 0, 1);
        grid.add(brandComboBox, 1, 1);

        grid.add(new Label("Modelo:"), 0, 2);
        grid.add(modelField, 1, 2);

        grid.add(new Label("Año:"), 0, 3);
        grid.add(yearSpinner, 1, 3);

        grid.add(new Label("Velocidad Máxima (km/h):"), 0, 4);
        grid.add(speedSpinner, 1, 4);

        grid.add(new Label(specificLabel), 0, 5);
        grid.add(specificField, 1, 5);

        // Información adicional según el tipo
        VBox infoBox = createInfoBox();

        VBox content = new VBox(10);
        content.getChildren().addAll(grid, infoBox);

        getDialogPane().setContent(content);
    }

    private VBox createInfoBox() {
        VBox infoBox = new VBox(5);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #87ceeb; -fx-border-radius: 5;");

        Label titleLabel = new Label("ℹ️ Información del Tipo de Vehículo");
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label capabilitiesLabel = new Label("Capacidades: " + getCapabilitiesDescription());
        Label featuresLabel = new Label("Características: " + getFeaturesDescription());

        infoBox.getChildren().addAll(titleLabel, capabilitiesLabel, featuresLabel);
        return infoBox;
    }

    private void setupButtons() {
        ButtonType createButtonType = new ButtonType("Crear Vehículo", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        Button createButton = (Button) getDialogPane().lookupButton(createButtonType);
        createButton.setDefaultButton(true);

        // Validación en tiempo real
        createButton.disableProperty().bind(
                idField.textProperty().isEmpty()
                        .or(brandComboBox.valueProperty().isNull()
                                .or(brandComboBox.getEditor().textProperty().isEmpty()))
                        .or(modelField.textProperty().isEmpty())
                        .or(specificField.textProperty().isEmpty())
        );

        // Estilos para los botones
        createButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        Button cancelButton = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
    }

    private void setupResultConverter() {
        setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return createVehicle();
            }
            return null;
        });
    }

    private Vehicle createVehicle() {
        try {
            String id = idField.getText().trim();
            String brand = brandComboBox.getValue() != null ?
                    brandComboBox.getValue() :
                    brandComboBox.getEditor().getText().trim();
            String model = modelField.getText().trim();
            int year = yearSpinner.getValue();
            int maxSpeed = speedSpinner.getValue();
            double specificValue = Double.parseDouble(specificField.getText().trim());

            switch (vehicleType) {
                case "Electric":
                    return new Electric_Vehicle(id, brand, model, year, maxSpeed, specificValue);
                case "Hybrid":
                    return new Hybrid_Vehicle(id, brand, model, year, maxSpeed, specificValue);
                case "Combustion":
                    return new Combustion_Vehicle(id, brand, model, year, maxSpeed, specificValue);
                default:
                    throw new IllegalArgumentException("Tipo de vehículo no válido: " + vehicleType);
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Error de formato", "El valor específico debe ser un número válido.");
            return null;
        } catch (Exception e) {
            showErrorDialog("Error", "Error al crear el vehículo: " + e.getMessage());
            return null;
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getVehicleTypeName() {
        switch (vehicleType) {
            case "Electric": return "Vehículo Eléctrico";
            case "Hybrid": return "Vehículo Híbrido";
            case "Combustion": return "Vehículo de Combustión";
            default: return "Vehículo";
        }
    }

    private String getVehicleIcon() {
        switch (vehicleType) {
            case "Electric": return "🔋";
            case "Hybrid": return "🔋⛽";
            case "Combustion": return "⛽";
            default: return "🚗";
        }
    }

    private String getSpecificFieldLabel() {
        switch (vehicleType) {
            case "Electric": return "Capacidad de Batería (kWh):";
            case "Hybrid": return "Eficiencia Energética (km/l):";
            case "Combustion": return "Capacidad del Tanque (L):";
            default: return "Valor Específico:";
        }
    }

    private String getSpecificFieldPrompt() {
        switch (vehicleType) {
            case "Electric": return "Ej: 75.0, 100.0";
            case "Hybrid": return "Ej: 25.5, 30.2";
            case "Combustion": return "Ej: 60.0, 80.0";
            default: return "Ingrese valor";
        }
    }

    private String getCapabilitiesDescription() {
        switch (vehicleType) {
            case "Electric":
                return "Conducible + Piloto Automático + Asistencia de Emergencias";
            case "Hybrid":
                return "Conducible + Piloto Automático";
            case "Combustion":
                return "Solo Conducible";
            default:
                return "Desconocido";
        }
    }

    private String getFeaturesDescription() {
        switch (vehicleType) {
            case "Electric":
                return "Motor eléctrico silencioso, batería recargable, tecnología avanzada";
            case "Hybrid":
                return "Combinación de motor eléctrico y combustión, eficiencia optimizada";
            case "Combustion":
                return "Motor de combustión interna, tanque de combustible";
            default:
                return "Características básicas";
        }
    }
}