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
import org.springframework.stereotype.Service;
import com.google.ortools.Loader;

import java.util.*;

@Service
public class OrToolsService {
    @Autowired
    OptimizerService1 optimizerService1;

    @Autowired
    CbcSolver cbcSolver;

    public void solve() {

        Loader.loadNativeLibraries();
        // إعداد البيانات
        int numWarehouses = 2;
        int numProducts = 2;
        // تعريف البيانات
        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
        String[] products = {"Product A", "Product B"};

        // سعة التخزين لكل منتج في كل مخزن
        int[][] storageCapacity = {{100, 150}, // Warehouse 1 capacities for Product A and B
                {200, 100}  // Warehouse 2 capacities for Product A and B
        };

        // أسعار المنتجات في كل مخزن
        double[][] prices = {{10, 20}, // Warehouse 1 prices for Product A and B
                {15, 25}  // Warehouse 2 prices for Product A and B
        };

        // الحد الأدنى للسعر المطلوب في كل مخزن
        double[] minPrice = {1000, 1500};

        // الكميات المطلوبة من كل منتج
        int[] demand = {80, 70};

        // إنشاء Solver
        MPSolver solver = MPSolver.createSolver("SCIP");
//        MPSolver solver = MPSolver.createSolver("GLOP");

        // المتغيرات: عدد الوحدات المطلوبة من كل منتج في كل مخزن
        MPVariable[][] x = new MPVariable[warehouses.length][products.length];
        for (int i = 0; i < warehouses.length; i++) {
            for (int j = 0; j < products.length; j++) {
                x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
            }
        }

        // دالة الهدف: تقليل التكلفة الإجمالية
        MPObjective objective = solver.objective();
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                objective.setCoefficient(x[i][j], prices[i][j]);
            }
        }
        objective.setMinimization();

        // قيود السعة لكل مخزن
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                MPConstraint capacityConstraint = solver.makeConstraint(0, storageCapacity[i][j]);
                capacityConstraint.setCoefficient(x[i][j], 1);
            }
        }

        // قيود الحد الأدنى لسعر الطلبية لكل مخزن
        for (int i = 0; i < numWarehouses; i++) {
            MPConstraint minPriceConstraint = solver.makeConstraint(minPrice[i], Double.POSITIVE_INFINITY);
            for (int j = 0; j < numProducts; j++) {
                minPriceConstraint.setCoefficient(x[i][j], prices[i][j]);
            }
        }

        // قيود الطلبات المطلوبة
        for (int j = 0; j < numProducts; j++) {
//            MPConstraint demandConstraint = solver.makeConstraint(demand[j], Double.POSITIVE_INFINITY);
            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j]);
            for (int i = 0; i < numWarehouses; i++) {
                demandConstraint.setCoefficient(x[i][j], 1);
            }
        }

        // حل النموذج
        ResultStatus resultStatus = solver.solve();

        // التحقق من الحل
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Optimal solution found:");
            for (int i = 0; i < warehouses.length; i++) {
                for (int j = 0; j < products.length; j++) {
                    System.out.printf("%s from %s: %.0f units%n", products[j], warehouses[i], x[i][j].solutionValue());
                }
            }
            System.out.printf("Total cost: %.2f%n", solver.objective().value());
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }

    }

    public List<Product> getDynamicOrder(){
        List<Product> order = new ArrayList<>();
        Random r = new Random();
        for(int i = 0 ; i < 5 ; i++){
            // required quantity between 1 : 200
//            order.add(new Product(i, r.nextInt(200 - 1) + 1));
        }
        return order;
    }

    List<Warehouse1> getDynamicWarehouses(){
        List<Warehouse1> warehouses = new ArrayList<>();

        Random r = new Random();

        for(int i = 0 ; i < 5 ; i ++){
            Warehouse1 temp1 = new Warehouse1();
            temp1.setId(i);
            // min order price between 0.5M : 5M
            temp1.setMinOrderPrice(r.nextInt(5000 - 1000) + 1000);
            Map<Integer, Double> productPrices = new HashMap<>();

            Set<Integer> selectedProducts = new HashSet<>();
            for(int j = 0; j < 5 ; j++){
                // product id between 1 : 2000
                Integer productId = j;//r.nextInt(5 - 1) + 1;
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

    public List<Warehouse1> getWarehouses(){
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

    public List<Product> getOrder(){
        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 5));
        order.add(new Product(2, 5));
        order.add(new Product(3, 5));
        order.add(new Product(4, 5));
        return order;
    }

    public void solveWithMinOrderPrice4() {
        Loader.loadNativeLibraries();
        int numWarehouses = 2;
        int numProducts = 2;
        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
        List<String> products = new ArrayList<>();
        products.add("Product A");
        products.add("Product B");

        // Storage capacity for each product in each warehouse
        int[][] storageCapacity = {
                {10, 50}, // Warehouse 1 capacities for Product A and B
                {30, 50}  // Warehouse 2 capacities for Product A and B
        };

        // Prices for products in each warehouse
        double[][] prices = {
                {10, 20}, // Warehouse 1 prices for Product A and B
                {15, 25}  // Warehouse 2 prices for Product A and B
        };

        // Demand for each product
        int[] demand = {80, 100};

        // Minimum price required for each warehouse
        double[] minPrice = {1000, 1500};

        MPSolver solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");
        if (solver == null) {
            System.out.println("Could not create solver CBC_MIXED_INTEGER_PROGRAMMING");
            return;
        }

        // Check available products
        for (int j = 0; j < numProducts; j++) {
            boolean productAvailable = false;
            for (int i = 0; i < numWarehouses; i++) {
                if (storageCapacity[i][j] > 0) {
                    productAvailable = true;
                    break;
                }
            }
            if (!productAvailable) {
                demand[j] = 0; // Set demand to 0 for unavailable products
            }
        }

        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
            }
        }

        MPVariable[] unmetDemand = new MPVariable[numProducts];
        for (int j = 0; j < numProducts; j++) {
            unmetDemand[j] = solver.makeNumVar(0, demand[j], "unmetDemand_" + j);
        }

        MPObjective objective = solver.objective();
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                objective.setCoefficient(x[i][j], prices[i][j]);
            }
        }

        double penaltyForUnmetDemand = 1000;
        for (int j = 0; j < numProducts; j++) {
            objective.setCoefficient(unmetDemand[j], penaltyForUnmetDemand);
        }

        objective.setMinimization();

        for (int j = 0; j < numProducts; j++) {
            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
            for (int i = 0; i < numWarehouses; i++) {
                demandConstraint.setCoefficient(x[i][j], 1);
            }
            demandConstraint.setCoefficient(unmetDemand[j], 1);
        }

        // Solve the problem initially
        ResultStatus resultStatus = solver.solve();

        // Check if any warehouse total order price is less than minimum required
        boolean rerunRequired = false;
        for (int i = 0; i < numWarehouses; i++) {
            double totalPrice = 0;
            for (int j = 0; j < numProducts; j++) {
                totalPrice += x[i][j].solutionValue() * prices[i][j];
            }
            if (totalPrice < minPrice[i]) {
                System.out.printf("%s does not meet the minimum order price. Excluding it.%n", warehouses[i]);
                // Set storage capacity to zero to exclude the warehouse
                for (int j = 0; j < numProducts; j++) {
                    storageCapacity[i][j] = 0;
                }
                rerunRequired = true;
            }
        }

        // Rerun solver if any warehouse was excluded
        if (rerunRequired) {
            System.out.println("Rerunning solver after excluding warehouses...");

            // Create a new solver instance
            solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");

            // Recreate decision variables with updated storage capacity
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
                }
            }

            for (int j = 0; j < numProducts; j++) {
                unmetDemand[j] = solver.makeNumVar(0, demand[j], "unmetDemand_" + j);
            }

            objective = solver.objective();
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    objective.setCoefficient(x[i][j], prices[i][j]);
                }
            }

            for (int j = 0; j < numProducts; j++) {
                objective.setCoefficient(unmetDemand[j], penaltyForUnmetDemand);
            }

            objective.setMinimization();

            for (int j = 0; j < numProducts; j++) {
                MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
                for (int i = 0; i < numWarehouses; i++) {
                    demandConstraint.setCoefficient(x[i][j], 1);
                }
                demandConstraint.setCoefficient(unmetDemand[j], 1);
            }

            // Solve again after exclusion
            resultStatus = solver.solve();
        }

        // Check and print the solution
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Optimal solution found:");
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    if (x[i][j].solutionValue() > 0) {
                        System.out.printf("%s from %s: %.0f units%n", products.get(j), warehouses[i], x[i][j].solutionValue());
                    }
                }
            }
            System.out.printf("Total cost: %.2f%n", solver.objective().value());
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }

        System.out.println("Advanced usage:");
        System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
        System.out.println("Problem solved in " + solver.iterations() + " iterations");
    }

    public void solveWithMinOrderPrice() {
        Loader.loadNativeLibraries();
        int numWarehouses = 2;
        int numProducts = 2;
        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
        List<String> products = new ArrayList<>();
        products.add("Product A");
        products.add("Product B");



        // check if the product does not exist at all warehouses and exclude it by setting the demand equal zero
        // check if the product quantity for warehouses does not meet demand quantity then get all the quantity for all warehouses and minimize the order price by adding penalty for undemanding products
        // check if the total order price for each warehouse less than the minimum order price for each warehouse I want to exclude the further one to match the constraint of the minimum order price and rerun the solver again
        // Storage capacity for each product in each warehouse
        int[][] storageCapacity = {
                {10, 50}, // Warehouse 1 capacities for Product A and B
                {30, 50}  // Warehouse 2 capacities for Product A and B
        };

        // Prices for products in each warehouse
        double[][] prices = {
                {10, 20}, // Warehouse 1 prices for Product A and B
                {15, 25}  // Warehouse 2 prices for Product A and B
        };

        // Demand for each product
        int[] demand = {80, 100};
        // Minimum price required for each warehouse
        double[] minPrice = {1000, 1500};
//        int numWarehouses = 2;
//        int numProducts = 1;
//
//        // Define data
//        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
//        String[] products = {"Product A"};
//
//        // Storage capacity for each product in each warehouse
//        int[][] storageCapacity = {{100}, {0}};
//
//        // Prices of products in each warehouse
//        double[][] prices = {{10}, {5}};
//
//        // Minimum order price required for each warehouse
//        double[] minPrice = {1000, 500};
//
//        // Quantity required for each product
//        int[] demand = {100};

        // Create Solver
        MPSolver solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");
        if (solver == null) {
            System.out.println("Could not create solver CBC_MIXED_INTEGER_PROGRAMMING");
            return;
        }


        // Check available products
        for (int j = 0; j < numProducts; j++) {
            boolean productAvailable = false;
            for (int i = 0; i < numWarehouses; i++) {
                if (storageCapacity[i][j] > 0) {
                    productAvailable = true;
                    break;
                }
            }
            if (!productAvailable) {
                demand[j] = 0; // Set demand to 0 for unavailable products
            }
        }


        // Decision variables
        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
            }
        }

        // Additional variables for unmet demand (slack variables)
        MPVariable[] unmetDemand = new MPVariable[numProducts];
        for (int j = 0; j < numProducts; j++) {
            unmetDemand[j] = solver.makeNumVar(0, demand[j], "unmetDemand_" + j);
        }


        // Objective function: Minimize total cost
        MPObjective objective = solver.objective();
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                objective.setCoefficient(x[i][j], prices[i][j]);
            }
        }


        // Penalty for unmet demand (i.e., maximize quantity supplied)
        // Use a large penalty to force the solver to fulfill as much demand as possible
        // 1000 * unMetDemandP2 + 1000 * unMetDemandP1 is added to the objective to be minimized
        double penaltyForUnmetDemand = 1000;  // Increase this penalty to ensure unmet demand is discouraged
        for (int j = 0; j < numProducts; j++) {
            objective.setCoefficient(unmetDemand[j], penaltyForUnmetDemand);  // Penalize unmet demand heavily
        }

        // Objective function: Minimize total cost
        objective.setMinimization();

