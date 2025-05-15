package com.innov4africa.api_gateway.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Commande i-shop")
public class IShopOrder {
    private List<List<IShopOrderProduct>> product_list;
    private double order_amount;
    private String order_status;
    private String order_date;
    private long order_id;
    private String datedebut;
    private String datefin;
    private String dateprevue;

    // Getters and setters
    public List<List<IShopOrderProduct>> getProduct_list() {
        return product_list;
    }

    public void setProduct_list(List<List<IShopOrderProduct>> product_list) {
        this.product_list = product_list;
    }

    public double getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(double order_amount) {
        this.order_amount = order_amount;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public String getDatedebut() {
        return datedebut;
    }

    public void setDatedebut(String datedebut) {
        this.datedebut = datedebut;
    }

    public String getDatefin() {
        return datefin;
    }

    public void setDatefin(String datefin) {
        this.datefin = datefin;
    }

    public String getDateprevue() {
        return dateprevue;
    }

    public void setDateprevue(String dateprevue) {
        this.dateprevue = dateprevue;
    }
}
