package com.sipcommb.envases.dto;

import java.math.BigDecimal;

public class PriceSearchRequest {

    private BigDecimal maxPrice;

    private BigDecimal minPrice;

    private BigDecimal exactPrice;

    private PriceDeals priceDeal;


    public PriceSearchRequest() {
    }

    public PriceSearchRequest(BigDecimal maxPrice, BigDecimal minPrice, BigDecimal exactPrice, PriceDeals priceDeal) {
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.exactPrice = exactPrice;
        this.priceDeal = priceDeal;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getExactPrice() {
        return exactPrice;
    }

    public void setExactPrice(BigDecimal exactPrice) {
        this.exactPrice = exactPrice;
    }

    public PriceDeals getPriceDeal() {
        return priceDeal;
    }

    public void setPriceDeal(PriceDeals priceDeal) {
        this.priceDeal = priceDeal;
    }

    
}
