package com.innov4africa.api_gateway.model;

import java.util.List;

/**
 * Classe représentant la réponse HTTP pour le solde iBanking
 */
public class IBankingBalanceResponse {
    private String status;
    private String message;
    private String montant;
    private List<ServiceStatus> serviceStatuses;

    public IBankingBalanceResponse() {
    }

    public IBankingBalanceResponse(String status, String message, String montant, List<ServiceStatus> serviceStatuses) {
        this.status = status;
        this.message = message;
        this.montant = montant;
        this.serviceStatuses = serviceStatuses;
    }

    // Getters et setters
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

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public List<ServiceStatus> getServiceStatuses() {
        return serviceStatuses;
    }

    public void setServiceStatuses(List<ServiceStatus> serviceStatuses) {
        this.serviceStatuses = serviceStatuses;
    }
}
