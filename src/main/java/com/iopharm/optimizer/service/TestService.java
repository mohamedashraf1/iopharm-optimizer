package com.iopharm.optimizer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import  com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;
import com.iopharm.optimizer.dtos.ProductDTO;
import com.iopharm.optimizer.dtos.WarehouseDTO;
import com.iopharm.optimizer.dtos.WarehouseProductDTO;
import com.iopharm.optimizer.model.CbcInput;
import com.iopharm.optimizer.model.CbcProduct;
import com.iopharm.optimizer.model.CbcWarehouse;
import com.iopharm.optimizer.model.Product;
import com.iopharm.optimizer.model.Warehouse1;
import com.iopharm.optimizer.model.WarehouseProduct;

@Service
public class TestService {
    
    public List<Warehouse1> getWarehouses1(){
        List<Warehouse1> warehouses = new ArrayList<>();
        Warehouse1 temp1 = new Warehouse1();
        temp1.setId(1);
        temp1.setMinOrderPrice(1000);
        Map<Integer, Double> productPrices = new HashMap<>();
        productPrices.put(1, 10.0);
        productPrices.put(2, 20.0);
        productPrices.put(3, 20.0);
        productPrices.put(4, 20.0);

        temp1.setProductPrices(productPrices);

        Map<Integer, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1, 2);
        productQuantities.put(2, 5);
        productQuantities.put(3, 5);
        productQuantities.put(4, 5);

        temp1.setProductQuantities(productQuantities);

        warehouses.add(temp1);

        Warehouse1 temp2 = new Warehouse1();
        temp2.setId(2);
        temp2.setMinOrderPrice(10);
        Map<Integer, Double> productPrices2 = new HashMap<>();
        productPrices2.put(1, 20.0);
        productPrices2.put(2, 10.0);
        productPrices2.put(3, 20.0);
        productPrices2.put(4, 20.0);

        temp2.setProductPrices(productPrices2);

        Map<Integer, Integer> productQuantities2 = new HashMap<>();
        productQuantities2.put(1, 5);
        productQuantities2.put(2, 5);
        productQuantities2.put(3, 5);
        productQuantities2.put(4, 5);

        temp2.setProductQuantities(productQuantities2);

        warehouses.add(temp2);



        Warehouse1 temp3 = new Warehouse1();
        temp3.setId(3);
        temp3.setMinOrderPrice(60);
        Map<Integer, Double> productPrices3 = new HashMap<>();
        productPrices3.put(1, 20.0);
        productPrices3.put(2, 20.0);
        productPrices3.put(3, 10.0);
        productPrices3.put(4, 20.0);

        temp3.setProductPrices(productPrices3);

        Map<Integer, Integer> productQuantities3 = new HashMap<>();
        productQuantities3.put(1, 1);
        productQuantities3.put(2, 5);
        productQuantities3.put(3, 5);
        productQuantities3.put(4, 5);

        temp3.setProductQuantities(productQuantities3);

        warehouses.add(temp3);


        Warehouse1 temp4 = new Warehouse1();
        temp4.setId(4);
        temp4.setMinOrderPrice(10);
        Map<Integer, Double> productPrices4 = new HashMap<>();
        productPrices4.put(1, 20.0);
        productPrices4.put(2, 20.0);
        productPrices4.put(3, 20.0);
        productPrices4.put(4, 10.0);

        temp4.setProductPrices(productPrices4);

        Map<Integer, Integer> productQuantities4 = new HashMap<>();
        productQuantities4.put(1, 5);
        productQuantities4.put(2, 5);
        productQuantities4.put(3, 5);
        productQuantities4.put(4, 5);

        temp4.setProductQuantities(productQuantities4);

        warehouses.add(temp4);

        return warehouses;
    }

    public List<Product> getOrder1(){
        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 5));
        order.add(new Product(2, 5));
        order.add(new Product(3, 5));
        order.add(new Product(4, 5));
        return order;
    }


    public List<Warehouse1> getWarehouses2(){
        List<Warehouse1> warehouses = new ArrayList<>();
        Warehouse1 temp1 = new Warehouse1();
        temp1.setId(1);
        temp1.setMinOrderPrice(1100);
        temp1.setName("Fardoos");

        Map<Integer, Double> productPrices = new HashMap<>();
        productPrices.put(1, 16.2);
        productPrices.put(2, 67.5);
        productPrices.put(3, 20.0);
        productPrices.put(4, 12.0);
        productPrices.put(5, 27.9);

        temp1.setProductPrices(productPrices);

        Map<Integer, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1, 0);
        productQuantities.put(2, 100);
        productQuantities.put(3, 100);
        productQuantities.put(4, 100);
        productQuantities.put(5, 100);

        temp1.setProductQuantities(productQuantities);

        warehouses.add(temp1);

        Warehouse1 temp2 = new Warehouse1();
        temp2.setId(2);
        temp2.setMinOrderPrice(800);
        temp2.setName("Aqsa");

        Map<Integer, Double> productPrices2 = new HashMap<>();
        productPrices2.put(1, 15.8);
        productPrices2.put(2, 66.6);
        productPrices2.put(3, 19.75);
        productPrices2.put(4, 11.55);
        productPrices2.put(5, 30.0);

        temp2.setProductPrices(productPrices2);

        Map<Integer, Integer> productQuantities2 = new HashMap<>();
        productQuantities2.put(1, 100);
        productQuantities2.put(2, 100);
        productQuantities2.put(3, 100);
        productQuantities2.put(4, 0);
        productQuantities2.put(5, 0);

        temp2.setProductQuantities(productQuantities2);

        warehouses.add(temp2);


        Warehouse1 temp3 = new Warehouse1();
        temp3.setId(3);
        temp3.setMinOrderPrice(500);
        temp3.setName("Oscar");

        Map<Integer, Double> productPrices3 = new HashMap<>();
        productPrices3.put(1, 16.4);
        productPrices3.put(2, 66.6);
        productPrices3.put(3, 20.25);
        productPrices3.put(4, 12.15);
        productPrices3.put(5, 28.20);

        temp3.setProductPrices(productPrices3);

        Map<Integer, Integer> productQuantities3 = new HashMap<>();
        productQuantities3.put(1, 100);
        productQuantities3.put(2, 0);
        productQuantities3.put(3, 100);
        productQuantities3.put(4, 100);
        productQuantities3.put(5, 100);

        temp3.setProductQuantities(productQuantities3);

        warehouses.add(temp3);

        return warehouses;
    }

    public List<Product> getOrder2(){

        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 8, "Panadol"));
        order.add(new Product(2, 8, "Augmentin"));
        order.add(new Product(3, 8, "Brufen"));
        order.add(new Product(4, 8, "Antinal"));
        order.add(new Product(5, 8, "Concor"));
        return order;
    }

}

