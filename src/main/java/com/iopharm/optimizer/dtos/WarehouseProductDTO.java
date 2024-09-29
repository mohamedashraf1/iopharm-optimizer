package com.iopharm.optimizer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProductDTO {
    Long productId;
    Double price;
    Long storage;
}
