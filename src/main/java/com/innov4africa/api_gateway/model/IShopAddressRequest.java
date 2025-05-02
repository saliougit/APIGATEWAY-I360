package com.innov4africa.api_gateway.model;

public class IShopAddressRequest {
    private Integer user_id;
    private String language;

    public IShopAddressRequest() {}

    public IShopAddressRequest(Integer user_id, String language) {
        this.user_id = user_id;
        this.language = language;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}