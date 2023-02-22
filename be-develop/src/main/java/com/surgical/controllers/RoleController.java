package com.surgical.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.services.RoleService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/role")
public class RoleController{

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    
    @Autowired
    RoleService roleService;

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestParam(value = "permissions", defaultValue = "false", required = false) Boolean permissions){
        try{
            return ResponseEntity.ok().body(roleService.list(permissions));
        }catch(Exception e){
            logger.error("RoleController list error => {}", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
