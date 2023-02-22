package com.surgical.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.exception.AppException;
import com.surgical.services.UserService;
import com.surgical.vo.ResponseMsg;
import com.surgical.vo.UserCreateRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController{

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Object> userCreate(@Valid @RequestBody UserCreateRequest userRequest, HttpServletRequest request){
        try{
            Long accountId = Long.parseLong(request.getSession().getAttribute("accountId").toString());
            userService.createUser(userRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("UserController user create error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<Object> userList(@RequestParam(name = "userName", required = false) String userName, @RequestParam(name = "roleName", required = false) String roleName, @PageableDefault(size = 10) Pageable pageable){
        try{
            return ResponseEntity.ok().body(userService.list(userName, roleName, pageable));
        }catch(Exception e){
            logger.error("UserController user list error => {}", e);
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    
}
