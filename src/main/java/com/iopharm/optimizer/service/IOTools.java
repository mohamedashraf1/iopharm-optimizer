package com.iopharm.optimizer.service;


import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;
import com.iopharm.optimizer.model.WarehouseProduct;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class IOTools {

    public void solve(){
        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver("GLOP");

        int numProducts = 4; // Dynamic number of products
        double[][] prices = {
                {10, 15, 20},  // Prices for Product 1
                {8, 12, 2},   // Prices for Product 2
                {5, 10, 15},   // Prices for Product 3
                {9, 14, 8}    // Prices for Product 4
        };

        MPVariable[][] x = new MPVariable[numProducts][3];

        for (int i = 0; i < numProducts; i++) {
            for (int j = 0; j < 3; j++) {
                x[i][j] = solver.makeNumVar(0.0, 1.0, "x_" + i + "_" + j);
            }
        }

        // Objective: Minimize total price
        var objective = solver.objective();
        for (int i = 0; i < numProducts; i++) {
            for (int j = 0; j < 3; j++) {
                objective.setCoefficient(x[i][j], prices[i][j]);
            }
        }
        objective.setMinimization();

        // Constraint: Choose exactly one price for each product
        for (int i = 0; i < numProducts; i++) {
            var constraint = solver.makeConstraint(1, 1, "constraint_" + i);
            for (int j = 0; j < 3; j++) {
                constraint.setCoefficient(x[i][j], 1);
            }
        }

        // Solve the problem
        if (solver.solve() == ResultStatus.OPTIMAL) {
            System.out.println("Minimum Price: " + solver.objective().value());
            for (int i = 0; i < numProducts; i++) {
                for (int j = 0; j < 3; j++) {
                    if (x[i][j].solutionValue() == 1) {
                        System.out.println("Product " + (i + 1) + " Price: " + prices[i][j] + " from warehouse: " + (j+1));
                    }
                }
            }
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }
    }

    public void getOptimizedSolution(){
        List<WarehouseProduct> warehouseProductList = Arrays.asList(
                new WarehouseProduct(1, 1, 10.0, 10.0),
                new WarehouseProduct(2, 1, 15.0, 10.0),
                new WarehouseProduct(3, 1, 20.0, 10.0),
                new WarehouseProduct(1, 2, 8.0, 10.0),
                new WarehouseProduct(2, 2, 12.0, 10.0),
                new WarehouseProduct(3, 2, 2.0, 10.0),
                new WarehouseProduct(1, 3, 5.0, 10.0),
                new WarehouseProduct(2, 3, 10.0, 10.0),
                new WarehouseProduct(3, 3, 15.0, 10.0),
                new WarehouseProduct(1, 4, 9.0, 10.0),
                new WarehouseProduct(2, 4, 14.0, 10.0),
                new WarehouseProduct(3, 4, 8.0, 10.0)
        );

        solveWithWarehouses(warehouseProductList);
    }

    public void getOptimizedSolutionWithConstraint(){
        List<WarehouseProduct> warehouseProductList = Arrays.asList(
                new WarehouseProduct(1, 1, 10.0, 10.0),
                new WarehouseProduct(2, 1, 15.0, 10.0),
                new WarehouseProduct(3, 1, 20.0, 15.0),
                new WarehouseProduct(1, 2, 8.0, 10.0),
                new WarehouseProduct(2, 2, 12.0, 10.0),
                new WarehouseProduct(3, 2, 2.0, 15.0),
                new WarehouseProduct(1, 3, 5.0, 10.0),
                new WarehouseProduct(2, 3, 10.0, 10.0),
                new WarehouseProduct(3, 3, 15.0, 15.0),
                new WarehouseProduct(1, 4, 9.0, 10.0),
                new WarehouseProduct(2, 4, 14.0, 10.0),
                new WarehouseProduct(3, 4, 8.0, 15.0)
        );

        solveWithMinOrderConstraint(warehouseProductList);
    }


    public void solveWithWarehouses(List<WarehouseProduct> warehouseProductList){
        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver("GLOP");

        // Group the list of WarehouseProduct by productId
        Map<Integer, List<WarehouseProduct>> productWarehouseMap = new HashMap<>();
        for (WarehouseProduct wp : warehouseProductList) {
            productWarehouseMap
                    .computeIfAbsent(wp.getProductId(), k -> new ArrayList<>())
                    .add(wp);
        }

        // Dictionary to store the decision variables x[i][j] where i = productId, j = warehouseId
        Map<String, MPVariable> x = new HashMap<>();

        // Create decision variables and set coefficients in the objective
        MPObjective objective = solver.objective();
        for (List<WarehouseProduct> productOptions : productWarehouseMap.values()) {
            for (WarehouseProduct wp : productOptions) {
                // Create variable for productId and warehouseId
                String variableName = "x_" + wp.getProductId() + "_" + wp.getWarehouseId();
                MPVariable var = solver.makeNumVar(0.0, 1.0, variableName);

                // Add this variable to the decision variable map
                x.put(variableName, var);

                // Add the price to the objective function
                objective.setCoefficient(var, wp.getPrice());
            }
        }

        // Set the objective to minimization
        objective.setMinimization();

        // Add the constraint: each product must be assigned to exactly one warehouse
        for (Integer productId : productWarehouseMap.keySet()) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "constraint_product_" + productId);
            for (WarehouseProduct wp : productWarehouseMap.get(productId)) {
                String variableName = "x_" + wp.getProductId() + "_" + wp.getWarehouseId();
                constraint.setCoefficient(x.get(variableName), 1);
            }
        }

        // Solve the problem
        MPSolver.ResultStatus resultStatus = solver.solve();

        // Check if an optimal solution was found
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
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
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }
    }

    public void solveWithMinOrderConstraint(List<WarehouseProduct> warehouseProductList){
        // Load native libraries for the solver
        Loader.loadNativeLibraries();

        // Create the solver
        MPSolver solver = MPSolver.createSolver("GLOP");

        // Group the list of WarehouseProduct by productId
        Map<Integer, List<WarehouseProduct>> productWarehouseMap = new HashMap<>();
        for (WarehouseProduct wp : warehouseProductList) {
            productWarehouseMap
                    .computeIfAbsent(wp.getProductId(), k -> new ArrayList<>())
                    .add(wp);
        }

        // Dictionary to store the decision variables x[i][j] where i = productId, j = warehouseId
        Map<String, MPVariable> x = new HashMap<>();

        // Create decision variables and set coefficients in the objective
        MPObjective objective = solver.objective();

        // Create a map to track warehouse binary variables (whether a warehouse is used or not)
        Map<Integer, MPVariable> warehouseUsed = new HashMap<>();

        for (List<WarehouseProduct> productOptions : productWarehouseMap.values()) {
            for (WarehouseProduct wp : productOptions) {
                // Create variable for productId and warehouseId
                String variableName = "x_" + wp.getProductId() + "_" + wp.getWarehouseId();
                MPVariable var = solver.makeNumVar(0.0, 1.0, variableName);

                // Add this variable to the decision variable map
                x.put(variableName, var);

                // Add the price to the objective function
                objective.setCoefficient(var, wp.getPrice());

                // Ensure we have a binary variable to track if this warehouse is used
                warehouseUsed.putIfAbsent(wp.getWarehouseId(), solver.makeBoolVar("used_" + wp.getWarehouseId()));
            }
        }
        // Set the objective to minimization
        objective.setMinimization();

        // Add the constraint: each product must be assigned to exactly one warehouse
        for (Integer productId : productWarehouseMap.keySet()) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "constraint_product_" + productId);
            for (WarehouseProduct wp : productWarehouseMap.get(productId)) {
                String variableName = "x_" + wp.getProductId() + "_" + wp.getWarehouseId();
                constraint.setCoefficient(x.get(variableName), 1);
            }
        }

        // Add constraints to ensure each warehouse satisfies its minimum order price
        for (Integer warehouseId : warehouseUsed.keySet()) {
            MPConstraint minOrderConstraint = solver.makeConstraint(0, Double.POSITIVE_INFINITY, "minOrder_" + warehouseId);

            // Get the minOrderPrice for the warehouse from any product in this warehouse
            double minOrderPrice = warehouseProductList.stream()
                    .filter(wp -> wp.getWarehouseId().equals(warehouseId))
                    .findFirst()
                    .map(WarehouseProduct::getMinOrderPrice)
                    .orElse(0.0);

            MPVariable usedVar = warehouseUsed.get(warehouseId);

            // Add the products of this warehouse to the constraint
            for (WarehouseProduct wp : warehouseProductList) {
                if (wp.getWarehouseId().equals(warehouseId)) {
                    String variableName = "x_" + wp.getProductId() + "_" + wp.getWarehouseId();
                    MPVariable var = x.get(variableName);
                    minOrderConstraint.setCoefficient(var, wp.getPrice());
                }
            }

            // The minimum price constraint: sum(products assigned to this warehouse) >= minOrderPrice * usedVar
            minOrderConstraint.setCoefficient(usedVar, -minOrderPrice);
        }

        // Solve the problem
        MPSolver.ResultStatus resultStatus = solver.solve();

        // Check if an optimal solution was found
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
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
