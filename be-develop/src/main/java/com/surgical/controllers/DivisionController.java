package com.surgical.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.exception.AppException;
import com.surgical.services.DivisionService;
import com.surgical.vo.DivisionInfo;
import com.surgical.vo.ResponseMsg;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/division")
public class DivisionController{

    private static final Logger logger = LoggerFactory.getLogger(DivisionController.class);

    @Autowired
    DivisionService divisionService;

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestParam(name = "status", required = false) Integer status){
        try{
            List<DivisionInfo> result = divisionService.getList(status);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("DivisionController list error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<Object> creatDivision(@RequestBody DivisionInfo info, HttpServletRequest request){
        try{
            if(StringUtils.isEmpty(info.getName()) || StringUtils.isEmpty(info.getCode())) {
                throw new AppException("科別名稱與科別代碼不得為空");
            }
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            divisionService.createDivison(info, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DivisionController createDivision error => ", e);
            return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
        }
    }
    
    @PostMapping("/status/update")
    public ResponseEntity<Object> updateDivisionStatus(@RequestBody DivisionInfo info, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            divisionService.updateStatus(info, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DivisionController createDivision error => ", e);
            return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
        }
    }
}
