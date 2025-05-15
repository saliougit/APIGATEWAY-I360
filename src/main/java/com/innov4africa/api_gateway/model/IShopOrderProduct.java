package com.innov4africa.api_gateway.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Produit dans une commande i-shop")
public class IShopOrderProduct {
    private int quantite;
    private int quantite_stock;
    private String product_name;
    private long product_id;
    private String is_review;
    private double product_price;
    private double product_amount;
    private String address_rammassage;
    private double product_longitude;
    private double product_latitude;
    private Object seller;
    private int rate;
    private List<String> media;

    // Getters and setters
    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getQuantite_stock() {
        return quantite_stock;
    }

    public void setQuantite_stock(int quantite_stock) {
        this.quantite_stock = quantite_stock;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public String getIs_review() {
        return is_review;
    }

    public void setIs_review(String is_review) {
        this.is_review = is_review;
    }

    public double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public double getProduct_amount() {
        return product_amount;
    }

    public void setProduct_amount(double product_amount) {
        this.product_amount = product_amount;
    }

    public String getAddress_rammassage() {
        return address_rammassage;
    }

    public void setAddress_rammassage(String address_rammassage) {
        this.address_rammassage = address_rammassage;
    }

    public double getProduct_longitude() {
        return product_longitude;
    }

    public void setProduct_longitude(double product_longitude) {
        this.product_longitude = product_longitude;
    }

    public double getProduct_latitude() {
        return product_latitude;
    }

    public void setProduct_latitude(double product_latitude) {
        this.product_latitude = product_latitude;
    }

    public Object getSeller() {
        return seller;
    }

    public void setSeller(Object seller) {
        this.seller = seller;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }
}