//        // Demand constraints
//        for (int j = 0; j < numProducts; j++) {
//            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
//            for (int i = 0; i < numWarehouses; i++) {
//                demandConstraint.setCoefficient(x[i][j], 1);
//            }
//        }
        // Demand constraints: total supplied + unmet demand = total demand
        for (int j = 0; j < numProducts; j++) {
            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
            for (int i = 0; i < numWarehouses; i++) { // x11(50) + x12(50) = demand of p1
                demandConstraint.setCoefficient(x[i][j], 1);  // Sum of all supplies should be part of the demand
            }
            // x11 + x12 + unmet of p1 = demand of p1
            demandConstraint.setCoefficient(unmetDemand[j], 1);  // Add unmet demand as part of the equation
        }
//        // Minimum price constraints: ensure the total cost of products in each warehouse meets the minimum price
//        for (int i = 0; i < numWarehouses; i++) {
//            MPConstraint minPriceConstraint = solver.makeConstraint(minPrice[i], Double.POSITIVE_INFINITY, "minPrice_" + i);
//            for (int j = 0; j < numProducts; j++) {
//                minPriceConstraint.setCoefficient(x[i][j], prices[i][j]);
//            }
//        }

//        // Capacity constraints
//        for (int i = 0; i < numWarehouses; i++) {
//            for (int j = 0; j < numProducts; j++) {
//                MPConstraint capacityConstraint = solver.makeConstraint(0, storageCapacity[i][j], "capacity_" + i + "_" + j);
//                capacityConstraint.setCoefficient(x[i][j], 1);
//            }
//        }

