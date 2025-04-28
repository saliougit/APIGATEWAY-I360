package com.innov4africa.api_gateway.model;

public class IBankingUserCheckResponse {
    private String status;
    private String message;
    private boolean exists;
    private ServiceStatus serviceStatus;

    public IBankingUserCheckResponse() {}

    public IBankingUserCheckResponse(String status, String message, boolean exists) {
        this.status = status;
        this.message = message;
        this.exists = exists;
        this.serviceStatus = new ServiceStatus("i-banking", exists, message);
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }
}