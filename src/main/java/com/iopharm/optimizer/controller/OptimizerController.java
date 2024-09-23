package com.iopharm.optimizer.controller;

import com.iopharm.optimizer.model.Solution;
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


    @GetMapping("/optimal-solution")
    ResponseEntity<Solution> test(){
        return new ResponseEntity<>(optimizerService.getOptimizedSolution(), HttpStatus.OK);
    }
}
