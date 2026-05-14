package com.example.phonecircle;

public class Phone {
    private String imei;
    private String brand;
    private String model;
    private String color;
    private String ownerId;
    private String ownerName;
    private String contactInfo;
    private String status;
    private String incidentDescription;
    private String caseNumber;
    private String lastLocation;
    private long timestamp;

    public Phone() {}

    public Phone(String imei, String brand, String model, String color, String ownerId, String ownerName) {
        this.imei = imei;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.status = "safe";
        this.contactInfo = "";
        this.timestamp = System.currentTimeMillis();
    }

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
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIncidentDescription() { return incidentDescription; }
    public void setIncidentDescription(String incidentDescription) { this.incidentDescription = incidentDescription; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    public String getLastLocation() { return lastLocation; }
    public void setLastLocation(String lastLocation) { this.lastLocation = lastLocation; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
