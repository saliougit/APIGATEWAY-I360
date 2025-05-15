package com.innov4africa.api_gateway.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Métadonnées de pagination")
public class PaginationMetadata {    private Integer current_offset;
    private Integer limit;
    private Integer totalItems;
    private Integer next_offset;

    public PaginationMetadata() {}

    public PaginationMetadata(Integer current_offset, Integer limit, Integer totalItems, Integer next_offset) {
        this.current_offset = current_offset;
        this.limit = limit;
        this.totalItems = totalItems;
        this.next_offset = next_offset;
    }    public Integer getCurrent_offset() {
        return current_offset;
    }

    public void setCurrent_offset(Integer current_offset) {
        this.current_offset = current_offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getNext_offset() {
        return next_offset;
    }

    public void setNext_offset(Integer next_offset) {
        this.next_offset = next_offset;
    }
}
