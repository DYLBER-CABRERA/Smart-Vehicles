package org.example.models;

public class VehicleInfo {
    protected String id;
    protected String brand;
    protected String model;
    protected int year;
    protected int maximumSpeed;
    protected String type;
    protected String specificInfo;
    protected String capabilities;

    public VehicleInfo(String id, String brand, String model, int year, int maximumSpeed,
                       String type, String specificInfo, String capabilities) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.maximumSpeed = maximumSpeed;
        this.type = type;
        this.specificInfo = specificInfo;
        this.capabilities = capabilities;
    }

    // Getters
    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public int getMaximumSpeed() { return maximumSpeed; }
    public String getType() { return type; }
    public String getSpecificInfo() { return specificInfo; }
    public String getCapabilities() { return capabilities; }

    @Override
    public String toString() {
        return String.format("ID: %s\nMarca: %s\nModelo: %s\nAño: %d\nVelocidad Máxima: %d km/h\nTipo: %s\n%s\nCapacidades: %s",
                id, brand, model, year, maximumSpeed, type, specificInfo, capabilities);
    }
}
}
