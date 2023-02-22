package com.surgical.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.exception.AppException;
import com.surgical.services.PackageOrderService;
import com.surgical.vo.ResponseMsg;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/packageOrder")
public class PackageOrderController{

    private static final Logger logger = LoggerFactory.getLogger(PackageOrderController.class);

    @Autowired
    PackageOrderService packageOrderService;
    
    /* 訂單狀態更新 */
    @PostMapping("/status/update")
    public ResponseEntity<Object> statusUpdate(HttpServletRequest request, @RequestParam(value = "orderId") Long orderId, @RequestParam(value = "step") Integer step){
        try{
            Long accountId = Long.parseLong(request.getSession().getAttribute("accountId").toString());
            packageOrderService.statusUpdate(accountId, orderId, step);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("PackageOrderController status update error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
}