//
//        // Minimum order price constraints with release condition
//        for (int i = 0; i < numWarehouses; i++) {
//            // Create a variable to track if the minimum price condition is met
//            MPVariable meetsMinPrice = solver.makeBoolVar("meetsMinPrice_" + i);
//
//            // Sum of units sourced multiplied by their price to ensure minimum price
//            MPConstraint minPriceConstraint = solver.makeConstraint(0, Double.POSITIVE_INFINITY, "min_price_" + i);
//            for (int j = 0; j < numProducts; j++) {
//                minPriceConstraint.setCoefficient(x[i][j], prices[i][j]);
//            }
//            minPriceConstraint.setCoefficient(meetsMinPrice, -minPrice[i]); // If the total price is less than minPrice, this variable is false
//
//            // Allow sourcing from this warehouse only if minimum price is met
//            for (int j = 0; j < numProducts; j++) {
//                // Ensure unique constraint name by including both warehouse and product indices
//                MPConstraint releaseMinPrice = solver.makeConstraint(0, Double.POSITIVE_INFINITY, "release_min_price_" + i + "_" + j);
//                releaseMinPrice.setCoefficient(x[i][j], 1); // Allow sourcing from the warehouse itself
//
//                // Add conditions to allow sourcing from other warehouses if minimum price is not met
//                for (int k = 0; k < numWarehouses; k++) {
//                    if (k != i) {
//                        releaseMinPrice.setCoefficient(x[k][j], 1); // Allow sourcing from other warehouses
//                    }
//                }
//            }
//        }


        // Solve the problem
        ResultStatus resultStatus = solver.solve();

        // Check and print the solution
        if (resultStatus == ResultStatus.OPTIMAL) {
            double total = 0.0;
            System.out.println("Optimal solution found:");
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    if (x[i][j].solutionValue() > 0) {
                        total+=  x[i][j].solutionValue() * prices[i][j];
                        System.out.printf("%s from %s: %.0f units * price : %.0f =  %.0f %n  ", products.get(j), warehouses[i], x[i][j].solutionValue(),prices[i][j],total);
                    }
                }
            }
            System.out.printf("Total cost: %.2f%n", total);
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }

        System.out.println("Advanced usage:");
        System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
        System.out.println("Problem solved in " + solver.iterations() + " iterations");
    }
    public void solveWithMinOrderPrice2() {
        Loader.loadNativeLibraries();
        int numWarehouses = 2;
        int numProducts = 2;
        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
        List<String> products = new ArrayList<>();
        products.add("Product A");
        products.add("Product B");

        // Storage capacity for each product in each warehouse
        int[][] storageCapacity = {
                {0, 150}, // Warehouse 1 capacities for Product A and B
                {0, 100}  // Warehouse 2 capacities for Product A and B
        };

        // Prices for products in each warehouse
        double[][] prices = {
                {10, 20}, // Warehouse 1 prices for Product A and B
                {15, 25}  // Warehouse 2 prices for Product A and B
        };

        // Demand for each product
        int[] demand = {80, 70};
        // Minimum price required for each warehouse
        double[] minPrice = {1000, 1500};
//        int numWarehouses = 2;
//        int numProducts = 1;
//
//        // Define data
//        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
//        String[] products = {"Product A"};
//
//        // Storage capacity for each product in each warehouse
//        int[][] storageCapacity = {{100}, {0}};
//
//        // Prices of products in each warehouse
//        double[][] prices = {{10}, {5}};
//
//        // Minimum order price required for each warehouse
//        double[] minPrice = {1000, 500};
//
//        // Quantity required for each product
//        int[] demand = {100};

        // Create Solver
        MPSolver solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");
        if (solver == null) {
            System.out.println("Could not create solver CBC_MIXED_INTEGER_PROGRAMMING");
            return;
        }

        // Demand constraints
        // Check available products
        for (int j = 0; j < numProducts; j++) {
            boolean productAvailable = false;
            for (int i = 0; i < numWarehouses; i++) {
                if (storageCapacity[i][j] > 0) {
                    productAvailable = true;
                    break;
                }
            }
            if (!productAvailable) {
                demand[j] = 0; // Set demand to 0 for unavailable products
            }
        }


        // Decision variables
        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
            }
        }

        // Objective function: Minimize total cost
        MPObjective objective = solver.objective();
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                objective.setCoefficient(x[i][j], prices[i][j]);
            }
        }
        // Objective function: Minimize total cost
        objective.setMinimization();

        // Demand constraints
        for (int j = 0; j < numProducts; j++) {
            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
            for (int i = 0; i < numWarehouses; i++) {
                demandConstraint.setCoefficient(x[i][j], 1);
            }
        }

        // Capacity constraints
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                MPConstraint capacityConstraint = solver.makeConstraint(0, storageCapacity[i][j], "capacity_" + i + "_" + j);
                capacityConstraint.setCoefficient(x[i][j], 1);
            }
        }
        // Minimum order price constraints with release condition
        for (int i = 0; i < numWarehouses; i++) {
            // Create a variable to track if the minimum price condition is met
            MPVariable meetsMinPrice = solver.makeBoolVar("meetsMinPrice_" + i);

            // Sum of units sourced multiplied by their price to ensure minimum price
            MPConstraint minPriceConstraint = solver.makeConstraint(0, Double.POSITIVE_INFINITY, "min_price_" + i);
            for (int j = 0; j < numProducts; j++) {
                minPriceConstraint.setCoefficient(x[i][j], prices[i][j]);
            }
            minPriceConstraint.setCoefficient(meetsMinPrice, -minPrice[i]); // If the total price is less than minPrice, this variable is false

            // Allow sourcing from this warehouse only if minimum price is met
            for (int j = 0; j < numProducts; j++) {
                // Ensure unique constraint name by including both warehouse and product indices
                MPConstraint releaseMinPrice = solver.makeConstraint(0, Double.POSITIVE_INFINITY, "release_min_price_" + i + "_" + j);
                releaseMinPrice.setCoefficient(x[i][j], 1); // Allow sourcing from the warehouse itself

                // Add conditions to allow sourcing from other warehouses if minimum price is not met
                for (int k = 0; k < numWarehouses; k++) {
                    if (k != i) {
                        releaseMinPrice.setCoefficient(x[k][j], 1); // Allow sourcing from other warehouses
                    }
                }
            }
        }
        // Solve the problem
        ResultStatus resultStatus = solver.solve();

        // Check and print the solution
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Optimal solution found:");
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    if (x[i][j].solutionValue() > 0) {
                        System.out.printf("%s from %s: %.0f units%n", products.get(j), warehouses[i], x[i][j].solutionValue());
                    }
                }
            }
            System.out.printf("Total cost: %.2f%n", solver.objective().value());
        } else if (resultStatus == ResultStatus.FEASIBLE) {
            System.out.println("Feasible solution found, but it may not be optimal:");
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    if (x[i][j].solutionValue() > 0) {
                        System.out.printf("%s from %s: %.0f units%n", products.get(j), warehouses[i], x[i][j].solutionValue());
                    }
                }
            }
            System.out.printf("Total cost: %.2f%n", solver.objective().value());
        } else if (resultStatus == ResultStatus.INFEASIBLE) {
            System.out.println("No feasible solution exists. Consider relaxing some constraints.");
        } else if (resultStatus == ResultStatus.UNBOUNDED) {
            System.out.println("The problem is unbounded. Check if all constraints are correctly defined.");
        } else if (resultStatus == ResultStatus.ABNORMAL) {
            System.out.println("An abnormal condition occurred during solving. Check the problem setup or solver configuration.");
        } else if (resultStatus == ResultStatus.NOT_SOLVED) {
            System.out.println("The solver did not solve the problem. Try increasing the time limit or adjusting the problem complexity.");
        } else {
            System.out.println("Solver returned an unknown status.");
        }
        System.out.println("Advanced usage:");
        System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
        System.out.println("Problem solved in " + solver.iterations() + " iterations");
    }

    public void solve2() {
        Loader.loadNativeLibraries();
        // إعداد البيانات
        int numWarehouses = 2;
        int numProducts = 1;
        // تعريف البيانات
        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
        String[] products = {"Product A"};

        // سعة التخزين لكل منتج في كل مخزن
        int[][] storageCapacity = {{100}, // Warehouse 1 capacities for Product A and B
                {200}  // Warehouse 2 capacities for Product A and B
        };

        // أسعار المنتجات في كل مخزن
        double[][] prices = {{10}, // Warehouse 1 prices for Product A and B
                {15}  // Warehouse 2 prices for Product A and B
        };

        // الحد الأدنى للسعر المطلوب في كل مخزن
        double[] minPrice = {1000, 1500};

        // الكميات المطلوبة من كل منتج
        int[] demand = {100};

        // Input data
//        int numWarehouses = 2;
//        int numProducts = 2;
//        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
//        String[] products = {"Product A", "Product B"};
//
//        // Storage capacity for each product in each warehouse
//        int[][] storageCapacity = {
//                {0, 150}, // Warehouse 1 capacities for Product A and B
//                {0, 100}  // Warehouse 2 capacities for Product A and B
//        };
//
//        // Prices for products in each warehouse
//        double[][] prices = {
//                {10, 20}, // Warehouse 1 prices for Product A and B
//                {15, 25}  // Warehouse 2 prices for Product A and B
//        };
//
//        // Demand for each product
//        int[] demand = {0, 70};
//        // Minimum price required for each warehouse
//        double[] minPrice = {1000, 1500};
//        GLOP is a linear programming solver,
//   it may not always handle constraints related to integer values (such as exact capacity or unit constraints)
//   as well as a Mixed Integer Programming (MIP) solver like CBC.
//      Switching to CBC can sometimes give you more flexibility in handling complex constraints
        // Create Solver (Switching to CBC solver)
        MPSolver solver = MPSolver.createSolver("GLOP");
//        MPSolver solver = MPSolver.createSolver("GLOP");
        if (solver == null) {
            System.out.println("Could not create solver CBC_MIXED_INTEGER_PROGRAMMING");
            return;
        }

        // Decision variables: number of units sourced from each warehouse
        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
            }
        }

        // Objective function: minimize total cost
        MPObjective objective = solver.objective();
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                objective.setCoefficient(x[i][j], prices[i][j]);
            }
        }
        objective.setMinimization();

        // Demand constraints: ensure full demand for each product is met
        // x11 + x21 + x31 = demand1
        for (int j = 0; j < numProducts; j++) {
            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
            for (int i = 0; i < numWarehouses; i++) {
                demandConstraint.setCoefficient(x[i][j], 1);
            }
        }

        // Capacity constraints: do not exceed warehouse capacity for each product
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                MPConstraint capacityConstraint = solver.makeConstraint(0, storageCapacity[i][j], "capacity_" + i + "_" + j);
                capacityConstraint.setCoefficient(x[i][j], 1);
            }
        }
