package com.iopharm.optimizer.service;


import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPVariable;
import org.springframework.stereotype.Service;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;


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
                        System.out.println("Product " + (i + 1) + " Price: " + prices[i][j]);
                    }
                }
            }
        } else {
            System.out.println("The problem does not have an optimal solution.");
        }
    }
}
