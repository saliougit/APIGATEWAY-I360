package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Vendeur i-shop")
public class IShopSeller {
    private Long id;
    private String name;
    private String prenom;
    private Double avg_rating;
    private CityResponse city;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Double getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(Double avg_rating) {
        this.avg_rating = avg_rating;
    }

    public CityResponse getCity() {
        return city;
    }

    public void setCity(CityResponse city) {
        this.city = city;
    }
}
