package com.iopharm.optimizer.service;

import org.springframework.stereotype.Service;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

@Service
public class CbcSolver {

	public void solve() {
		Loader.loadNativeLibraries();

		// Create the solver using CBC (integer programming).
		MPSolver solver = new MPSolver("OrderOptimization",
				MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

		// Sample problem data
		int numProducts = 3; // Number of different products
		int numWarehouses = 2; // Number of available warehouses

		// Required quantities for each product
		int[] demand = { 10, 20, 5 }; // Demand for each product

		// Stock available at each warehouse for each product
		int[][] stock = { { 7, 15, 3 }, // Warehouse 1: [product1, product2, product3]
				{ 10, 10, 0 } // Warehouse 2: [product1, product2, product3]
		};

		// Cost of each product in each warehouse
		double[][] cost = { { 2.5, 3.0, 1.0 }, // Warehouse 1: [product1, product2, product3]
				{ 3.0, 2.8, 1.5 } // Warehouse 2: [product1, product2, product3]
		};

		// Minimum order price for each warehouse
		double[] minOrderPrice = { 50.0, 30.0 };

		// +++++
		// Penalty for unmet demand
        double penaltyUnmetDemand = 10.0;  // Cost per unit of unmet demand

		// Variables for the quantity of each product from each warehouse (integer
		// variables)
		// quantities[i][j] < stock[j][i]
		MPVariable[][] quantities = new MPVariable[numProducts][numWarehouses];
		for (int i = 0; i < numProducts; i++) {
			for (int j = 0; j < numWarehouses; j++) {
				quantities[i][j] = solver.makeIntVar(0, stock[j][i], "quantity_" + i + "_" + j);
			}
		}

		// +++++
		// Slack variables for unmet demand (continuous variables)
		// unmetDemand[i] < demand[i]
        MPVariable[] unmetDemand = new MPVariable[numProducts];
        for (int i = 0; i < numProducts; i++) {
            unmetDemand[i] = solver.makeIntVar(0, demand[i], "unmetDemand_" + i);
        }

		// Objective: Minimize the total cost
        // +++++ including penalties for unmet demand
		MPObjective objective = solver.objective();
		for (int i = 0; i < numProducts; i++) {
			for (int j = 0; j < numWarehouses; j++) {
				objective.setCoefficient(quantities[i][j], cost[j][i]);
			}
			// +++++
			// Add penalty for unmet demand to the objective
            objective.setCoefficient(unmetDemand[i], penaltyUnmetDemand);
		}
		objective.setMinimization();

		// Constraints: Fulfill the required quantity for each product (Demand)
		// quantities[1][j] ++ = demand[1]
		// +++++
		// with relaxation for unmet demand
		for (int i = 0; i < numProducts; i++) {
			MPConstraint demandConstraint = solver.makeConstraint(demand[i], demand[i], "demand_" + i);
			for (int j = 0; j < numWarehouses; j++) {
				demandConstraint.setCoefficient(quantities[i][j], 1);
			}
			// +++++
			// Relax the demand with unmet demand slack
            demandConstraint.setCoefficient(unmetDemand[i], 1);
		}

		// Constraints: Ensure the minimum order price per warehouse
		// quantities[i][1] * cost[1][i] ++ < minOrderPrice[1]
		for (int j = 0; j < numWarehouses; j++) {
			MPConstraint minOrderPriceConstraint = solver.makeConstraint(minOrderPrice[j], Double.POSITIVE_INFINITY,
					"min_order_price_" + j);
			for (int i = 0; i < numProducts; i++) {
				minOrderPriceConstraint.setCoefficient(quantities[i][j], cost[j][i]);
			}
		}

		// Solve the problem
		MPSolver.ResultStatus resultStatus = solver.solve();

		// Check the result status
		if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
			System.out.println("Optimal solution found!");

			// Output the solution
			double totalCost = 0;
			for (int i = 0; i < numProducts; i++) {
				for (int j = 0; j < numWarehouses; j++) {
					int quantity = (int) quantities[i][j].solutionValue();
					if (quantity > 0) {
						System.out.println(
								"Order " + quantity + " units of product " + (i + 1) + " from warehouse " + (j + 1));
						totalCost += quantity * cost[j][i];
					}
				}
				System.out.println();
				// +++++
				// Output unmet demand
                double unmet = unmetDemand[i].solutionValue();
                if (unmet > 0) {
                    System.out.println("Unmet demand for product " + (i + 1) + ": " + unmet + " units (penalty applied)");
//                    totalCost += unmet * penaltyUnmetDemand;
                }
			}
			
			System.out.println("Total cost: $" + totalCost);
			System.out.println();

			for (int j = 0; j < numWarehouses; j++) {
				System.out.println("Order from warehouse " + (j + 1) + ":");
				for (int i = 0; i < numProducts; i++) {
					int quantity = (int) quantities[i][j].solutionValue();
					if (quantity > 0) {
						System.out.println("- product " + (i + 1) + ": " + quantity + " units (" + cost[j][i] + ")");
					}
				}
			}
		} else {
			System.out.println("No optimal solution found.");
		}
	}
}
