package com.innov4africa.api_gateway.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Classe représentant la réponse HTTP pour un paiement iShop
 */
@Schema(description = "Réponse d'un paiement iShop")
public class IShopPaymentResponse {
    
    @Schema(description = "Statut de la transaction (success, error)", example = "success")
    private String status;
    
    @Schema(description = "Message détaillé de la transaction", example = "Paiement effectué avec succès")
    private String message;
    
    @Schema(description = "Référence unique de la transaction", example = "ISHP123456789")
    private String reference;
    
    @Schema(description = "État des services impliqués dans la transaction")
    private List<ServiceStatus> services;

    public IShopPaymentResponse() {
    }

    public IShopPaymentResponse(String status, String message, String reference, List<ServiceStatus> services) {
        this.status = status;
        this.message = message;
        this.reference = reference;
        this.services = services;
    }

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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<ServiceStatus> getServices() {
        return services;
    }

    public void setServices(List<ServiceStatus> services) {
        this.services = services;
    }
}