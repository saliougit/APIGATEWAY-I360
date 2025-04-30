package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Réponse de login pour i-shop")
public class IShopLoginResponse {
    private String status;
    private String message;
    private String code;
    private Integer user_id;
    private String name;
    private String prenom;
    private String email_id;
    private Boolean is_email_verified;
    private String mobile_no;
    private Boolean is_mobile_verified;
    private String otp;
    private String socail_id;
    private String social_type;
    private String image;
    private String user_type;
    private String seller_type;
    private Double avg_rating;
    private Integer fidelity_point;
    private Integer credit;
    private Integer count_notification;
    private List<String> domaineList;
    private Integer total_reviews;
    private String preferred_language_code;
    private String preferred_currency;
    private CityResponse citie;

    public IShopLoginResponse() {}

    // Getters and Setters
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getSocail_id() {
        return socail_id;
    }

    public void setSocail_id(String socail_id) {
        this.socail_id = socail_id;
    }

    public String getSocial_type() {
        return social_type;
    }

    public void setSocial_type(String social_type) {
        this.social_type = social_type;
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

    public List<String> getDomaineList() {
        return domaineList;
    }

    public void setDomaineList(List<String> domaineList) {
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