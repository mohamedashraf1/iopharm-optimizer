package com.iopharm.optimizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CbcProduct {
    int id;
    int quantity;
    double price;
    String name;
    
}
