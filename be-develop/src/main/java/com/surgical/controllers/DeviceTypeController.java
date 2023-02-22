package com.surgical.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.web.PageableDefault;

import com.surgical.exception.AppException;
import com.surgical.services.DeviceTypeService;
import com.surgical.vo.DeviceTypeAddRequest;
import com.surgical.vo.DeviceTypeList;
import com.surgical.vo.ResponseMsg;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/deviceType")
public class DeviceTypeController{

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);

    @Autowired
    DeviceTypeService deviceTypeService;
   
    @PostMapping(value = "/add")
    public ResponseEntity<Object> addDeviceType(@RequestBody DeviceTypeAddRequest deviceType, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            Long result = deviceTypeService.addDeviceType(deviceType, accountId);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping(value = "/detail")
    public ResponseEntity<Object> detail(@RequestParam(value = "typeId") Long typeId){
        try {
            return ResponseEntity.ok().body(deviceTypeService.detail(typeId));
        }catch (Exception e) {
            logger.error("DeviceTypeController detail error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getDeviceTypeList(@RequestParam(value = "divisionId", required = false) Long divisionId, @RequestParam(value = "nameScientific", required = false) String nameScientific, @RequestParam(value = "spec", required = false) String spec, @PageableDefault(size = 10) Pageable page){
        try{
            Page<DeviceTypeList> result = deviceTypeService.getDeviceTypeList(divisionId, nameScientific, spec, page);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("DeviceTypeController getDeviceTypeList error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "update")
    public ResponseEntity<Object> updateDeviceType(@RequestBody DeviceTypeAddRequest deviceType, HttpServletRequest request){
        try {
            Long accoutId = (Long) request.getSession().getAttribute("accountId");
            deviceTypeService.updateDeviceType(deviceType, accoutId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/model/list/{deviceTypeId}")
    public ResponseEntity<Object> deviceModelList(@PathVariable(value = "deviceTypeId") Long typeId, @RequestParam(value = "info", defaultValue = "false", required = false) Boolean info, @RequestParam(value = "divisionId", required = false) Long divisionId){
        try{
            List<Map<String, Object>> resultList = deviceTypeService.deviceModelList(typeId, info, divisionId);
            return ResponseEntity.ok().body(resultList);
        }catch(Exception e){
            logger.error("DeviceTypeController deviceModelList error => " + e.getMessage());
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
