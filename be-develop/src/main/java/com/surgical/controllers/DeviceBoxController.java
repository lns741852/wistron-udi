package com.surgical.controllers;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

import com.surgical.enums.ComponentStatusFlag;
import com.surgical.exception.AppException;
import com.surgical.services.DeviceBoxService;
import com.surgical.vo.CreateDeviceBox;
import com.surgical.vo.DeviceBoxMaintainInfo;
import com.surgical.vo.ResponseMsg;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/deviceBox")
public class DeviceBoxController{

    private static final Logger logger = LoggerFactory.getLogger(DeviceBoxController.class);

    @Autowired
    DeviceBoxService deviceBoxService;

    @GetMapping("/list")
    public ResponseEntity<Object> getDeviceBoxList(@RequestParam(value = "status", required = false) Integer status, @PageableDefault(size = 10) Pageable page){
        try{
            Map<String, Object> result = deviceBoxService.deviceBoxList(status,page);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("DeviceBoxController list error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/details")
    public ResponseEntity<Object> getDeviceBoxDetails(@RequestParam(value = "qrcode") String qrcode){
        try{
            return ResponseEntity.ok().body(deviceBoxService.details(qrcode));
        }catch(Exception e){
            logger.error("DeviceBoxController details error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<Object> createDeviceBox(@RequestBody CreateDeviceBox deviceBoxRequest, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            deviceBoxService.add(accountId, deviceBoxRequest);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeviceBoxController add error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/repair")
    public ResponseEntity<Object> repairDeviceBox(@RequestBody DeviceBoxMaintainInfo deviceBoxMaintain, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            deviceBoxService.updateStatus(deviceBoxMaintain, accountId, ComponentStatusFlag.REPAIR);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeviceBoxController rehabiliate error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/repair/return")
    public ResponseEntity<Object> returnRepairedDeviceBox(@RequestBody DeviceBoxMaintainInfo deviceBoxMaintain, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            deviceBoxService.updateStatus(deviceBoxMaintain, accountId, ComponentStatusFlag.REPAIR_DONE);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeviceBoxController delete error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/scrapped")
    public ResponseEntity<Object> scrapDeviceBox(@RequestBody DeviceBoxMaintainInfo deviceBoxMaintain, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            deviceBoxService.updateStatus(deviceBoxMaintain, accountId, ComponentStatusFlag.SCRAPPED);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeviceBoxController suspend error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/info")
    public ResponseEntity<Object> getDeviceBoxInfo(@RequestParam(value = "qrcode") String qrcode){
        try{
            Map<String, Object> result = deviceBoxService.getDeviceBoxInfo(qrcode);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("DeviceBoxController  error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
