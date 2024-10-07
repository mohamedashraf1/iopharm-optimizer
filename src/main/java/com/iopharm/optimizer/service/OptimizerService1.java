package com.iopharm.optimizer.service;

import com.iopharm.optimizer.model.Product;
import com.iopharm.optimizer.model.Warehouse1;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class OptimizerService1 {

    Map<Integer, Warehouse1> warehousesById = new HashMap<>();
    Map<Integer, Product> productsById = new HashMap<>();

    List<Warehouse1> getWarehouses(){
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

    public List<Product> getDynamicOrder(){
        List<Product> order = new ArrayList<>();
        Random r = new Random();
        for(int i = 1 ; i <= 2000 ; i++){
            // required quantity between 1 : 200
            order.add(new Product(i, r.nextInt(200 - 1) + 1));
        }
        return order;
    }

    List<Warehouse1> getDynamicWarehouses(){
        List<Warehouse1> warehouses = new ArrayList<>();

        Random r = new Random();

        for(int i = 1 ; i <= 20 ; i ++){
            Warehouse1 temp1 = new Warehouse1();
            temp1.setId(i);
            // min order price between 0.5M : 5M
            temp1.setMinOrderPrice(r.nextInt(5000000 - 500000) + 500000);
            Map<Integer, Double> productPrices = new HashMap<>();

            Set<Integer> selectedProducts = new HashSet<>();
            for(int j = 1 ; j < 200 ; j++){
                // product id between 1 : 2000
                Integer productId = r.nextInt(2000 - 1) + 1;
                selectedProducts.add(productId);
                // price between 10 : 500
                productPrices.put(productId, r.nextDouble(500 - 10) + 10);
            }

            temp1.setProductPrices(productPrices);

            Map<Integer, Integer> productQuantities = new HashMap<>();
            for(Integer productId : selectedProducts){
                // quantity between 1 : 200
                productQuantities.put(productId, r.nextInt(200 - 1) + 1);
            }

            temp1.setProductQuantities(productQuantities);

            warehouses.add(temp1);
        }

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

    public Map<Integer, List<Product>> getOptimizedSolution(List<Warehouse1> warehouses , List<Product> order){
//        List<Warehouse1> warehouses = getDynamicWarehouses();
//        List<Product> order = getDynamicOrder();

        Instant start = Instant.now();

        Set<Product> notReachableProducts = new HashSet<>();

        setWarehousesById(warehouses);
        setProductsById(order);


        // warehouseId, List<product>
        Map<Integer, List<Product>> assignment = new HashMap<>();
        Map<Integer, List<Product>> solution = new HashMap<>();

        // List<warehouseId>
        Set<Integer> notSatisfiedWarehousesIds = new HashSet<>();


        // assign each product to the cheapest warehouse that can satisfy the quantity
        for(Product product : order){
            int requiredQuantity = product.getQuantity();

            while (requiredQuantity != 0){
                List<Warehouse1> availableWarehouses = getAvailableWarehouses(product, warehouses);
                Warehouse1 cheapestWarehouse = findCheapestWarehouse(product.getId(), availableWarehouses);

                if(cheapestWarehouse == null){
                    break;
                }

                Integer cheapestWarehouseQuantity = cheapestWarehouse.getProductQuantities().get(product.getId());

                Product availableProduct;

                if(cheapestWarehouseQuantity >= requiredQuantity){// the warehouse can satisfy the whole product quantity
                    availableProduct = new Product(product.getId(), requiredQuantity, product.getName());
                    cheapestWarehouse.getProductQuantities().put(product.getId(), cheapestWarehouseQuantity - requiredQuantity); // subtract the quantity from warehouse
                    requiredQuantity = 0;
                }else {
                    requiredQuantity -= cheapestWarehouseQuantity;
                    availableProduct = new Product(product.getId(), cheapestWarehouseQuantity, product.getName());
                    cheapestWarehouse.getProductQuantities().put(product.getId(), 0); // this warehouse now doesn't have any of this product
                }

                assignProductToWarehouse(cheapestWarehouse.getId(), availableProduct, assignment);
            }

            if(requiredQuantity == product.getQuantity()) // there isn't any warehouse to get this product from
                notReachableProducts.add(product);
        }

        // warehouseId, assignedOrderPrice
        Map<Integer, Double> warehousesOrderPrice = new HashMap<>();

        for (Integer warehouseId : assignment.keySet()){
            Warehouse1 warehouse = warehousesById.get(warehouseId);
            Double warehouseOrderPrice = getWarehouseOrderPrice(assignment, warehouse);
            warehousesOrderPrice.put(warehouse.getId(), warehouseOrderPrice);
            if(warehouseOrderPrice >= warehouse.getMinOrderPrice()){
                // finished
                solution.put(warehouseId, assignment.get(warehouseId));
            }else {
                // need to reassign
                notSatisfiedWarehousesIds.add(warehouse.getId());
            }
        }


        if(notSatisfiedWarehousesIds.isEmpty()){// all warehouses satisfy the criteria

            printSolution(solution, notReachableProducts, notSatisfiedWarehousesIds, assignment);

            Instant end = Instant.now();
            System.out.println("Duration in milli second: " + Duration.between(start, end).toMillis());

            return solution;
        }

        System.out.println("not satisfied warehouses Ids before: " + notSatisfiedWarehousesIds);


        // take the order of the highest difference (between order price and minOrderPrice) warehouse and add them to the least difference
        while (!notSatisfiedWarehousesIds.isEmpty() && notSatisfiedWarehousesIds.size() > 1){
            System.out.println("reassign");
//            reassignProducts(finalAssignment, assignment, notSatisfiedWarehousesIds, warehouses, warehousesOrderPrice);
            notReachableProducts =
                    reassignProductsWithNextCheapest(solution, assignment, notSatisfiedWarehousesIds, warehousesOrderPrice);
        }

        if(notSatisfiedWarehousesIds.size() == 1){
            Integer notSatisfiedWarehouse = notSatisfiedWarehousesIds.stream().toList().get(0);
            notReachableProducts.addAll(assignment.get(notSatisfiedWarehouse));
        }

        printSolution(solution, notReachableProducts, notSatisfiedWarehousesIds, assignment);

        Instant end = Instant.now();
        System.out.println("Duration in milli second: " + Duration.between(start, end).toMillis());

        return solution;
    }

    void printSolution(Map<Integer, List<Product>> solution, Set<Product> notReachableProducts,
                       Set<Integer> notSatisfiedWarehousesIds, Map<Integer, List<Product>> assignment){
        System.out.println("solution: ");
        for(Integer warehouseId : solution.keySet()){
            Warehouse1 warehouse = warehousesById.get(warehouseId);
            System.out.println("Warehouse: " + warehouse.getName() + " has the below products:");
            for(Product product : solution.get(warehouseId)){
                System.out.println("Product: " + product.getName() + " with quantity: "+ product.getQuantity());
            }
        }
        System.out.println();


        System.out.println("Not reachable Products: " + notReachableProducts);
        System.out.println();
        System.out.println("not satisfied warehouses:");
        for(Integer warehouseId : notSatisfiedWarehousesIds){
            Warehouse1 warehouse = warehousesById.get(warehouseId);
            System.out.println("warehouse: " + warehouse.getName() + " with products: " + assignment.get(warehouseId));
        }
        System.out.println();

        double totalCost = 0;
        for(Integer warehouseId : solution.keySet()){
            double warehouseCost = 0;
            Warehouse1 warehouse = warehousesById.get(warehouseId);
            for(Product product : solution.get(warehouseId)){
                warehouseCost += (product.getQuantity()*warehouse.getProductPrices().get(product.getId()));
            }
            totalCost += warehouseCost;
            System.out.println("Warehouse " + warehouse.getName() + " cost is: " + warehouseCost);
        }
        System.out.println("Total Cost :" + totalCost);
    }

    List<Warehouse1> getAvailableWarehouses(Product product, List<Warehouse1> warehouses){
        List<Warehouse1> availableWarehouse = new ArrayList<>();
        for (Warehouse1 warehouse : warehouses){
            if(warehouse.getProductQuantities().get(product.getId()) != null &&
                    warehouse.getProductQuantities().get(product.getId()) > 0)
                availableWarehouse.add(warehouse);
        }

        return availableWarehouse;
    }

    Warehouse1 findCheapestWarehouse(Integer productId, List<Warehouse1> warehouses){
        Warehouse1 cheapestWarehouse = null;
        double minPrice = Double.MAX_VALUE;

        for (Warehouse1 warehouse : warehouses) {
            Double price = warehouse.getProductPrices().get(productId);
            if(price == null)
                continue;
            if (price < minPrice) {
                minPrice = price;
                cheapestWarehouse = warehouse;
            }
        }

        return cheapestWarehouse;
    }

    void assignProductToWarehouse(Integer warehouseId, Product product, Map<Integer, List<Product>> assignment){
        if(assignment.containsKey(warehouseId))
            assignment.get(warehouseId).add(product);
        else {
            assignment.put(warehouseId, new ArrayList<>());
            assignment.get(warehouseId).add(product);
        }
    }

    Double getWarehouseOrderPrice(Map<Integer, List<Product>> assignment, Warehouse1 warehouse){
        double totalPrice = 0.0;
        for(Product product : assignment.get(warehouse.getId())){
            totalPrice += warehouse.getProductPrices().get(product.getId()) * product.getQuantity();
        }
        return totalPrice;
    }

    void  setWarehousesById(List<Warehouse1> warehouses){
        warehouses.forEach(w -> warehousesById.put(w.getId(), w));
    }
    void  setProductsById(List<Product> products){
        products.forEach(p -> productsById.put(p.getId(), p));
    }

    Set<Product> reassignProductsWithNextCheapest(Map<Integer, List<Product>> solution, Map<Integer, List<Product>> assignment,
                                                  Set<Integer> notSatisfiedWarehousesIds, Map<Integer, Double> warehousesOrderPrice){
        List<Warehouse1> notSatisfiedWarehouses = new ArrayList<>();

        for(Integer warehouseId : notSatisfiedWarehousesIds){
            notSatisfiedWarehouses.add(warehousesById.get(warehouseId));
        }

        // Sort the notSatisfiedWarehouses by how close they are to reaching their minOrderPrice
        notSatisfiedWarehouses.sort(Comparator.comparingDouble(warehouse -> {
            // Get the total order price for the warehouse
            double totalOrderPrice = warehousesOrderPrice.get(warehouse.getId());
            // Calculate the difference to the minOrderPrice
            return warehouse.getMinOrderPrice() - totalOrderPrice;
        }));


        List<Product> leastSatisfiedWarehouseProducts
                = assignment.get(notSatisfiedWarehouses.get(notSatisfiedWarehouses.size()-1).getId());

        Set<Product> notReachableProducts = new HashSet<>();
        Warehouse1 leastSatisfiedWarehouse = notSatisfiedWarehouses.get(notSatisfiedWarehouses.size() - 1);

        // Remove the least satisfied warehouse from future consideration
        notSatisfiedWarehousesIds.remove(leastSatisfiedWarehouse.getId());
        notSatisfiedWarehouses.remove(notSatisfiedWarehouses.size()-1);

        for(Product product : leastSatisfiedWarehouseProducts){
            Integer requiredQuantity = product.getQuantity();

            while (requiredQuantity != 0){
                List<Warehouse1> availableWarehouses = getAvailableWarehouses(product, notSatisfiedWarehouses);
                Warehouse1 secondCheapestWarehouse = findCheapestWarehouse(product.getId(), availableWarehouses);
                if(secondCheapestWarehouse == null){
                    notReachableProducts.add(new Product(product.getId(), requiredQuantity, product.getName()));
                    break;
                }

                Integer secondCheapestAvailableQuantity = secondCheapestWarehouse.getProductQuantities().get(product.getId());

                Product availableProduct;
                if(secondCheapestAvailableQuantity >= requiredQuantity){// subtract the quantity from warehouse
                    availableProduct = new Product(product.getId(), requiredQuantity, product.getName());
                    secondCheapestWarehouse.getProductQuantities().put(product.getId(), secondCheapestAvailableQuantity - requiredQuantity);
                    requiredQuantity = 0;
                }else {
                    requiredQuantity -= secondCheapestAvailableQuantity;
                    availableProduct = new Product(product.getId(), secondCheapestAvailableQuantity, product.getName());
                    secondCheapestWarehouse.getProductQuantities().put(product.getId(), 0);
                }


                List<Product> secondCheapestWarehouseProducts = assignment.get(secondCheapestWarehouse.getId());

                // check if this product already has a quantity in this warehouse and increase it
                Product oldProduct = secondCheapestWarehouseProducts.stream().filter(p -> p.getId() == availableProduct.getId()).findFirst().orElse(null);
                if(oldProduct == null)
                    secondCheapestWarehouseProducts.add(availableProduct);
                else
                    oldProduct.setQuantity(oldProduct.getQuantity() + availableProduct.getQuantity());

                assignment.put(secondCheapestWarehouse.getId(), secondCheapestWarehouseProducts);

                // Update the warehouse's order price after reassignment
                double updatedOrderPrice = warehousesOrderPrice.get(secondCheapestWarehouse.getId())
                        + availableProduct.getQuantity() * secondCheapestWarehouse.getProductPrices().get(availableProduct.getId());
                warehousesOrderPrice.put(secondCheapestWarehouse.getId(), updatedOrderPrice);
            }
        }

        // check if any warehouse now is satisfied
        for(Warehouse1 warehouse : notSatisfiedWarehouses){
            double orderPrice = warehousesOrderPrice.get(warehouse.getId());
            if(orderPrice >= warehouse.getMinOrderPrice()){
                solution.put(warehouse.getId(), assignment.get(warehouse.getId()));
                notSatisfiedWarehousesIds.remove(warehouse.getId());
            }
        }

        return notReachableProducts;
    }
}
