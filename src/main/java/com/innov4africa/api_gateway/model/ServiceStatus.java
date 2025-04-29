package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statut détaillé d'un service (iPay, iBanking, etc.)")
public class ServiceStatus {
    
    @Schema(description = "Nom du service", example = "i-banking")
    private String serviceName;
    
    @Schema(description = "Indique si le service est disponible", example = "true")
    private boolean available;
    
    @Schema(description = "Message détaillé sur l'état du service", example = "Service opérationnel")
    private String message;

    public ServiceStatus() {}

    public ServiceStatus(String serviceName, boolean available, String message) {
        this.serviceName = serviceName;
        this.available = available;
        this.message = message;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
