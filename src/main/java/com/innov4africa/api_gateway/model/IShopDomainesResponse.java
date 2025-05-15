package com.innov4africa.api_gateway.model;

import java.util.List;

public class IShopDomainesResponse {
    private String status;
    private String message;
    private Integer code;
    private List<DomaineResponse> data;

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

    public List<DomaineResponse> getData() {
        return data;
    }

    public void setData(List<DomaineResponse> data) {
        this.data = data;
    }
}