//

        // Minimum price constraints: ensure the total cost of products in each warehouse meets the minimum price
        for (int i = 0; i < numWarehouses; i++) {
            MPConstraint minPriceConstraint = solver.makeConstraint(minPrice[i], Double.POSITIVE_INFINITY, "minPrice_" + i);
            for (int j = 0; j < numProducts; j++) {
                minPriceConstraint.setCoefficient(x[i][j], prices[i][j]);
            }
        }

        // Solve the problem
        ResultStatus resultStatus = solver.solve();

        // Check and print the solution
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Optimal solution found:");
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    if (x[i][j].solutionValue() > 0) {
                        System.out.printf("%s from %s: %.0f units%n", products[j], warehouses[i], x[i][j].solutionValue());
                    }
                }
            }
            System.out.printf("Total cost: %.2f%n", solver.objective().value());
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }

        System.out.println("Advanced usage:");

        System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");

        System.out.println("Problem solved in " + solver.iterations() + " iterations");
    }
    public void solve4() {
        Loader.loadNativeLibraries();
        // إعداد البيانات
        int numWarehouses = 2;
        int numProducts = 1;

        // تعريف البيانات
        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
        String[] products = {"Product A"};

        // سعة التخزين لكل منتج في كل مخزن
        int[][] storageCapacity = {{100}, // Warehouse 1 capacities for Product A
                {200}  // Warehouse 2 capacities for Product A
        };

        // أسعار المنتجات في كل مخزن
        double[][] prices = {{10}, // Warehouse 1 prices for Product A
                {15}  // Warehouse 2 prices for Product A
        };

        // الحد الأدنى للسعر المطلوب في كل مخزن
        double[] minPrice = {1000, 1500};

        // الكميات المطلوبة من كل منتج
        int[] demand = {100};

        // Create Solver (Switching to CBC solver)
        MPSolver solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");
        if (solver == null) {
            System.out.println("Could not create solver CBC_MIXED_INTEGER_PROGRAMMING");
            return;
        }

        // Decision variables: number of units sourced from each warehouse
        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
            }
        }

        // Objective function: minimize total cost
        MPObjective objective = solver.objective();
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                objective.setCoefficient(x[i][j], prices[i][j]);
            }
        }
        objective.setMinimization();

        // Demand constraints: ensure full demand for each product is met
        for (int j = 0; j < numProducts; j++) {
            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
            for (int i = 0; i < numWarehouses; i++) {
                demandConstraint.setCoefficient(x[i][j], 1);
            }
        }

        // Capacity constraints: do not exceed warehouse capacity for each product
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                MPConstraint capacityConstraint = solver.makeConstraint(0, storageCapacity[i][j], "capacity_" + i + "_" + j);
                capacityConstraint.setCoefficient(x[i][j], 1);
            }
        }

        // Minimum price constraints: ensure the total cost of products in each warehouse meets the minimum price
        for (int i = 0; i < numWarehouses; i++) {
            MPConstraint minPriceConstraint = solver.makeConstraint(minPrice[i], Double.POSITIVE_INFINITY, "minPrice_" + i);
            for (int j = 0; j < numProducts; j++) {
                minPriceConstraint.setCoefficient(x[i][j], prices[i][j]);
            }
        }

        // Solve the problem
        ResultStatus resultStatus = solver.solve();

        // Check and print the solution
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Optimal solution found:");
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    if (x[i][j].solutionValue() > 0) {
                        System.out.printf("%s from %s: %.0f units%n", products[j], warehouses[i], x[i][j].solutionValue());
                    }
                }
            }
            System.out.printf("Total cost: %.2f%n", solver.objective().value());
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }

        System.out.println("Advanced usage:");
        System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
        System.out.println("Problem solved in " + solver.iterations() + " iterations");
    }
    public void solve5() {
        Loader.loadNativeLibraries();
//        // إعداد البيانات
        int numWarehouses = 2;
        int numProducts = 1;

// تعريف البيانات
        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
        String[] products = {"Product A"};

// سعة التخزين لكل منتج في كل مخزن
        int[][] storageCapacity = {{100}, // Warehouse 1 capacities for Product A
                {200}  // Warehouse 2 capacities for Product A
        };

// أسعار المنتجات في كل مخزن
        double[][] prices = {{10}, // Warehouse 1 prices for Product A
                {25}  // Warehouse 2 prices for Product A
        };

// الحد الأدنى للسعر المطلوب في كل مخزن
        double[] minPrice = {1000, 1500};

        // الكميات المطلوبة من كل منتج
        int[] demand = {100};

        // الحد الأدنى للطلب من كل مخزن
        int[] minOrder = {100, 150}; // الحد الأدنى للطلب لكل مخزن
        // Create Solver (Switching to CBC solver)
        MPSolver solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");
        if (solver == null) {
            System.out.println("Could not create solver CBC_MIXED_INTEGER_PROGRAMMING");
            return;
        }

        // Decision variables: number of units sourced from each warehouse
        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
            }
        }

        // Objective function: minimize total cost
        MPObjective objective = solver.objective();
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                objective.setCoefficient(x[i][j], prices[i][j]);
            }
        }
        objective.setMinimization();

        // Demand constraints: ensure full demand for each product is met
        for (int j = 0; j < numProducts; j++) {
            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
            for (int i = 0; i < numWarehouses; i++) {
                demandConstraint.setCoefficient(x[i][j], 1);
            }
        }

        // Capacity constraints: do not exceed warehouse capacity for each product
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                MPConstraint capacityConstraint = solver.makeConstraint(0, storageCapacity[i][j], "capacity_" + i + "_" + j);
                capacityConstraint.setCoefficient(x[i][j], 1);
            }
        }

