package com.iopharm.optimizer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    Long id;
    String name;
    Double minOrderPrice;
    List<WarehouseProductDTO> availableProducts;
}

