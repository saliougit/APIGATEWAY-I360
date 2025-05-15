package com.innov4africa.api_gateway.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse pour la liste des commandes i-shop")
public class IShopOrderResponse {
    private String status;
    private String message;
    private List<IShopOrder> data;
    private Integer code;

    // Getters and setters
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

    public List<IShopOrder> getData() {
        return data;
    }

    public void setData(List<IShopOrder> data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
