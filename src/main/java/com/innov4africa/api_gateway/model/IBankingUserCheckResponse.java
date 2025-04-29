package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse pour les opérations de vérification et création d'utilisateur iBanking")
public class IBankingUserCheckResponse {
    
    @Schema(description = "Statut de l'opération (success, error, not_found)", example = "success")
    private String status;
    
    @Schema(description = "Message détaillé de l'opération", example = "Utilisateur trouvé dans iBanking")
    private String message;
    
    @Schema(description = "Indique si l'utilisateur existe ou si l'opération a réussi", example = "true")
    private boolean exists;
    
    @Schema(description = "Informations détaillées sur le statut du service")
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