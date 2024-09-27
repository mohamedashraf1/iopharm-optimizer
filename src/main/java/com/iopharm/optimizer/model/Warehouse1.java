package com.iopharm.optimizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse1 {
    Integer id;
    double minOrderPrice;
    Map<Integer, Double> productPrices; // between productId and price
    Map<Integer, Integer> productQuantities; // between productId and available quantity

}
