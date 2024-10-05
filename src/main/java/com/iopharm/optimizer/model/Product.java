package com.iopharm.optimizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    int id;
    int quantity;
    String name;
    
	public Product(int id, int quantity) {
		super();
		this.id = id;
		this.quantity = quantity;
	}
    
    
}
