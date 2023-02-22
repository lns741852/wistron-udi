package com.surgical.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.services.LoginService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class LoginController{

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password, HttpServletResponse response){
        try{
            Map<String, Object> result = loginService.login(account, password);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("LoginController login error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.ok().body("OK");
    }
}
