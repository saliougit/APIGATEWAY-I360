package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Structure de réponse standard pour l'API")
public class ApiResponse<T> {
    
    @Schema(description = "Statut de la réponse (success/error)")
    private String status;
    
    @Schema(description = "Message de succès ou d'erreur")
    private String message;
    
    @Schema(description = "Code de statut HTTP")
    private Integer code;
    
    @Schema(description = "Données de la réponse")
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(String status, String message, Integer code, T data) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", "Opération réussie", 200, data);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>("error", message, code, null);
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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
