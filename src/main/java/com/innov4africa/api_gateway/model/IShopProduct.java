package com.innov4africa.api_gateway.model;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Produit i-shop")
public class IShopProduct {
    private Long product_id;
    private String name;
    private String description;
    private Double price;
    private Integer quantite;
    private String currency;
    private Double avg_rating;
    private Integer total_reviews;
    private String market_type;
    private String mini_img;
    private List<IShopMedia> media;
    private IShopSeller seller;
    private IShopCategory category;
    private IShopCategory sub_category;
    private String share_url;
    private Boolean is_favorite;

    // Constructeur par défaut
    public IShopProduct() {
        this.currency = "XOF"; // Valeur par défaut
    }

    // Getters et Setters
    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency != null ? currency : "XOF";
    }

    public Double getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(Double avg_rating) {
        this.avg_rating = avg_rating;
    }

    public Integer getTotal_reviews() {
        return total_reviews;
    }

    public void setTotal_reviews(Integer total_reviews) {
        this.total_reviews = total_reviews;
    }

    public String getMarket_type() {
        return market_type;
    }

    public void setMarket_type(String market_type) {
        this.market_type = market_type;
    }

    public String getMini_img() {
        return mini_img;
    }

    public void setMini_img(String mini_img) {
        this.mini_img = mini_img;
    }

    public List<IShopMedia> getMedia() {
        return media;
    }

    public void setMedia(List<IShopMedia> media) {
        this.media = media;
    }

    public IShopSeller getSeller() {
        return seller;
    }

    public void setSeller(IShopSeller seller) {
        this.seller = seller;
    }

    public IShopCategory getCategory() {
        return category;
    }

    public void setCategory(IShopCategory category) {
        this.category = category;
    }

    public IShopCategory getSub_category() {
        return sub_category;
    }

    public void setSub_category(IShopCategory sub_category) {
        this.sub_category = sub_category;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public Boolean getIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(Boolean is_favorite) {
        this.is_favorite = is_favorite;
    }
}