// قيود الحد الأدنى للسعر
        for (int j = 0; j < numProducts; j++) {
            for (int i = 0; i < numWarehouses; i++) {
                MPConstraint minPriceConstraint = solver.makeConstraint(0, Double.POSITIVE_INFINITY, "min_price_" + i + "_" + j);
                minPriceConstraint.setCoefficient(x[i][j], prices[i][j]);
                minPriceConstraint.setBounds(minPrice[i], Double.POSITIVE_INFINITY);
            }
        }
        // Solve the problem
        ResultStatus resultStatus = solver.solve();

        // Check and print the solution
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Optimal solution found:");
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    if (x[i][j].solutionValue() > 0) {
                        System.out.printf("%s from %s: %.0f units%n", products[j], warehouses[i], x[i][j].solutionValue());
                    }
                }
            }
            System.out.printf("Total cost: %.2f%n", solver.objective().value());
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }

        System.out.println("Advanced usage:");
        System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
        System.out.println("Problem solved in " + solver.iterations() + " iterations");
    }

    public void solve3() {
        Loader.loadNativeLibraries();

        List<Warehouse1> warehouseList =  getWarehouses();
        int numWarehouses = warehouseList.size();
        List<Product> orderList = getOrder();
        int numProducts = orderList.size();

        // Create the solver
        MPSolver solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");
        if (solver == null) {
            System.out.println("Could not create solver CBC_MIXED_INTEGER_PROGRAMMING");
            return;
        }

        // Decision variables: number of units sourced from each warehouse
        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
        for (int i = 0; i < numWarehouses; i++) {
            int j = 0;
            for (Map.Entry<Integer,Integer> productQty : warehouseList.get(i).getProductQuantities().entrySet()) {
                x[i][j] = solver.makeNumVar(0, warehouseList.get(i).getProductQuantities().get(productQty.getKey()),
                        "x_" + i + "_" + productQty.getKey());
                j++;
            }
        }

        // Additional variables for unmet demand (slack variables)
        MPVariable[] unmetDemand = new MPVariable[numProducts];
        for (int j = 0; j < numProducts; j++) {
            unmetDemand[j] = solver.makeNumVar(0, orderList.get(j).getQuantity(), "unmetDemand_" + j);
        }

        // Objective function: minimize total cost + large penalty for unmet demand
        MPObjective objective = solver.objective();

        // Minimize the total cost
        for (int i = 0; i < numWarehouses; i++) {
            int j = 0;
            for (Map.Entry<Integer,Double> productPrice : warehouseList.get(i).getProductPrices().entrySet()) {
                objective.setCoefficient(x[i][j], warehouseList.get(i).getProductPrices().get(productPrice.getKey()));  // Add cost of sourcing from each warehouse
                j++;
            }
        }

        // Penalty for unmet demand (i.e., maximize quantity supplied)
        // Use a large penalty to force the solver to fulfill as much demand as possible
        // 1000 * unMetDemandP2 + 1000 * unMetDemandP1 is added to the objective to be minimized
        double penaltyForUnmetDemand = 1000;  // Increase this penalty to ensure unmet demand is discouraged
        for (int j = 0; j < numProducts; j++) {
            objective.setCoefficient(unmetDemand[j], penaltyForUnmetDemand);  // Penalize unmet demand heavily
        }

        // now objective function is 10x11 + 20x12 + 15x21 + 25x22 + 1 * unMetDemandP2 + 1 * unMetDemandP1
        objective.setMinimization();  // Set to minimize the total objective

        // Demand constraints: total supplied + unmet demand = total demand
        for (int j = 0; j < numProducts; j++) {
            MPConstraint demandConstraint = solver.makeConstraint(orderList.get(j).getQuantity(), orderList.get(j).getQuantity(), "demand_" + j);
            for (int i = 0; i < numWarehouses; i++) { // x11(50) + x12(50) = demand of p1
                demandConstraint.setCoefficient(x[i][j], 1);  // Sum of all supplies should be part of the demand
            }
            // x11 + x12 + unmet of p1 = demand of p1
            demandConstraint.setCoefficient(unmetDemand[j], 1);  // Add unmet demand as part of the equation
        }

        // Solve the problem
        ResultStatus resultStatus = solver.solve();

        // Check and print the solution
        Map<Integer, Double> warehousesTotalOrder = new HashMap<>();
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Initially Optimal solution ... ");
            for (int i = 0; i < numWarehouses; i++) {
                Double sumPerWarehouse = 0.0;
                for (int j = 0; j < numProducts; j++) {
                    if (x[i][j].solutionValue() > 0) {
                        System.out.println("product " + orderList.get(j).getId() + " from warehouse " + warehouseList.get(i).getId() + " : " + x[i][j].solutionValue());
                        sumPerWarehouse += x[i][j].solutionValue() * warehouseList.get(i).getProductPrices().get(orderList.get(j).getId());
                    }
                }
                warehousesTotalOrder.put(warehouseList.get(i).getId(), sumPerWarehouse);
            }

            double sumOfUnmetQty = 0;
            for (int j = 0; j < numProducts; j++) {
                sumOfUnmetQty += unmetDemand[j].solutionValue();
                //System.out.printf("Unmet demand for %s: %.0f units%n", products[j], unmetDemand[j].solutionValue());
            }
            Double totalCost = solver.objective().value() - penaltyForUnmetDemand*sumOfUnmetQty;
            System.out.println("With Total cost: " + totalCost);

//            System.out.println("After Checking min order constraint ...");
//            for (Map.Entry<Integer,Double> warehouse : warehousesTotalOrder.entrySet()){
//                Warehouse1 warehouse1 = warehouseList.stream()
//                        .filter(w -> w.getId() == warehouse.getKey())
//                        .findFirst().get();
//                if(warehouse.getValue() < warehouse1.getMinOrderPrice()){
//                    System.out.println("warehouse " + warehouse.getKey() + " will be dropped from order as it violates min order constraint ");
//                    totalCost -= warehouse.getValue();
//                }
//            }
//            System.out.println("Now Total cost = " + totalCost);

        } else {
            System.out.println("The problem does not have an optimal solution.");
        }

        System.out.println("-----------------------");
        System.out.println("Analysis");
        System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
        System.out.println("Problem solved in " + solver.iterations() + " iterations");
    }


    public void solveWithMinOrderPostProcessing() {
        Loader.loadNativeLibraries();

        List<Warehouse1> warehouseList = getDynamicWarehouses();
        int numWarehouses = warehouseList.size();
        List<Product> orderList = getDynamicOrder();
        int numProducts = orderList.size();

        System.out.println(warehouseList);
        System.out.println(orderList);

        System.out.println("----------------------------------");
        optimizerService1.getOptimizedSolution(warehouseList, orderList);
        System.out.println("---------------------------------");

        List<CbcProduct> demand = orderList.stream()
        		.map(product -> new CbcProduct(product.getId(), product.getQuantity(), 0, ""))
        		.collect(Collectors.toList());

        List<CbcWarehouse> warehouses = new ArrayList<CbcWarehouse>();
        for (Warehouse1 warehouse : warehouseList) {
        	List<CbcProduct> products = new ArrayList<CbcProduct>();
        	for (int i : warehouse.getProductPrices().keySet()) {
        		products.add(
        				new CbcProduct(i, warehouse.getProductQuantities().get(i), warehouse.getProductPrices().get(i), ""));
        	}
        	warehouses.add(new CbcWarehouse(warehouse.getId(), warehouse.getName(), warehouse.getMinOrderPrice(), products));
        }

        cbcSolver.solve(new CbcInput(demand, warehouses));

        System.out.println("---------------------------------");

        // Create the solver
        MPSolver solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");
        if (solver == null) {
            System.out.println("Could not create solver CBC_MIXED_INTEGER_PROGRAMMING");
            return;
        }

        // Decision variables: number of units sourced from each warehouse
        MPVariable[][] x = new MPVariable[numWarehouses][numProducts]; //x11
        for (int i = 0; i < numWarehouses; i++) {
            for(int j=0; j<numProducts; j++){
                if (warehouseList.get(i).getProductQuantities().get(j) != null) {
                    x[i][j] = solver.makeNumVar(0, warehouseList.get(i).getProductQuantities().get(j),
                            "x_" + i + "_" + j);
                }else{
                        x[i][j] = solver.makeNumVar(0, 0, "x_" + i + "_" + j);
                }
            }
        }

        // Additional variables for unmet demand (slack variables)
        MPVariable[] unmetDemand = new MPVariable[numProducts];
        for (int j = 0; j < numProducts; j++) {
            unmetDemand[j] = solver.makeNumVar(0, orderList.get(j).getQuantity(), "unmetDemand_" + j);
        }

        // Penalty for unmet demand (i.e., maximize quantity supplied)
        // Use a large penalty to force the solver to fulfill as much demand as possible
        // 1000 * unMetDemandP2 + 1000 * unMetDemandP2 is added to the objective to be minimized
        // now objective function is 10x11 + 20x12 + 15x21 + 25x22 + 1000 * unMetDemandP2 + 1000 * unMetDemandP2

        // Objective function: minimize total cost + large penalty for unmet demand
        MPObjective objective = solver.objective();

        // Minimize the total cost
        for (int i = 0; i < numWarehouses; i++) {
            int j = 0;
            for (Map.Entry<Integer, Double> productPrice : warehouseList.get(i).getProductPrices().entrySet()) {
                objective.setCoefficient(x[i][j], warehouseList.get(i).getProductPrices().get(productPrice.getKey()));
                j++;
            }
        }

        // Penalty for unmet demand
        double penaltyForUnmetDemand = 1000;
        for (int j = 0; j < numProducts; j++) {
            objective.setCoefficient(unmetDemand[j], penaltyForUnmetDemand);
        }

        objective.setMinimization();

        // Demand constraints: total supplied + unmet demand = total demand
        for (int j = 0; j < numProducts; j++) {
            MPConstraint demandConstraint = solver.makeConstraint(orderList.get(j).getQuantity(), orderList.get(j).getQuantity(), "demand_" + j);
            for (int i = 0; i < numWarehouses; i++) {
                demandConstraint.setCoefficient(x[i][j], 1);
            }
            demandConstraint.setCoefficient(unmetDemand[j], 1);
        }

        // Solve the problem
        ResultStatus resultStatus = solver.solve();

        // Check and print the initial solution
        Map<Integer, Double> warehousesTotalOrder = new HashMap<>();
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Initially Optimal solution ... ");
            for (int i = 0; i < numWarehouses; i++) {
                Double sumPerWarehouse = 0.0;
                for (int j = 0; j < numProducts; j++) {
                    if (x[i][j].solutionValue() > 0) {
                        System.out.println("product " + orderList.get(j).getId() + " from warehouse " +
                                warehouseList.get(i).getId() + " : " + x[i][j].solutionValue());

                        sumPerWarehouse += x[i][j].solutionValue() * warehouseList.get(i).getProductPrices()
                                .get(orderList.get(j).getId());
                    }
                }
                warehousesTotalOrder.put(warehouseList.get(i).getId(), sumPerWarehouse);
            }

            double sumOfUnmetQty = 0;
            for (int j = 0; j < numProducts; j++) {
                sumOfUnmetQty += unmetDemand[j].solutionValue();
            }

            Double totalCost = solver.objective().value() - penaltyForUnmetDemand * sumOfUnmetQty;
            System.out.println("With Total cost: " + totalCost);

            // Post-process: Check if any warehouse violates min order constraint
            boolean rerun = false;
            for (Map.Entry<Integer, Double> warehouse : warehousesTotalOrder.entrySet()) {
                Warehouse1 warehouse1 = warehouseList.stream()
                        .filter(w -> w.getId() == warehouse.getKey())
                        .findFirst().get();
                if (warehouse.getValue() < warehouse1.getMinOrderPrice()) {
                    System.out.println("Warehouse " + warehouse.getKey() + " violates min order constraint, removing from the order.");
                    for (int j = 0; j < numProducts; j++) {
                        // Set product quantities for this warehouse to 0
                        x[warehouse.getKey()][j].setBounds(0, 0);
                    }
                    rerun = true;
                }
            }

            // Rerun the solver if any warehouse was dropped
            if (rerun) {
                System.out.println("Rerunning the solver with modified constraints...");
                resultStatus = solver.solve();
                if (resultStatus == ResultStatus.OPTIMAL) {
                    System.out.println("Solution after enforcing min order constraints:");
                    totalCost = 0.0;
                    for (int i = 0; i < numWarehouses; i++) {
                        Double sumPerWarehouse = 0.0;
                        for (int j = 0; j < numProducts; j++) {
                            if (x[i][j].solutionValue() > 0) {
                                System.out.println("product " + orderList.get(j).getId() + " from warehouse " + warehouseList.get(i).getId() + " : " + x[i][j].solutionValue());
                                sumPerWarehouse += x[i][j].solutionValue() * warehouseList.get(i).getProductPrices().get(orderList.get(j).getId());
                            }
                        }
                        totalCost += sumPerWarehouse;
                    }

                    sumOfUnmetQty = 0;
                    for (int j = 0; j < numProducts; j++) {
                        sumOfUnmetQty += unmetDemand[j].solutionValue();
                    }

                    totalCost = solver.objective().value() - penaltyForUnmetDemand * sumOfUnmetQty;
                    System.out.println("Total cost after min order enforcement: " + totalCost);
                } else {
                    System.out.println("No optimal solution found after enforcing min order constraints.");
                }
            }
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }

        System.out.println("-----------------------");
        System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
        System.out.println("Problem solved in " + solver.iterations() + " iterations");
    }

    public void solveFinal(List<Warehouse1> warehouseList, List<Product> orderList) {
        Loader.loadNativeLibraries();

        System.out.println("----------------------------------");
        optimizerService1.getOptimizedSolution(warehouseList, orderList);
        System.out.println("---------------------------------");

        List<CbcProduct> demand = orderList.stream()
        		.map(product -> new CbcProduct(product.getId(), product.getQuantity(), 0, ""))
        		.collect(Collectors.toList());
        
        List<CbcWarehouse> warehouses = new ArrayList<CbcWarehouse>();
        for (Warehouse1 warehouse : warehouseList) {
        	List<CbcProduct> products = new ArrayList<CbcProduct>();
        	for (int i : warehouse.getProductPrices().keySet()) {
        		products.add(
        				new CbcProduct(i, warehouse.getProductQuantities().get(i), warehouse.getProductPrices().get(i), ""));
        	}
        	warehouses.add(new CbcWarehouse(warehouse.getId(), warehouse.getName(), warehouse.getMinOrderPrice(), products));
        }

        cbcSolver.solve(new CbcInput(demand, warehouses));
        
        System.out.println("---------------------------------");
    }

