package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Classe représentant une requête de paiement iShop
 */
@Schema(description = "Requête pour effectuer un paiement via iShop")
public class IShopPaymentRequest {
    
    @Schema(description = "Numéro de référence", example = "REF123456", required = true)
    private String numeros;
    
    @Schema(description = "Montant du paiement", example = "5000", required = true)
    private String montant;
    
    @Schema(description = "Identifiant de la commande", example = "CMD789", required = true)
    private String order;
    
    @Schema(description = "Code de paiement", example = "PAY456", required = true)
    private String code;

    public IShopPaymentRequest() {
    }

    public IShopPaymentRequest(String numeros, String montant, String order, String code) {
        this.numeros = numeros;
        this.montant = montant;
        this.order = order;
        this.code = code;
    }

    public String getNumeros() {
        return numeros;
    }

    public void setNumeros(String numeros) {
        this.numeros = numeros;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}