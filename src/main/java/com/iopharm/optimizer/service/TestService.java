package com.iopharm.optimizer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.iopharm.optimizer.model.Product;
import com.iopharm.optimizer.model.Warehouse1;

@Service
public class TestService {
    
    public List<Warehouse1> getWarehouses1(){
        List<Warehouse1> warehouses = new ArrayList<>();
        Map<Integer, Double> productPrices = new HashMap<>();
        productPrices.put(1, 16.2);
        productPrices.put(2, 67.5);
        productPrices.put(3, 20.0);
        productPrices.put(4, 12.0);
        productPrices.put(5, 27.9);

        Map<Integer, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1, 0);
        productQuantities.put(2, 100);
        productQuantities.put(3, 100);
        productQuantities.put(4, 100);
        productQuantities.put(5, 100);

        warehouses.add(new Warehouse1(1, "Fardos", 1000, productPrices, productQuantities));

        Map<Integer, Double> productPrices2 = new HashMap<>();
        productPrices2.put(1, 15.8);
        productPrices2.put(2, 66.6);
        productPrices2.put(3, 19.75);
        productPrices2.put(4, 11.55);
        productPrices2.put(5, 30.0);

        Map<Integer, Integer> productQuantities2 = new HashMap<>();
        productQuantities2.put(1, 100);
        productQuantities2.put(2, 100);
        productQuantities2.put(3, 100);
        productQuantities2.put(4, 0);
        productQuantities2.put(5, 0);

        warehouses.add(new Warehouse1(2, "Aqsa", 800, productPrices2, productQuantities2));

        Map<Integer, Double> productPrices3 = new HashMap<>();
        productPrices3.put(1, 16.4);
        productPrices3.put(2, 66.6);
        productPrices3.put(3, 20.25);
        productPrices3.put(4, 12.15);
        productPrices3.put(5, 28.2);

        Map<Integer, Integer> productQuantities3 = new HashMap<>();
        productQuantities3.put(1, 100);
        productQuantities3.put(2, 100);
        productQuantities3.put(3, 100);
        productQuantities3.put(4, 100);
        productQuantities3.put(5, 100);

        warehouses.add(new Warehouse1(3, "Oscar", 500, productPrices3, productQuantities3));