//    public void solve2() {
//        Loader.loadNativeLibraries();
//
//        // Input data
//        int numWarehouses = 2;
//        int numProducts = 2;
//        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
//        String[] products = {"Product A", "Product B"};
//
//        // Storage capacity for each product in each warehouse
//        int[][] storageCapacity = {
//                {100, 150}, // Warehouse 1 capacities for Product A and B
//                {200, 100}  // Warehouse 2 capacities for Product A and B
//        };
//
//        // Prices for products in each warehouse
//        double[][] prices = {
//                {10, 20}, // Warehouse 1 prices for Product A and B
//                {15, 25}  // Warehouse 2 prices for Product A and B
//        };
//
//        // Minimum order price requirement for each warehouse
//        double[] minPrice = {1000, 1500};
//
//        // Demand for each product
//        int[] demand = {80, 70};
//
//        // Create Solver
//        MPSolver solver = MPSolver.createSolver("SCIP");
//        if (solver == null) {
//            System.out.println("Could not create solver SCIP");
//            return;
//        }
//
//        // Decision variables: how many units of each product are sourced from each warehouse
//        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
//
//        // Binary decision variables: whether to source from a specific warehouse
//        MPVariable[][] y = new MPVariable[numWarehouses][numProducts];
//
//        for (int i = 0; i < numWarehouses; i++) {
//            for (int j = 0; j < numProducts; j++) {
//                // x[i][j] is the number of units of product j sourced from warehouse i
//                x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
//
//                // y[i][j] is 1 if product j is sourced from warehouse i, 0 otherwise
//                y[i][j] = solver.makeBoolVar("y_" + i + "_" + j);
//            }
//        }
//
//        // Objective: minimize total cost
//        MPObjective objective = solver.objective();
//        for (int i = 0; i < numWarehouses; i++) {
//            for (int j = 0; j < numProducts; j++) {
//                objective.setCoefficient(x[i][j], prices[i][j]);
//            }
//        }
//        objective.setMinimization();
//
//        // Demand constraints: meet the full demand for each product
//        for (int j = 0; j < numProducts; j++) {
//            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
//            for (int i = 0; i < numWarehouses; i++) {
//                demandConstraint.setCoefficient(x[i][j], 1);
//            }
//        }
//
//        // Capacity constraints: the quantity sourced from a warehouse cannot exceed its capacity
//        for (int i = 0; i < numWarehouses; i++) {
//            for (int j = 0; j < numProducts; j++) {
//                MPConstraint capacityConstraint = solver.makeConstraint(0, storageCapacity[i][j], "capacity_" + i + "_" + j);
//                capacityConstraint.setCoefficient(x[i][j], 1);
//            }
//        }
//
//        // Minimum order price constraint for each warehouse
//        for (int i = 0; i < numWarehouses; i++) {
//            MPConstraint minOrderPriceConstraint = solver.makeConstraint(minPrice[i], Double.POSITIVE_INFINITY, "minOrder_" + i);
//            for (int j = 0; j < numProducts; j++) {
//                minOrderPriceConstraint.setCoefficient(x[i][j], prices[i][j]);
//            }
//        }
//
//        // Linking constraints: if y[i][j] is 0, then x[i][j] must be 0
//        for (int i = 0; i < numWarehouses; i++) {
//            for (int j = 0; j < numProducts; j++) {
//                MPConstraint linkingConstraint = solver.makeConstraint(0, storageCapacity[i][j], "linking_" + i + "_" + j);
//                linkingConstraint.setCoefficient(x[i][j], 1);
//                linkingConstraint.setCoefficient(y[i][j], -storageCapacity[i][j]); // Ensures x <= y * capacity
//            }
//        }
//
//        // Add "either-or" constraint: force the solver to choose one warehouse for each product
//        for (int j = 0; j < numProducts; j++) {
//            MPConstraint eitherOrConstraint = solver.makeConstraint(1, 1, "either_or_" + j);
//            for (int i = 0; i < numWarehouses; i++) {
//                eitherOrConstraint.setCoefficient(y[i][j], 1);
//            }
//        }
//
//        // Solve the problem
//        ResultStatus resultStatus = solver.solve();
//
//        // Check and print the solution
//        if (resultStatus == ResultStatus.OPTIMAL) {
//            System.out.println("Optimal solution found:");
//            for (int i = 0; i < numWarehouses; i++) {
//                for (int j = 0; j < numProducts; j++) {
//                    if (x[i][j].solutionValue() > 0) {
//                        System.out.printf("%s from %s: %.0f units%n", products[j], warehouses[i], x[i][j].solutionValue());
//                    }
//                }
//            }
//            System.out.printf("Total cost: %.2f%n", solver.objective().value());
//        } else {
//            System.out.println("The problem does not have an optimal solution.");
//        }
//    }

