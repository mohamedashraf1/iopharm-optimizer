package com.iopharm.optimizer.service;

import com.iopharm.optimizer.model.Product;
import com.iopharm.optimizer.model.Solution;
import com.iopharm.optimizer.model.Warehouse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OptimizerService {

    List<Warehouse> getWarehouses(){
        List<Warehouse> warehouses = new ArrayList<>();
        Warehouse temp1 = new Warehouse();
        temp1.setId(1);
        temp1.setMinOrderPrice(10);
        Map<Product, Double> availableProducts = new HashMap<>();
        availableProducts.put(new Product(1, 5), 10.0);
        availableProducts.put(new Product(2, 5), 20.0);
        availableProducts.put(new Product(3, 5), 20.0);
        availableProducts.put(new Product(4, 5), 20.0);

        temp1.setAvailableProducts(availableProducts);

        warehouses.add(temp1);



        Warehouse temp2 = new Warehouse();
        temp2.setId(2);
        temp2.setMinOrderPrice(10);
        Map<Product, Double> availableProducts2 = new HashMap<>();
        availableProducts2.put(new Product(1, 5), 20.0);
        availableProducts2.put(new Product(2, 5), 10.0);
        availableProducts2.put(new Product(3, 5), 20.0);
        availableProducts2.put(new Product(4, 5), 20.0);

        temp2.setAvailableProducts(availableProducts2);

        warehouses.add(temp2);



        Warehouse temp3 = new Warehouse();
        temp3.setId(3);
        temp3.setMinOrderPrice(10);
        Map<Product, Double> availableProducts3 = new HashMap<>();
        availableProducts3.put(new Product(1, 5), 20.0);
        availableProducts3.put(new Product(2, 5), 20.0);
        availableProducts3.put(new Product(3, 5), 10.0);
        availableProducts3.put(new Product(4, 5), 20.0);

        temp3.setAvailableProducts(availableProducts3);

        warehouses.add(temp3);



        Warehouse temp4 = new Warehouse();
        temp4.setId(4);
        temp4.setMinOrderPrice(10);
        Map<Product, Double> availableProducts4 = new HashMap<>();
        availableProducts4.put(new Product(1, 5), 20.0);
        availableProducts4.put(new Product(2, 5), 20.0);
        availableProducts4.put(new Product(3, 5), 20.0);
        availableProducts4.put(new Product(4, 5), 10.0);

        temp4.setAvailableProducts(availableProducts4);

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

    public Solution getOptimizedSolution(){
        List<Warehouse> warehouses = getWarehouses();
        List<Product> order = getOrder();

        Map<Warehouse, List<Product>> assignment = new HashMap<>();
        Solution bestSolution = null;

        order.sort((p1, p2) -> compareProductPriceAcrossWarehouses(p1, p2, warehouses));

        // Assign products greedily to warehouses
        for (Product product : order) {
            Warehouse bestWarehouse = findCheapestWarehouseForProduct(product, warehouses);
            assignProductToWarehouse(product, bestWarehouse, assignment);
        }

        if (!checkMinOrderPrice(assignment)) {
            bestSolution = backtrackAndReassign(assignment, warehouses);
        } else {
            Map<Product, Warehouse> productWarehouseMapping = new HashMap<>();
            for (Map.Entry<Warehouse, List<Product>> entry : assignment.entrySet()) {
                Warehouse warehouse = entry.getKey();
                for (Product product : entry.getValue()) {
                    productWarehouseMapping.put(product, warehouse);
                }
            }

            // Calculate total cost and create Solution object
            bestSolution = new Solution(productWarehouseMapping, calculateTotalCost(assignment));        }

        return bestSolution;
    }

    // Compares the price of two products across all warehouses and sorts them in ascending order by price
    public int compareProductPriceAcrossWarehouses(Product p1, Product p2, List<Warehouse> warehouses) {
        double minPriceP1 = findMinPriceForProduct(p1, warehouses);
        double minPriceP2 = findMinPriceForProduct(p2, warehouses);

        return Double.compare(minPriceP1, minPriceP2);
    }

    // Helper method to find the minimum price of a product across all warehouses
    private double findMinPriceForProduct(Product product, List<Warehouse> warehouses) {
        double minPrice = Double.MAX_VALUE;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getAvailableProducts().containsKey(product)) {
                double price = warehouse.getAvailableProducts().get(product);
                minPrice = Math.min(minPrice, price);
            }
        }
        return minPrice;
    }

    // Finds the warehouse that offers the cheapest price for the given product
    public Warehouse findCheapestWarehouseForProduct(Product product, List<Warehouse> warehouses) {
        Warehouse cheapestWarehouse = null;
        double minPrice = Double.MAX_VALUE;

        for (Warehouse warehouse : warehouses) {
            if (warehouse.getAvailableProducts().containsKey(product)) {
                double price = warehouse.getAvailableProducts().get(product);
                if (price < minPrice) {
                    minPrice = price;
                    cheapestWarehouse = warehouse;
                }
            }
        }

        return cheapestWarehouse;
    }


    // Assigns a product to a specific warehouse and updates the assignment map
    public void assignProductToWarehouse(Product product, Warehouse warehouse, Map<Warehouse, List<Product>> assignment) {
        // If the warehouse already has some products assigned, just add to its list
        if (assignment.containsKey(warehouse)) {
            assignment.get(warehouse).add(product);
        } else {
            // Otherwise, create a new list for this warehouse
            List<Product> products = new ArrayList<>();
            products.add(product);
            assignment.put(warehouse, products);
        }
    }

    // Checks if each warehouse's total assigned products' price meets the minimum order requirement
    public boolean checkMinOrderPrice(Map<Warehouse, List<Product>> assignment) {
        for (Map.Entry<Warehouse, List<Product>> entry : assignment.entrySet()) {
            Warehouse warehouse = entry.getKey();
            List<Product> assignedProducts = entry.getValue();

            double totalPrice = 0.0;
            for (Product product : assignedProducts) {
                totalPrice += warehouse.getAvailableProducts().get(product);
            }

            // If the total price for the warehouse is less than the minimum order price, return false
            if (totalPrice < warehouse.getMinOrderPrice()) {
                return false;
            }
        }
        return true;
    }

    // Calculates the total cost of all products assigned to warehouses
    public double calculateTotalCost(Map<Warehouse, List<Product>> assignment) {
        double totalCost = 0.0;

        for (Map.Entry<Warehouse, List<Product>> entry : assignment.entrySet()) {
            Warehouse warehouse = entry.getKey();
            List<Product> assignedProducts = entry.getValue();

            for (Product product : assignedProducts) {
                totalCost += warehouse.getAvailableProducts().get(product);
            }
        }

        return totalCost;
    }

    public Solution backtrackAndReassign(Map<Warehouse, List<Product>> assignment, List<Warehouse> warehouses) {
        // Iterate through each warehouse and check if the total price meets the min order price
        for (Map.Entry<Warehouse, List<Product>> entry : assignment.entrySet()) {
            Warehouse warehouse = entry.getKey();
            List<Product> assignedProducts = entry.getValue();

            double totalPrice = 0.0;
            for (Product product : assignedProducts) {
                totalPrice += warehouse.getAvailableProducts().get(product);
            }

            // If total price is less than minimum order price, attempt reassignment or adjustment
            if (totalPrice < warehouse.getMinOrderPrice()) {
                // Try reassigning products from other warehouses
                for (Warehouse otherWarehouse : warehouses) {
                    if (!otherWarehouse.equals(warehouse)) {
                        List<Product> otherProducts = assignment.getOrDefault(otherWarehouse, new ArrayList<>());

                        for (Product product : otherProducts) {
                            double productPrice = warehouse.getAvailableProducts().getOrDefault(product, Double.MAX_VALUE);
                            if (productPrice != Double.MAX_VALUE && (totalPrice + productPrice) >= warehouse.getMinOrderPrice()) {
                                // Reassign the product from another warehouse to this warehouse
                                otherProducts.remove(product);
                                assignedProducts.add(product);
                                totalPrice += productPrice;

                                if (totalPrice >= warehouse.getMinOrderPrice()) {
                                    break;
                                }
                            }
                        }
                    }
                    // If the warehouse now meets the minimum price, break out of the loop
                    if (totalPrice >= warehouse.getMinOrderPrice()) {
                        break;
                    }
                }
            }
        }

        // Now convert the assignment into the required product-warehouse mapping
        Map<Product, Warehouse> productWarehouseMapping = new HashMap<>();
        for (Map.Entry<Warehouse, List<Product>> entry : assignment.entrySet()) {
            Warehouse warehouse = entry.getKey();
            List<Product> products = entry.getValue();
            for (Product product : products) {
                productWarehouseMapping.put(product, warehouse);
            }
        }

        // Check if all warehouses meet their min order price constraints
        if (checkMinOrderPrice(assignment)) {
            double totalCost = calculateTotalCost(assignment);
            return new Solution(productWarehouseMapping, totalCost);
        } else {
            // If unable to satisfy constraints, return null or handle error
            return null;  // Could throw an exception or handle the failure more gracefully
        }
    }



}
