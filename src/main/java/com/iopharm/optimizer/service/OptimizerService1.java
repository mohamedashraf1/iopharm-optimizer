package com.iopharm.optimizer.service;

import com.iopharm.optimizer.model.Product;
import com.iopharm.optimizer.model.Solution;
import com.iopharm.optimizer.model.Warehouse;
import com.iopharm.optimizer.model.Warehouse1;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OptimizerService1 {

    List<Warehouse1> getWarehouses(){
        List<Warehouse1> warehouses = new ArrayList<>();
        Warehouse1 temp1 = new Warehouse1();
        temp1.setId(1);
        temp1.setMinOrderPrice(60);
        Map<Integer, Double> productPrices = new HashMap<>();
        productPrices.put(1, 10.0);
        productPrices.put(2, 20.0);
        productPrices.put(3, 20.0);
        productPrices.put(4, 20.0);

        temp1.setProductPrices(productPrices);

        Map<Integer, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1, 5);
        productQuantities.put(2, 5);
        productQuantities.put(3, 5);
        productQuantities.put(4, 5);

        temp1.setProductQuantities(productQuantities);

        warehouses.add(temp1);

        Warehouse1 temp2 = new Warehouse1();
        temp2.setId(2);
        temp2.setMinOrderPrice(60);
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
        temp3.setMinOrderPrice(10);
        Map<Integer, Double> productPrices3 = new HashMap<>();
        productPrices3.put(1, 20.0);
        productPrices3.put(2, 20.0);
        productPrices3.put(3, 10.0);
        productPrices3.put(4, 20.0);

        temp3.setProductPrices(productPrices3);

        Map<Integer, Integer> productQuantities3 = new HashMap<>();
        productQuantities3.put(1, 5);
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

    List<Product> getOrder(){
        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 5));
        order.add(new Product(2, 5));
        order.add(new Product(3, 5));
        order.add(new Product(4, 5));
        return order;
    }

    public Map<Integer, List<Integer>> getOptimizedSolution(){
        List<Warehouse1> warehouses = getWarehouses();
        List<Product> order = getOrder();

        // warehouseId, List<ProductId>
        Map<Integer, List<Integer>> assignment = new HashMap<>();
        Map<Integer, List<Integer>> finalAssignment = new HashMap<>();

        // List<warehouseId>
        Set<Integer> notSatisfiedWarehousesIds = new HashSet<>();


        // assign each product to the cheapest warehouse that can satisfy the quantity
        for(Product product : order){
            List<Warehouse1> availableWarehouses = getAvailableWarehouses(product, warehouses);
            Warehouse1 cheapestWarehouse = findCheapestWarehouse(product, availableWarehouses);
            assignProductToWarehouse(cheapestWarehouse.getId(), product.getId(), assignment);
        }

        // warehouseId, assignedOrderPrice
        Map<Integer, Double> warehousesOrderPrice = new HashMap<>();

        for(Warehouse1 warehouse : warehouses){
            // an order is assigned to this warehouse
            if(assignment.containsKey(warehouse.getId())){
                Double warehouseOrderPrice = getWarehouseOrderPrice(assignment, warehouse);
                warehousesOrderPrice.put(warehouse.getId(), warehouseOrderPrice);
                if(warehouseOrderPrice >= warehouse.getMinOrderPrice()){
                    // finished
                    finalAssignment.put(warehouse.getId(), assignment.get(warehouse.getId()));
                }else {
                    // need to reassign
                    notSatisfiedWarehousesIds.add(warehouse.getId());
                }
            }
        }


        if(notSatisfiedWarehousesIds.isEmpty()){ // all warehouses satisfy the criteria
            return finalAssignment;
        }

        System.out.println("not satisfied warehouses Ids before: " + notSatisfiedWarehousesIds);

        // take the order of the highest difference (between order price and minOrderPrice) warehouse and add them to the least difference
        while (!notSatisfiedWarehousesIds.isEmpty() && notSatisfiedWarehousesIds.size() > 1){
            System.out.println("reassign");
            reassignProducts(finalAssignment, assignment, notSatisfiedWarehousesIds, warehouses, warehousesOrderPrice);
        }


        System.out.println("final assignment (warehouseId, List<product>): " + finalAssignment);
        System.out.println("not satisfied warehouses Ids after: " + notSatisfiedWarehousesIds);

        return finalAssignment;
    }

    List<Warehouse1> getAvailableWarehouses(Product product, List<Warehouse1> warehouses){
        List<Warehouse1> availableWarehouse = new ArrayList<>();
        for (Warehouse1 warehouse : warehouses){
            if(warehouse.getProductQuantities().get(product.getId()) >= product.getQuantity())
                availableWarehouse.add(warehouse);
        }

        return availableWarehouse;
    }

    Warehouse1 findCheapestWarehouse(Product product, List<Warehouse1> warehouses){
        Warehouse1 cheapestWarehouse = null;
        double minPrice = Double.MAX_VALUE;

        for (Warehouse1 warehouse : warehouses) {
            double price = warehouse.getProductPrices().get(product.getId());
            if (price < minPrice) {
                minPrice = price;
                cheapestWarehouse = warehouse;
            }
        }

        return cheapestWarehouse;
    }

    void assignProductToWarehouse(Integer warehouseId, Integer productId, Map<Integer, List<Integer>> assignment){
        if(assignment.containsKey(warehouseId))
            assignment.get(warehouseId).add(productId);
        else {
            assignment.put(warehouseId, new ArrayList<>());
            assignment.get(warehouseId).add(productId);
        }
    }

    Double getWarehouseOrderPrice(Map<Integer, List<Integer>> assignment,  Warehouse1 warehouse){
        double totalPrice = 0.0;
        for(Integer productId : assignment.get(warehouse.getId())){
            Integer productRequiredQuantity = 0;
            for(Product product : getOrder()){
                if(Objects.equals(product.getId(), productId))
                    productRequiredQuantity = product.getQuantity();
            }

            totalPrice += warehouse.getProductPrices().get(productId) * productRequiredQuantity;
        }
        return totalPrice;
    }

    Warehouse1 getWarehouseById(Integer warehouseId, List<Warehouse1> warehouses){
        for(Warehouse1 warehouse : warehouses){
            if(Objects.equals(warehouse.getId(), warehouseId))
                return warehouse;
        }
        return null;
    }

    void reassignProducts(Map<Integer, List<Integer>> finalAssignment, Map<Integer, List<Integer>> assignment, Set<Integer> notSatisfiedWarehousesIds
            , List<Warehouse1> warehouses, Map<Integer, Double> warehousesOrderPrice){
        List<Warehouse1> notSatisfiedWarehouses = new ArrayList<>();

        for(Integer warehouseId : notSatisfiedWarehousesIds){
            notSatisfiedWarehouses.add(getWarehouseById(warehouseId, warehouses));
        }

        // Sort the notSatisfiedWarehouses by how close they are to reaching their minOrderPrice
        notSatisfiedWarehouses.sort(Comparator.comparingDouble(warehouse -> {
            // Get the total order price for the warehouse
            double totalOrderPrice = warehousesOrderPrice.get(warehouse.getId());
            // Calculate the difference to the minOrderPrice
            return warehouse.getMinOrderPrice() - totalOrderPrice;
        }));

        List<Integer> firstWarehouseProductIds = assignment.get(notSatisfiedWarehouses.get(0).getId());
        // add the last warehouse products to the first
        firstWarehouseProductIds.addAll(assignment.get(notSatisfiedWarehouses.get(notSatisfiedWarehouses.size()-1).getId()));

        // overwrite the first notSatisfiedWarehouse with the new list
        assignment.put(notSatisfiedWarehouses.get(0).getId(), firstWarehouseProductIds);

        // remove the last warehouse id from the notSatisfied as we will not order anything from it now
        notSatisfiedWarehousesIds.remove(notSatisfiedWarehouses.get(notSatisfiedWarehouses.size()-1).getId());

        // check if the first warehouse is now satisfied and remove it
        Warehouse1 firstWarehouse = getWarehouseById(notSatisfiedWarehouses.get(0).getId(), warehouses);
        double firstWarehouseOrderPrice
                = getWarehouseOrderPrice(assignment, firstWarehouse);
        if(firstWarehouseOrderPrice >= firstWarehouse.getMinOrderPrice()){
            finalAssignment.put(notSatisfiedWarehouses.get(0).getId(), firstWarehouseProductIds);
            notSatisfiedWarehousesIds.remove(notSatisfiedWarehouses.get(0).getId());
        }
    }
}
