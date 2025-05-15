package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Media d'un produit i-shop")
public class IShopMedia {
    private Long media_id;
    private String url;

    public Long getMedia_id() {
        return media_id;
    }

    public void setMedia_id(Long media_id) {
        this.media_id = media_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
