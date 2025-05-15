package com.innov4africa.api_gateway.model;

import java.util.List;

public class IShopCategoriesResponse {
    private String status;
    private String message;
    private Integer code;
    private List<CategorieResponse> data;

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

    public List<CategorieResponse> getData() {
        return data;
    }

    public void setData(List<CategorieResponse> data) {
        this.data = data;
    }
}
