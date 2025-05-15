package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requête pour la liste des produits i-shop")
public class IShopProductRequest {    private Integer user_id;
    private Integer next_offset;
    private String language;

    public IShopProductRequest() {}    public IShopProductRequest(Integer user_id, Integer next_offset, String language) {
        this.user_id = user_id;
        this.next_offset = next_offset;
        this.language = language;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getNext_offset() {
        return next_offset;
    }

    public void setNext_offset(Integer next_offset) {
        this.next_offset = next_offset;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
