package com.innov4africa.api_gateway.model;

import java.util.List;

public class IShopInfo {
    private Integer user_id;
    private String name;
    private String prenom;
    private String email_id;
    private Boolean is_email_verified;
    private String mobile_no;
    private Boolean is_mobile_verified;
    private String image;
    private String user_type;
    private String seller_type;
    private Double avg_rating;
    private Integer fidelity_point;
    private Integer credit;
    private Integer count_notification;
    private List<DomaineResponse> domaineList;
    private Integer total_reviews;
    private String preferred_language_code;
    private String preferred_currency;
    private CityResponse citie;

    public IShopInfo() {}

    // Constructeur avec tous les champs
    public IShopInfo(Integer user_id, String name, String prenom, String email_id, Boolean is_email_verified,
                     String mobile_no, Boolean is_mobile_verified, String image, String user_type,
                     String seller_type, Double avg_rating, Integer fidelity_point, Integer credit,
                     Integer count_notification, List<DomaineResponse> domaineList, Integer total_reviews,
                     String preferred_language_code, String preferred_currency, CityResponse citie) {
        this.user_id = user_id;
        this.name = name;
        this.prenom = prenom;
        this.email_id = email_id;
        this.is_email_verified = is_email_verified;
        this.mobile_no = mobile_no;
        this.is_mobile_verified = is_mobile_verified;
        this.image = image;
        this.user_type = user_type;
        this.seller_type = seller_type;
        this.avg_rating = avg_rating;
        this.fidelity_point = fidelity_point;
        this.credit = credit;
        this.count_notification = count_notification;
        this.domaineList = domaineList;
        this.total_reviews = total_reviews;
        this.preferred_language_code = preferred_language_code;
        this.preferred_currency = preferred_currency;
        this.citie = citie;
    }

    // Convertir IShopLoginResponse en IShopInfo
    public static IShopInfo fromLoginResponse(IShopLoginResponse response) {
        if (response == null) {
            return null;
        }
    
        return new IShopInfo(
            response.getUser_id(),
            response.getName(),
            response.getPrenom(),
            response.getEmail_id(),
            response.getIs_email_verified(),
            response.getMobile_no(),
            response.getIs_mobile_verified(),
            response.getImage(),
            response.getUser_type(),
            response.getSeller_type(),
            response.getAvg_rating(),
            response.getFidelity_point(),
            response.getCredit(),
            response.getCount_notification(),
            response.getDomaineList(),
            response.getTotal_reviews(),
            response.getPreferred_language_code(),
            response.getPreferred_currency(),
            response.getCitie()
        );
    }
    // Getters et Setters
    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
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

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public Boolean getIs_email_verified() {
        return is_email_verified;
    }

    public void setIs_email_verified(Boolean is_email_verified) {
        this.is_email_verified = is_email_verified;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public Boolean getIs_mobile_verified() {
        return is_mobile_verified;
    }

    public void setIs_mobile_verified(Boolean is_mobile_verified) {
        this.is_mobile_verified = is_mobile_verified;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getSeller_type() {
        return seller_type;
    }

    public void setSeller_type(String seller_type) {
        this.seller_type = seller_type;
    }

    public Double getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(Double avg_rating) {
        this.avg_rating = avg_rating;
    }

    public Integer getFidelity_point() {
        return fidelity_point;
    }

    public void setFidelity_point(Integer fidelity_point) {
        this.fidelity_point = fidelity_point;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getCount_notification() {
        return count_notification;
    }

    public void setCount_notification(Integer count_notification) {
        this.count_notification = count_notification;
    }

    public List<DomaineResponse> getDomaineList() {
        return domaineList;
    }

    public void setDomaineList(List<DomaineResponse> domaineList) {
        this.domaineList = domaineList;
    }

    public Integer getTotal_reviews() {
        return total_reviews;
    }

    public void setTotal_reviews(Integer total_reviews) {
        this.total_reviews = total_reviews;
    }

    public String getPreferred_language_code() {
        return preferred_language_code;
    }

    public void setPreferred_language_code(String preferred_language_code) {
        this.preferred_language_code = preferred_language_code;
    }

    public String getPreferred_currency() {
        return preferred_currency;
    }

    public void setPreferred_currency(String preferred_currency) {
        this.preferred_currency = preferred_currency;
    }

    public CityResponse getCitie() {
        return citie;
    }

    public void setCitie(CityResponse citie) {
        this.citie = citie;
    }
}