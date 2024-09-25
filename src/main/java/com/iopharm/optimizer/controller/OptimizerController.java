package com.iopharm.optimizer.controller;

import com.iopharm.optimizer.model.Solution;
import com.iopharm.optimizer.service.IOTools;
import com.iopharm.optimizer.service.OptimizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OptimizerController {
    @Autowired
    OptimizerService optimizerService;
    @Autowired
    IOTools ioTools;


    @GetMapping("/optimal-solution")
    ResponseEntity<Solution> test(){
        return new ResponseEntity<>(optimizerService.getOptimizedSolution(), HttpStatus.OK);
    }

    @GetMapping("/optimal-solution-or/v1")
    void testOR(){
        ioTools.solve();
    }

    @GetMapping("/optimal-solution-or/v2")
    void testORv2(){
        ioTools.getOptimizedSolution();
    }
}
