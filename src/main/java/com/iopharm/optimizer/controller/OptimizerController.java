package com.iopharm.optimizer.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.iopharm.optimizer.dtos.ProductDTO;
import com.iopharm.optimizer.dtos.WarehouseDTO;
import com.iopharm.optimizer.dtos.WarehouseProductDTO;
import com.iopharm.optimizer.model.CbcInput;
import com.iopharm.optimizer.model.Product;
import com.iopharm.optimizer.model.Solution;
import com.iopharm.optimizer.model.Warehouse1;
import com.iopharm.optimizer.service.CbcSolver;
import com.iopharm.optimizer.service.IOTools;
import com.iopharm.optimizer.service.OptimizerService;
import com.iopharm.optimizer.service.OptimizerService1;
import com.iopharm.optimizer.service.OrToolsService;
import com.iopharm.optimizer.service.TestService;

@RestController
public class OptimizerController {
    @Autowired
    OptimizerService optimizerService;
    @Autowired
    OptimizerService1 optimizerService1;
    @Autowired
    IOTools ioTools;

    @Autowired
    OrToolsService orTools;
    
    @Autowired
    CbcSolver cbcSolver;

    @Autowired
    TestService testService;
    
    @PostMapping("/cbc/v1")
    void testCBC(@RequestBody CbcInput input){
    	System.out.println(input);
    	cbcSolver.solve(input);
    }

    @GetMapping("/optimal-solution/v1")
    ResponseEntity<Solution> test(){
        return new ResponseEntity<>(optimizerService.getOptimizedSolution(), HttpStatus.OK);
    }

    @GetMapping("/optimal-solution/v2")
    ResponseEntity<?> testV2(){
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/optimal-solution-or/v1")
    void testOR(){
        ioTools.solve();
    }

    @GetMapping("/optimal-solution-or/v2")
    void testORv2(){
        ioTools.getOptimizedSolution();
    }

    @GetMapping("/optimal-solution-or/v3")
    void testORv3(){
        ioTools.getOptimizedSolutionWithConstraint();
    }

    @GetMapping("/optimal-solution-or/v4")
    void testORv4() {
        orTools.solve2();
    }

    @GetMapping("/optimal-solution-or/v5")
    void testORv5() {
        List<WarehouseDTO> warehouses = new ArrayList<>();
        List<ProductDTO> products = new ArrayList<>();

        // Create Products with quantities
        ProductDTO productA = new ProductDTO(1L, "Product A", 80L); // Quantity is now 80
        ProductDTO productB = new ProductDTO(2L, "Product B", 70L); // Quantity is now 70
        products.add(productA);
        products.add(productB);

        // Create Warehouses
        WarehouseDTO warehouse1 = new WarehouseDTO(1L, "Warehouse 1", 1000.0, new ArrayList<>());
        warehouse1.getAvailableProducts().add(new WarehouseProductDTO(1L, 10.0, 100L));
        warehouse1.getAvailableProducts().add(new WarehouseProductDTO(2L, 20.0, 150L));
        warehouses.add(warehouse1);

        WarehouseDTO warehouse2 = new WarehouseDTO(2L, "Warehouse 2", 1500.0, new ArrayList<>());
        warehouse2.getAvailableProducts().add(new WarehouseProductDTO(1L, 15.0, 200L));
        warehouse2.getAvailableProducts().add(new WarehouseProductDTO(2L, 25.0, 100L));
        warehouses.add(warehouse2);

        // Solve the optimization problem
        orTools.solveOptimized(warehouses, products);
    }
    @GetMapping("/optimal-solution-or/v6")
    void testORv6() {
        List<WarehouseDTO> warehouses = new ArrayList<>();
        List<ProductDTO> products = new ArrayList<>();

        // Create Products with quantities
        ProductDTO productA = new ProductDTO(1L, "Product A", 80L); // Quantity is now 80
        ProductDTO productB = new ProductDTO(2L, "Product B", 70L); // Quantity is now 70
        products.add(productA);
        products.add(productB);

        // Create Warehouses
        WarehouseDTO warehouse1 = new WarehouseDTO(1L, "Warehouse 1", 1000.0, new ArrayList<>());
        warehouse1.getAvailableProducts().add(new WarehouseProductDTO(1L, 10.0, 100L));
        warehouse1.getAvailableProducts().add(new WarehouseProductDTO(2L, 20.0, 150L));
        warehouses.add(warehouse1);

        WarehouseDTO warehouse2 = new WarehouseDTO(2L, "Warehouse 2", 1500.0, new ArrayList<>());
        warehouse2.getAvailableProducts().add(new WarehouseProductDTO(1L, 15.0, 200L));
        warehouse2.getAvailableProducts().add(new WarehouseProductDTO(2L, 25.0, 100L));
        warehouses.add(warehouse2);

        // Solve the optimization problem
        orTools.solveOptimized(warehouses, products);
    }

    @GetMapping("/optimal-solution-or/v7")
    void testORv7() {
        orTools.solveWithMinOrderPostProcessing();
    }

    @GetMapping("/solve/1")
    void testAll() {
    	List<Warehouse1> warehouses = testService.getWarehouses1();
    	List<Product> demand = testService.getOrder1();
    	
    	orTools.solveFinal(warehouses, demand);
    }
}