        return warehouses;
    }

    public List<Product> getOrder1(){
        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 5, "Panadol"));
        order.add(new Product(2, 5, "Augmentin"));
        order.add(new Product(3, 5, "Brufen"));
        order.add(new Product(4, 5, "Antinal"));
        order.add(new Product(5, 5, "Concor"));
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

    public List<Warehouse1> getWarehouses3(){
        List<Warehouse1> warehouses = new ArrayList<>();
        Warehouse1 temp1 = new Warehouse1();
        temp1.setId(1);
        temp1.setName("Fardos");
        temp1.setMinOrderPrice(1100);
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
        //-----------------------------
        Warehouse1 temp2 = new Warehouse1();
        temp2.setId(2);
        temp2.setName("Aqsa");
        temp2.setMinOrderPrice(800);
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
        // ---------------------------------
        Warehouse1 temp3 = new Warehouse1();
        temp3.setId(3);
        temp3.setName("Oscar");
        temp3.setMinOrderPrice(500);
        Map<Integer, Double> productPrices3 = new HashMap<>();
        productPrices3.put(1, 16.4);
        productPrices3.put(2, 66.6);
        productPrices3.put(3, 20.25);
        productPrices3.put(4, 12.15);
        productPrices3.put(5, 28.2);
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

    public List<Product> getOrder3(){
        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 20, "Panadol"));
        order.add(new Product(2, 20, "Augmentin"));
        order.add(new Product(3, 20, "Brufen"));
        order.add(new Product(4, 20, "Antinal"));
        order.add(new Product(5, 20,"Concor"));
        return order;
    }

    public List<Warehouse1> getWarehouses5(){
        List<Warehouse1> warehouses = new ArrayList<>();
        Warehouse1 temp1 = new Warehouse1();
        temp1.setId(1);
        temp1.setName("Fardos");
        temp1.setMinOrderPrice(1100);
        Map<Integer, Double> productPrices = new HashMap<>();
        productPrices.put(1, 16.2);
        productPrices.put(2, 65.7);
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
        //-----------------------------
        Warehouse1 temp2 = new Warehouse1();
        temp2.setId(2);
        temp2.setName("Aqsa");
        temp2.setMinOrderPrice(800);
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
        // ---------------------------------
        Warehouse1 temp3 = new Warehouse1();
        temp3.setId(3);
        temp3.setName("Oscar");
        temp3.setMinOrderPrice(500);
        Map<Integer, Double> productPrices3 = new HashMap<>();
        productPrices3.put(1, 16.4);
        productPrices3.put(2, 66.6);
        productPrices3.put(3, 20.25);
        productPrices3.put(4, 11.85);
        productPrices3.put(5, 27.6);
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

    public List<Product> getOrder5(){
        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 20, "Panadol"));
        order.add(new Product(2, 20, "Augmentin"));
        order.add(new Product(3, 35, "Brufen"));
        order.add(new Product(4, 20, "Antinal"));
        order.add(new Product(5, 20,"Concor"));
        return order;
    }

    public List<Warehouse1> getWarehouses4(){
        List<Warehouse1> warehouses = new ArrayList<>();
        Warehouse1 temp1 = new Warehouse1();
        temp1.setId(1);
        temp1.setMinOrderPrice(1100);
        temp1.setName("Fardoos");

        Map<Integer, Double> productPrices = new HashMap<>();
        productPrices.put(1, 16.2);
        productPrices.put(2, 65.7);
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
        productPrices3.put(4, 12.25);
        productPrices3.put(5, 28.2);

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
    public List<Product> getOrder4(){

        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 20, "Panadol"));
        order.add(new Product(2, 20, "Augmentin"));
        order.add(new Product(3, 35, "Brufen"));
        order.add(new Product(4, 20, "Antinal"));
        order.add(new Product(5, 20,"Concor"));
        return order;
    }

    public List<Warehouse1> getWarehouses6(){
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
        productPrices3.put(4, 10.5);
        productPrices3.put(5, 19.5);

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

    public List<Product> getOrder6(){

        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 8, "Panadol"));
        order.add(new Product(2, 8, "Augmentin"));
        order.add(new Product(3, 8, "Brufen"));
        order.add(new Product(4, 8, "Antinal"));
        order.add(new Product(5, 8, "Concor"));
        return order;
    }

    public List<Warehouse1> getWarehouses7(){
        List<Warehouse1> warehouses = new ArrayList<>();
        Map<Integer, Double> productPrices = new HashMap<>();
        productPrices.put(1, 16.2);
        productPrices.put(2, 63.0);
        productPrices.put(3, 19.75);
        productPrices.put(4, 10.8);
        productPrices.put(5, 20.1);

        Map<Integer, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1, 0);
        productQuantities.put(2, 100);
        productQuantities.put(3, 100);
        productQuantities.put(4, 100);
        productQuantities.put(5, 100);

        warehouses.add(new Warehouse1(1, "Fardos", 1100, productPrices, productQuantities));

        Map<Integer, Double> productPrices2 = new HashMap<>();
        productPrices2.put(1, 15.8);
        productPrices2.put(2, 64.8);
        productPrices2.put(3, 21.25);
        productPrices2.put(4, 11.55);
        productPrices2.put(5, 30.0);

        Map<Integer, Integer> productQuantities2 = new HashMap<>();
        productQuantities2.put(1, 100);
        productQuantities2.put(2, 100);
        productQuantities2.put(3, 100);
        productQuantities2.put(4, 0);
        productQuantities2.put(5, 0);

        warehouses.add(new Warehouse1(2, "Aqsa", 800, productPrices2, productQuantities2));

        Map<Integer, Double> productPrices3 = new HashMap<>();
        productPrices3.put(1, 19.0);
        productPrices3.put(2, 66.6);
        productPrices3.put(3, 21.0);
        productPrices3.put(4, 10.5);
        productPrices3.put(5, 19.5);

        Map<Integer, Integer> productQuantities3 = new HashMap<>();
        productQuantities3.put(1, 100);
        productQuantities3.put(2, 0);
        productQuantities3.put(3, 100);
        productQuantities3.put(4, 100);
        productQuantities3.put(5, 100);

        warehouses.add(new Warehouse1(3, "Oscar", 500, productPrices3, productQuantities3));

        return warehouses;
    }

    public List<Product> getOrder7(){
        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 35, "Panadol"));
        order.add(new Product(2, 15, "Augmentin"));
        order.add(new Product(3, 15, "Brufen"));
        order.add(new Product(4, 22, "Antinal"));
        order.add(new Product(5, 22, "Concor"));
        return order;
    }
}

