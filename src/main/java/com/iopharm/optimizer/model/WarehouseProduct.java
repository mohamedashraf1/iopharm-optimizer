package com.iopharm.optimizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProduct {
    Integer warehouseId;
    Integer productId;
    Double price;
    Double minOrderPrice;
}
