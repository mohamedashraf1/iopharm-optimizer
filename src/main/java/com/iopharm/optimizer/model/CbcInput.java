package com.iopharm.optimizer.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CbcInput {
	
    List<CbcProduct> demand;
    List<CbcWarehouse> warehouses;

}
