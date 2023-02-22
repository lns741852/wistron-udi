package com.surgical.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.exception.AppException;
import com.surgical.services.PrivilegeService;
import com.surgical.vo.CreateRoleInfo;
import com.surgical.vo.ResponseMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/role")
public class PrivilegeController{
    
    @Autowired
    PrivilegeService privilegeService;
    
    @PostMapping("/create")
    public ResponseEntity<Object> createOrUpdateRole(HttpServletRequest request, @Valid @RequestBody CreateRoleInfo info){
        try{
            Long accountId = Long.parseLong(request.getSession().getAttribute("accountId").toString());
            privilegeService.createOrUpdateRole(info, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            log.error("PrivilegeController createOrUpdateRole error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
}
