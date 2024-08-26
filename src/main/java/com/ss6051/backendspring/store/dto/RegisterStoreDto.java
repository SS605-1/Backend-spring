package com.ss6051.backendspring.store.dto;

import lombok.Data;

/**
 * 가게 등록을 위한 DTO
 * <br>String {@code store_name}
 * <br>String {@code street_address}
 * <br>String {@code lot_number_address}
 */
@Data
public class RegisterStoreDto {
    public String storeName;
    public String streetAddress;
    public String lotNumberAddress;
}
