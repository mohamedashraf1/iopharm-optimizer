package com.iopharm.optimizer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.iopharm.optimizer.model.CbcInput;
import com.iopharm.optimizer.model.CbcProduct;
import com.iopharm.optimizer.model.CbcWarehouse;

@Service
public class CbcSolver {

	public void solve(CbcInput input) {
		Loader.loadNativeLibraries();

		// Create the solver using CBC (integer programming).
		MPSolver solver = new MPSolver("OrderOptimization",
				MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

		// Sample problem data
		List<CbcWarehouse> warehouseList = input.getWarehouses();
		int numProducts = input.getDemand().size(); // Number of different products
		int numWarehouses = input.getWarehouses().size(); // Number of available warehouses

		// Required quantities for each product
		List<CbcProduct> demand = input.getDemand(); // Demand for each product

		// Stock available at each warehouse for each product
//		int[][] stock = { { 0, 100, 0, 100, 100 }, // Warehouse 1: [product1, product2, product3]
//				{ 100, 0, 100, 100, 100 } // Warehouse 2: [product1, product2, product3]
//		};

		// Cost of each product in each warehouse
//		double[][] cost = { { 16, 67, 20, 12, 28 }, // Warehouse 1: [product1, product2, product3]
//				{ 16, 66, 20, 10, 19 } // Warehouse 2: [product1, product2, product3]
//		};

		// Minimum order price for each warehouse
//		double[] minOrderPrice = { 1100.0, 130.0 };

		// +++++
		// Penalty for unmet demand
		double penaltyUnmetDemand; // Cost per unit of unmet demand
		double warehouseUsagePenalty; // Small penalty to discourage unnecessary warehouses

		// Variables for the quantity of each product from each warehouse (integer
		// variables)
		// quantities[i][j] < stock[j][i]
		double maxProductPrice = 0;
		MPVariable[][] quantities = new MPVariable[numProducts][numWarehouses];
		for (int i = 0; i < numProducts; i++) {
			for (int j = 0; j < numWarehouses; j++) {
				quantities[i][j] = solver.makeIntVar(0, warehouseList.get(j).getProducts().get(i).getQuantity(),
						"quantity_" + i + "_" + j);
				maxProductPrice = Math.max(maxProductPrice,
						warehouseList.get(j).getProducts().get(i).getPrice());
			}
		}
		penaltyUnmetDemand = 3 * maxProductPrice;

		// +++++
		// Slack variables for unmet demand (continuous variables)
		// unmetDemand[i] < demand[i]
		MPVariable[] unmetDemand = new MPVariable[numProducts];
		for (int i = 0; i < numProducts; i++) {
			unmetDemand[i] = solver.makeIntVar(0, demand.get(i).getQuantity(), "unmetDemand_" + i);
		}

		// +++++ +++++
		MPVariable[] metWarehouse = new MPVariable[numWarehouses];
		for (int i = 0; i < numWarehouses; i++) {
			metWarehouse[i] = solver.makeBoolVar("metWarehouse_" + i);
		}

		// Objective: Minimize the total cost
		// +++++ including penalties for unmet demand and warehouse usage
		MPObjective objective = solver.objective();
		for (int i = 0; i < numProducts; i++) {
			for (int j = 0; j < numWarehouses; j++) {
				objective.setCoefficient(quantities[i][j],
						warehouseList.get(j).getProducts().get(i).getPrice());
			}
			// +++++
			// Add penalty for unmet demand to the objectivwarehouseListmetDemand = 3 * maxProductPrice;
			objective.setCoefficient(unmetDemand[i], penaltyUnmetDemand);
//            System.out.println("penaltyUnmetDemand: " + penaltyUnmetDemand);
		}
		// +++++ +++++
		for (int j = 0; j < numWarehouses; j++) {
			// Small penalty for using a warehouse
			warehouseUsagePenalty = 0.01 * warehouseList.get(j).getMinOrderPrice();
			objective.setCoefficient(metWarehouse[j], warehouseUsagePenalty);
//            System.out.println("warehouseUsagePenalty: " + warehouseUsagePenalty);
		}
		objective.setMinimization();

		// Constraints: Fulfill the required quantity for each product (Demand)
		// quantities[1][j] ++ = demand[1]
		// +++++
		// with relaxation for unmet demand
		for (int i = 0; i < numProducts; i++) {
			MPConstraint demandConstraint = solver.makeConstraint(demand.get(i).getQuantity(),
					demand.get(i).getQuantity(), "demand_" + i);
			for (int j = 0; j < numWarehouses; j++) {
				demandConstraint.setCoefficient(quantities[i][j], 1);
			}
			// +++++
			// Relax the demand with unmet demand slack
			demandConstraint.setCoefficient(unmetDemand[i], 1);
		}

		// Constraints: Ensure the minimum order price per warehouse if the warehouse is
		// used
		// quantities[i][1] * cost[1][i] ++ > minOrderPrice[1]
		// quantities[i][1] * cost[1][i] ++ -minOrderPrice >= 0
		for (int j = 0; j < numWarehouses; j++) {
			MPConstraint minOrderPriceConstraint = solver.makeConstraint(0, Double.POSITIVE_INFINITY,
					"min_order_price_" + j);
			for (int i = 0; i < numProducts; i++) {
				minOrderPriceConstraint.setCoefficient(quantities[i][j],
						warehouseList.get(j).getProducts().get(i).getPrice());
			}
			// +++++ +++++
			minOrderPriceConstraint.setCoefficient(metWarehouse[j], -warehouseList.get(j).getMinOrderPrice());
		}

		// +++++ +++++
		// Constraints: If a warehouse is not used, all product quantities from that
		// warehouse should be zero
		// quantities[i][j] - stock[j][i] <= 0
		for (int j = 0; j < numWarehouses; j++) {
			for (int i = 0; i < numProducts; i++) {
				MPConstraint warehouseUsageConstraint = solver.makeConstraint(Double.NEGATIVE_INFINITY, 0,
						"warehouse_usage_" + j + "_" + i);
				warehouseUsageConstraint.setCoefficient(quantities[i][j], 1);
				warehouseUsageConstraint.setCoefficient(metWarehouse[j],
						-warehouseList.get(j).getProducts().get(i).getQuantity());
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
						totalCost += quantity * warehouseList.get(j).getProducts().get(i).getPrice();
					}
				}
				System.out.println();
				// +++++
				// Output unmet demand
				double unmet = unmetDemand[i].solutionValue();
				if (unmet > 0) {
					System.out
							.println("Unmet demand for product " + (i + 1) + ": " + unmet + " units (penalty applied)");
					// totalCost += unmet * penaltyUnmetDemand;
				}
			}

			System.out.println("Total cost: $" + totalCost);
			System.out.println();

			for (int j = 0; j < numWarehouses; j++) {
				totalCost = 0;
				System.out.println("Order from warehouse " + (j + 1) + ":");
				for (int i = 0; i < numProducts; i++) {
					int quantity = (int) quantities[i][j].solutionValue();
					if (quantity > 0) {
						System.out.println("- product " + (i + 1) + ": " + quantity + " units ("
								+ warehouseList.get(j).getProducts().get(i).getPrice() + ")");
						totalCost += quantity * warehouseList.get(j).getProducts().get(i).getPrice();
					}
				}
				System.out.println(totalCost);
			}

			for (int j = 0; j < numWarehouses; j++) {
				if (metWarehouse[j].solutionValue() == 1) {
					System.out.println("Warehouse " + (j + 1) + " is used.");
				} else {
                    System.out.println("Warehouse " + (j + 1) + " is discarded.");
                }
			}
			System.out.println("iterations: " + solver.iterations());
			System.out.println("time: " + solver.wallTime() + " milllis");
		} else {
			System.out.println("No optimal solution found.");
		}
	}
}