//    public void solve2() {
//        Loader.loadNativeLibraries();
//
//        // Input data
//        int numWarehouses = 2;
//        int numProducts = 2;
//        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
//        String[] products = {"Product A", "Product B"};
//
//        // Storage capacity for each product in each warehouse
//        int[][] storageCapacity = {
//                {100, 150}, // Warehouse 1 capacities for Product A and B
//                {200, 100}  // Warehouse 2 capacities for Product A and B
//        };
//
//        // Prices for products in each warehouse
//        double[][] prices = {
//                {10, 20}, // Warehouse 1 prices for Product A and B
//                {15, 25}  // Warehouse 2 prices for Product A and B
//        };
//
//        // Minimum order price requirement for each warehouse
//        double[] minPrice = {1000, 1500};
//
//        // Demand for each product
//        int[] demand = {80, 70};
//
//        // Create Solver
//        MPSolver solver = MPSolver.createSolver("SCIP");
//        if (solver == null) {
//            System.out.println("Could not create solver SCIP");
//            return;
//        }
//
//        // Decision variables: how many units of each product are sourced from each warehouse
//        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
//
//        for (int i = 0; i < numWarehouses; i++) {
//            for (int j = 0; j < numProducts; j++) {
//                // x[i][j] is the number of units of product j sourced from warehouse i
//                x[i][j] = solver.makeNumVar(0, storageCapacity[i][j], "x_" + i + "_" + j);
//            }
//        }
//
//        // Objective: minimize total cost
//        MPObjective objective = solver.objective();
//        for (int i = 0; i < numWarehouses; i++) {
//            for (int j = 0; j < numProducts; j++) {
//                objective.setCoefficient(x[i][j], prices[i][j]);
//            }
//        }
//        objective.setMinimization();
//
//        // Demand constraints: meet the full demand for each product
//        for (int j = 0; j < numProducts; j++) {
//            MPConstraint demandConstraint = solver.makeConstraint(demand[j], demand[j], "demand_" + j);
//            for (int i = 0; i < numWarehouses; i++) {
//                demandConstraint.setCoefficient(x[i][j], 1);
//            }
//        }
//
//        // Capacity constraints: the quantity sourced from a warehouse cannot exceed its capacity
//        for (int i = 0; i < numWarehouses; i++) {
//            for (int j = 0; j < numProducts; j++) {
//                MPConstraint capacityConstraint = solver.makeConstraint(0, storageCapacity[i][j], "capacity_" + i + "_" + j);
//                capacityConstraint.setCoefficient(x[i][j], 1);
//            }
//        }
//
//        // Minimum order price constraint for each warehouse
//        for (int i = 0; i < numWarehouses; i++) {
//            MPConstraint minOrderPriceConstraint = solver.makeConstraint(minPrice[i], Double.POSITIVE_INFINITY, "minOrder_" + i);
//            for (int j = 0; j < numProducts; j++) {
//                minOrderPriceConstraint.setCoefficient(x[i][j], prices[i][j]);
//            }
//        }
//
//        // Solve the problem
//        ResultStatus resultStatus = solver.solve();
//
//        // Check and print the solution
//        if (resultStatus == ResultStatus.OPTIMAL) {
//            System.out.println("Optimal solution found:");
//            for (int i = 0; i < numWarehouses; i++) {
//                for (int j = 0; j < numProducts; j++) {
//                    if (x[i][j].solutionValue() > 0) {
//                        System.out.printf("%s from %s: %.0f units%n", products[j], warehouses[i], x[i][j].solutionValue());
//                    }
//                }
//            }
//            System.out.printf("Total cost: %.2f%n", solver.objective().value());
//        } else {
//            System.out.println("The problem does not have an optimal solution.");
//        }
//    }

    public void solveOptimized(List<WarehouseDTO> warehouses, List<ProductDTO> products) {
        Loader.loadNativeLibraries();

        int numWarehouses = warehouses.size();
        int numProducts = products.size();

        // إنشاء Solver
        MPSolver solver = MPSolver.createSolver("GLOP");

        // المتغيرات: عدد الوحدات المطلوبة من كل منتج في كل مخزن
        MPVariable[][] x = new MPVariable[numWarehouses][numProducts];
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                int finalJ = j;
                WarehouseProductDTO warehouseProduct = warehouses.get(i).getAvailableProducts().stream()
                        .filter(p -> p.getProductId().equals(products.get(finalJ).getId()))
                        .findFirst()
                        .orElse(null);

                if (warehouseProduct != null) {
                    x[i][j] = solver.makeNumVar(0, warehouseProduct.getStorage().intValue(), "x_" + i + "_" + j);
                }
            }
        }

        // دالة الهدف: تقليل التكلفة الإجمالية
        MPObjective objective = solver.objective();
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                int finalJ = j;
                WarehouseProductDTO warehouseProduct = warehouses.get(i).getAvailableProducts().stream()
                        .filter(p -> p.getProductId().equals(products.get(finalJ).getId()))
                        .findFirst()
                        .orElse(null);

                if (warehouseProduct != null) {
                    objective.setCoefficient(x[i][j], warehouseProduct.getPrice());
                }
            }
        }
        objective.setMinimization();

        // قيود السعة لكل مخزن
        for (int i = 0; i < numWarehouses; i++) {
            for (int j = 0; j < numProducts; j++) {
                int finalJ = j;
                WarehouseProductDTO warehouseProduct = warehouses.get(i).getAvailableProducts().stream()
                        .filter(p -> p.getProductId().equals(products.get(finalJ).getId()))
                        .findFirst()
                        .orElse(null);

                if (warehouseProduct != null) {
                    MPConstraint capacityConstraint = solver.makeConstraint(0, warehouseProduct.getStorage().intValue());
                    capacityConstraint.setCoefficient(x[i][j], 1);
                }
            }
        }

        // قيود الحد الأدنى لسعر الطلبية لكل مخزن باستخدام minOrderPrice من WarehouseDTO
        for (int i = 0; i < numWarehouses; i++) {
            double minPrice = warehouses.get(i).getMinOrderPrice(); // Get min order price from WarehouseDTO
            MPConstraint minPriceConstraint = solver.makeConstraint(minPrice, Double.POSITIVE_INFINITY);
            for (int j = 0; j < numProducts; j++) {
                int finalJ = j;
                WarehouseProductDTO warehouseProduct = warehouses.get(i).getAvailableProducts().stream()
                        .filter(p -> p.getProductId().equals(products.get(finalJ).getId()))
                        .findFirst()
                        .orElse(null);

                if (warehouseProduct != null) {
                    minPriceConstraint.setCoefficient(x[i][j], warehouseProduct.getPrice());
                }
            }
        }

        // قيود الطلبات المطلوبة
        for (int j = 0; j < numProducts; j++) {
            int demand = products.get(j).getQuantity().intValue(); // Directly use quantity from ProductDTO
            MPConstraint demandConstraint = solver.makeConstraint(demand, Double.POSITIVE_INFINITY);
            for (int i = 0; i < numWarehouses; i++) {
                int finalJ = j;
                WarehouseProductDTO warehouseProduct = warehouses.get(i).getAvailableProducts().stream()
                        .filter(p -> p.getProductId().equals(products.get(finalJ).getId()))
                        .findFirst()
                        .orElse(null);

                if (warehouseProduct != null) {
                    demandConstraint.setCoefficient(x[i][j], 1);
                }
            }
        }

        // حل النموذج
        ResultStatus resultStatus = solver.solve();

        // التحقق من الحل
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Optimal solution found:");
            for (int i = 0; i < numWarehouses; i++) {
                for (int j = 0; j < numProducts; j++) {
                    int finalJ = j;
                    WarehouseProductDTO warehouseProduct = warehouses.get(i).getAvailableProducts().stream()
                            .filter(p -> p.getProductId().equals(products.get(finalJ).getId()))
                            .findFirst()
                            .orElse(null);

                    if (warehouseProduct != null) {
                        System.out.printf("%s from %s: %.0f units%n", products.get(j).getName(), warehouses.get(i).getName(), x[i][j].solutionValue());
                    }
                }
            }
            System.out.printf("Total cost: %.2f%n", solver.objective().value());
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }
    }


    public void solveWithMinOrderConstraint(List<WarehouseProduct> warehouseProductList) {
        // Load native libraries for the solver
        Loader.loadNativeLibraries();

        // Create the solver
        MPSolver solver = MPSolver.createSolver("GLOP");

        // Group the list of WarehouseProduct by productId and warehouseId
        Map<Integer, List<WarehouseProduct>> productWarehouseMap = new HashMap<>();
        Map<Integer, Map<Integer, WarehouseProduct>> warehouseProductMap = new HashMap<>();

        for (WarehouseProduct wp : warehouseProductList) {
            productWarehouseMap
                    .computeIfAbsent(wp.getProductId(), k -> new ArrayList<>())
                    .add(wp);

            warehouseProductMap
                    .computeIfAbsent(wp.getWarehouseId(), k -> new HashMap<>())
                    .put(wp.getProductId(), wp);
        }

        // Dictionary to store the decision variables x[i][j] where i = productId, j = warehouseId
        Map<String, MPVariable> x = new HashMap<>();
        MPObjective objective = solver.objective();

        // Create warehouse used binary variables
        Map<Integer, MPVariable> warehouseUsed = new HashMap<>();

        for (List<WarehouseProduct> productOptions : productWarehouseMap.values()) {
            for (WarehouseProduct wp : productOptions) {
                String variableName = "x_" + wp.getProductId() + "_" + wp.getWarehouseId();
                MPVariable var = solver.makeNumVar(0.0, 1.0, variableName);
                x.put(variableName, var);
                objective.setCoefficient(var, wp.getPrice());

                // Ensure we have a binary variable to track if this warehouse is used
                warehouseUsed.putIfAbsent(wp.getWarehouseId(), solver.makeBoolVar("used_" + wp.getWarehouseId()));
            }
        }

        // Set the objective to minimization
        objective.setMinimization();

        // Each product must be assigned to exactly one warehouse
        for (Integer productId : productWarehouseMap.keySet()) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "constraint_product_" + productId);
            for (WarehouseProduct wp : productWarehouseMap.get(productId)) {
                String variableName = "x_" + wp.getProductId() + "_" + wp.getWarehouseId();
                constraint.setCoefficient(x.get(variableName), 1);
            }
        }

        // Add minimum order price constraints per warehouse
        for (Map.Entry<Integer, MPVariable> entry : warehouseUsed.entrySet()) {
            Integer warehouseId = entry.getKey();
            MPConstraint minOrderConstraint = solver.makeConstraint(0, Double.POSITIVE_INFINITY, "minOrder_" + warehouseId);
            double minOrderPrice = warehouseProductList.stream()
                    .filter(wp -> wp.getWarehouseId().equals(warehouseId))
                    .mapToDouble(WarehouseProduct::getMinOrderPrice)
                    .findFirst().orElse(0.0);

            minOrderConstraint.setCoefficient(entry.getValue(), -minOrderPrice);

            for (WarehouseProduct wp : warehouseProductList) {
                if (wp.getWarehouseId().equals(warehouseId)) {
                    String variableName = "x_" + wp.getProductId() + "_" + wp.getWarehouseId();
                    minOrderConstraint.setCoefficient(x.get(variableName), wp.getPrice());
                }
            }
        }

        // Solve the problem
        ResultStatus resultStatus = solver.solve();

        // Check if an optimal solution was found
        if (resultStatus == ResultStatus.OPTIMAL) {
            System.out.println("Minimum Price: " + solver.objective().value());

            // Output the selected warehouse for each product
            for (Integer productId : productWarehouseMap.keySet()) {
                for (WarehouseProduct wp : productWarehouseMap.get(productId)) {
                    String variableName = "x_" + wp.getProductId() + "_" + wp.getWarehouseId();
                    if (x.get(variableName).solutionValue() == 1) {
                        System.out.println("Product " + productId + " is sourced from Warehouse " +
                                wp.getWarehouseId() + " with price " + wp.getPrice());
                    }
                }
            }

            // Output the total cost per warehouse and check if it satisfies the min order price
            for (Integer warehouseId : warehouseUsed.keySet()) {
                if (warehouseUsed.get(warehouseId).solutionValue() == 1) {
                    System.out.println("Warehouse " + warehouseId + " is used.");
                }
            }

        } else {
            System.out.println("The problem does not have an optimal solution.");
        }
    }




}

