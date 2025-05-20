package com.innov4africa.api_gateway.model;

import java.util.List;

public class TransactionHistoryResponse {
    private String status;
    private String message;
    private List<BalanceHistoryPoint> data;

    public TransactionHistoryResponse(String status, String message, List<BalanceHistoryPoint> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters et Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<BalanceHistoryPoint> getData() { return data; }
    public void setData(List<BalanceHistoryPoint> data) { this.data = data; }
}

class BalanceHistoryPoint {
    private String date;
    private String solde;
    private String periode;

    public BalanceHistoryPoint(String date, String solde, String periode) {
        this.date = date;
        this.solde = solde;
        this.periode = periode;
    }

    // Getters et Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getSolde() { return solde; }
    public void setSolde(String solde) { this.solde = solde; }
    public String getPeriode() { return periode; }
    public void setPeriode(String periode) { this.periode = periode; }
}
