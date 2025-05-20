package com.innov4africa.api_gateway.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Classe représentant la réponse HTTP pour le solde global (iPay + iBanking)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)  // Ne pas inclure les champs null dans la réponse JSON
public class GlobalBalanceResponse {
    private String status;
    private String message;
    private String totalMontant;  // Somme des soldes iPay et iBanking
    private String montantIPay;   // Solde iPay
    private String montantIBanking; // Solde iBanking
    private List<ServiceStatus> serviceStatuses;

    public GlobalBalanceResponse() {
    }

    // Constructeur pour les erreurs d'authentification - n'inclut que les champs nécessaires
    public static GlobalBalanceResponse authError(String message, String serviceMessage) {
        GlobalBalanceResponse response = new GlobalBalanceResponse();
        response.setStatus("error");
        response.setMessage(message);
        response.setServiceStatuses(List.of(new ServiceStatus("auth", false, serviceMessage)));
        return response;
    }

    public GlobalBalanceResponse(String status, String message, String totalMontant, 
                               String montantIPay, String montantIBanking,
                               List<ServiceStatus> serviceStatuses) {
        this.status = status;
        this.message = message;
        this.totalMontant = totalMontant;
        this.montantIPay = montantIPay;
        this.montantIBanking = montantIBanking;
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

    public String getTotalMontant() {
        return totalMontant;
    }

    public void setTotalMontant(String totalMontant) {
        this.totalMontant = totalMontant;
    }

    public String getMontantIPay() {
        return montantIPay;
    }

    public void setMontantIPay(String montantIPay) {
        this.montantIPay = montantIPay;
    }

    public String getMontantIBanking() {
        return montantIBanking;
    }

    public void setMontantIBanking(String montantIBanking) {
        this.montantIBanking = montantIBanking;
    }

    public List<ServiceStatus> getServiceStatuses() {
        return serviceStatuses;
    }

    public void setServiceStatuses(List<ServiceStatus> serviceStatuses) {
        this.serviceStatuses = serviceStatuses;
    }
}
