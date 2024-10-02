package com.iopharm.optimizer.service;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.sat.*;
import com.iopharm.optimizer.dtos.ProductDTO;
import com.iopharm.optimizer.dtos.WarehouseDTO;
import com.iopharm.optimizer.dtos.WarehouseProductDTO;
import com.iopharm.optimizer.model.Product;
import com.iopharm.optimizer.model.Warehouse1;
import com.iopharm.optimizer.model.WarehouseProduct;
import org.springframework.stereotype.Service;
import  com.google.ortools.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrToolsService {
    public void solve() {

        Loader.loadNativeLibraries();
        // إعداد البيانات
        int numWarehouses = 2;
        int numProducts = 2;
        // تعريف البيانات
        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
        String[] products = {"Product A", "Product B"};

        // سعة التخزين لكل منتج في كل مخزن
        int[][] storageCapacity = {
                {100, 150}, // Warehouse 1 capacities for Product A and B
                {200, 100}  // Warehouse 2 capacities for Product A and B
        };

        // أسعار المنتجات في كل مخزن
        double[][] prices = {
                {10, 20}, // Warehouse 1 prices for Product A and B
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

    List<Warehouse1> getWarehouses(){
        List<Warehouse1> warehouses = new ArrayList<>();
        Warehouse1 temp1 = new Warehouse1();
        temp1.setId(1);
        temp1.setMinOrderPrice(1000);
        Map<Integer, Double> productPrices = new HashMap<>();
        productPrices.put(1, 10.0);
        productPrices.put(2, 20.0);

        temp1.setProductPrices(productPrices);

        Map<Integer, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1, 70);
        productQuantities.put(2, 150);

        temp1.setProductQuantities(productQuantities);

        warehouses.add(temp1);

        Warehouse1 temp2 = new Warehouse1();
        temp2.setId(2);
        temp2.setMinOrderPrice(1500);
        Map<Integer, Double> productPrices2 = new HashMap<>();
        productPrices2.put(1, 15.0);
        productPrices2.put(2, 25.0);

        temp2.setProductPrices(productPrices2);

        Map<Integer, Integer> productQuantities2 = new HashMap<>();
        productQuantities2.put(1, 1);
        productQuantities2.put(2, 100);

        temp2.setProductQuantities(productQuantities2);

        warehouses.add(temp2);

        return warehouses;
    }

    List<Product> getOrder(){
        List<Product> order = new ArrayList<>();
        order.add(new Product(1, 80));
        order.add(new Product(2, 70));
        return order;
    }

    public void solve2() {
        Loader.loadNativeLibraries();

        // Input data
        int numWarehouses = 2;
        int numProducts = 2;
        String[] warehouses = {"Warehouse 1", "Warehouse 2"};
        String[] products = {"Product A", "Product B"};

        // Storage capacity for each product in each warehouse
        int[][] storageCapacity = {
                {70, 150}, // Warehouse 1 capacities for Product A and B
                {1, 100}  // Warehouse 2 capacities for Product A and B
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
//        GLOP is a linear programming solver,
//   it may not always handle constraints related to integer values (such as exact capacity or unit constraints)
//   as well as a Mixed Integer Programming (MIP) solver like CBC.
//      Switching to CBC can sometimes give you more flexibility in handling complex constraints
        // Create Solver (Switching to CBC solver)
        MPSolver solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");
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

//        // Capacity constraints: do not exceed warehouse capacity for each product
//        for (int i = 0; i < numWarehouses; i++) {
//            for (int j = 0; j < numProducts; j++) {
//                MPConstraint capacityConstraint = solver.makeConstraint(0, storageCapacity[i][j], "capacity_" + i + "_" + j);
//                capacityConstraint.setCoefficient(x[i][j], 1);
//            }
//        }


        // Minimum price constraints: ensure the total cost of products in each warehouse meets the minimum price
        // x11*p11 + x12*p2 >= 1000
        // P2 >= 1000
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
        // 1000 * unMetDemandP2 + 1000 * unMetDemandP2 is added to the objective to be minimized
        double penaltyForUnmetDemand = 1000;  // Increase this penalty to ensure unmet demand is discouraged
        for (int j = 0; j < numProducts; j++) {
            objective.setCoefficient(unmetDemand[j], penaltyForUnmetDemand);  // Penalize unmet demand heavily
        }

        // now objective function is 10x11 + 20x12 + 15x21 + 25x22 + 1000 * unMetDemandP2 + 1000 * unMetDemandP2
        objective.setMinimization();  // Set to minimize the total objective

        // Demand constraints: total supplied + unmet demand = total demand
        for (int j = 0; j < numProducts; j++) {
            MPConstraint demandConstraint = solver.makeConstraint(orderList.get(j).getQuantity(), orderList.get(j).getQuantity(), "demand_" + j);
            for (int i = 0; i < numWarehouses; i++) {
                demandConstraint.setCoefficient(x[i][j], 1);  // Sum of all supplies should be part of the demand
            }
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

