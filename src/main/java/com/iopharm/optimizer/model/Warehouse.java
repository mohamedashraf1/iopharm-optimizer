package com.iopharm.optimizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    Integer id;
    double minOrderPrice;
    Map<Product, Double> availableProducts;
}
