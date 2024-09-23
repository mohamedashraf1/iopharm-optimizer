package com.iopharm.optimizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solution {
    Map<Product, Warehouse> productWarehouseMapping;
    double totalCost;
}
