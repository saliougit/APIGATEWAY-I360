package com.innov4africa.api_gateway.model;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse de la liste des produits i-shop")
public class IShopProductResponse {
    private String status;
    private String message;
    private Integer code;
    private PaginationMetadata pagination;
    private List<IShopProduct> result;

    public IShopProductResponse() {}

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

    public PaginationMetadata getPagination() {
        return pagination;
    }

    public void setPagination(PaginationMetadata pagination) {
        this.pagination = pagination;
    }

    public List<IShopProduct> getResult() {
        return result;
    }

    public void setResult(List<IShopProduct> result) {
        this.result = result;
    }
}
