package com.example.phonecircle;

public class Phone {
    private String imei;
    private String brand;
    private String model;
    private String color;
    private String ownerId;
    private String status; // "Safe", "Lost", "Stolen"
    private long timestamp;

    public Phone() {
        // Required for Firebase
    }

    public Phone(String imei, String brand, String model, String color, String ownerId) {
        this.imei = imei;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.ownerId = ownerId;
        this.status = "Safe";
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}