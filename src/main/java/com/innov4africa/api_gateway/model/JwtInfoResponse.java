package com.innov4africa.api_gateway.model;

public class JwtInfoResponse {
    private String status;
    private String message;
    private String email;
    private String telephone;
    private String userId;
    private boolean isSeller;
    private IShopInfo ishopInfo;

    public JwtInfoResponse() {}

    public JwtInfoResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters et Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public boolean isSeller() { return isSeller; }
    public void setSeller(boolean seller) { isSeller = seller; }
    public IShopInfo getIshopInfo() { return ishopInfo; }
    public void setIshopInfo(IShopInfo ishopInfo) { this.ishopInfo = ishopInfo; }
}