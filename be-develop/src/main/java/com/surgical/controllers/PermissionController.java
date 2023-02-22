package com.surgical.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.services.PermissionService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/permission")
public class PermissionController{

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);
    
    @Autowired
    PermissionService permissionService;

    @GetMapping("/list")
    public ResponseEntity<Object> list(){
        try{
            return ResponseEntity.ok().body(permissionService.list());
        }catch(Exception e){
            logger.error("PermissionController list error => {}", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
