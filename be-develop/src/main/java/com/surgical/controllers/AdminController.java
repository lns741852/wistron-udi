package com.surgical.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.entities.Admin;
import com.surgical.enums.StatusFlag;
import com.surgical.services.AdminService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AdminController{

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    AdminService adminService;

    @GetMapping("/getAdminList")
    public ResponseEntity<Object> getAdminList(@PageableDefault(value = 10) Pageable pageable){
        Page<Admin> adminList = null;
        try{
            adminList = adminService.list(pageable);
        }catch(Exception e){
            logger.error("『 AdminController 』 list error => ", e);
        }
        return ResponseEntity.ok().body(adminList);
    }

    @PostMapping("/addAdmin")
    public ResponseEntity<Object> addAdmin(@RequestBody Admin admin){
        try{
            adminService.create(admin);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("AdminController create error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/updateAdmin")
    public ResponseEntity<Object> updateAdmin(@RequestBody Admin admin){
        try{
            adminService.update(admin);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("AdminController update error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/suspendAdmin")
    public ResponseEntity<Object> suspendAdmin(@RequestParam(value = "id") Long id, @RequestParam(value = "updater") String updater){
        try{
            adminService.updateStatus(id, StatusFlag.SUSPEND, updater);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("AdminController suspend error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/rehabiliateAdmin")
    public ResponseEntity<Object> rehabiliateAdmin(@RequestParam(value = "id") Long id, @RequestParam(value = "updater") String updater){
        try{
            adminService.updateStatus(id, StatusFlag.NORMAL, updater);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("AdminController rehabiliate error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/deleteAdmin")
    public ResponseEntity<Object> deleteAdmin(@RequestParam(value = "id") Long id, @RequestParam(value = "updater") String updater){
        try{
            adminService.updateStatus(id, StatusFlag.DELETE, updater);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("AdminController delete error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
