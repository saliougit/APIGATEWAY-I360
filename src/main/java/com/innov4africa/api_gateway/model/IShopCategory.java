package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Catégorie i-shop")
public class IShopCategory {
    private String name;
    private Boolean is_featured;
    private Long category_id;
    private Long parent_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIs_featured() {
        return is_featured;
    }

    public void setIs_featured(Boolean is_featured) {
        this.is_featured = is_featured;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }
}
