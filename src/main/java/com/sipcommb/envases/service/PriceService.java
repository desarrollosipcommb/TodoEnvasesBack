package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.PriceSearchRequest;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class PriceService {

    public boolean verifyPriceSearchRequest(PriceSearchRequest priceSearchRequest) {
        if(priceSearchRequest.getExactPrice() == null && priceSearchRequest.getMinPrice() == null && priceSearchRequest.getMaxPrice() == null) {
            throw new IllegalArgumentException("Debe especificar al menos un rango de precio.");
        }

        if(priceSearchRequest.getPriceDeal() == null) {
            throw new IllegalArgumentException("El tipo de trato de precio debe ser especificado.");
        }

        if(priceSearchRequest.getMinPrice() != null && priceSearchRequest.getMinPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio mínimo no puede ser negativo.");
        }

        if(priceSearchRequest.getMaxPrice() != null && priceSearchRequest.getMaxPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio máximo no puede ser negativo.");
        }

        if(priceSearchRequest.getExactPrice() != null && priceSearchRequest.getExactPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio exacto no puede ser negativo.");
        }

        if(priceSearchRequest.getMaxPrice() != null && priceSearchRequest.getMinPrice() != null && priceSearchRequest.getMaxPrice().compareTo(priceSearchRequest.getMinPrice()) < 0) {
            throw new IllegalArgumentException("El precio máximo no puede ser menor que el precio mínimo.");
        }

        if(priceSearchRequest.getMinPrice() == null){
            priceSearchRequest.setMinPrice(BigDecimal.ZERO);
        }

        if(priceSearchRequest.getMaxPrice() == null){
            priceSearchRequest.setMaxPrice(BigDecimal.valueOf(Double.MAX_VALUE));
        }

        boolean exactSearch = false;

        if(priceSearchRequest.getExactPrice() != null){
            exactSearch = true;
        }

        return exactSearch;
    }
    
}
