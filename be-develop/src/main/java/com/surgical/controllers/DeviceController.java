package com.surgical.controllers;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.entities.Device;
import com.surgical.enums.ComponentStatusFlag;
import com.surgical.exception.AppException;
import com.surgical.services.DeviceService;
import com.surgical.vo.DeviceDetailRequest;
import com.surgical.vo.DeviceReturnRequest;
import com.surgical.vo.DeviceStatusOperation;
import com.surgical.vo.DevicesRequest;
import com.surgical.vo.ResponseMsg;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/device")
public class DeviceController{

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    DeviceService deviceService;
    
    @PostMapping("/delete")
    public ResponseEntity<Object> deleteMD(HttpServletRequest request, @RequestBody DeviceStatusOperation deviceInfo){
        try{
            deviceInfo.setUpdater(Integer.parseInt(request.getSession().getAttribute("accountId").toString()));
            deviceService.updateStatus(deviceInfo, ComponentStatusFlag.DELETE);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeviceController suspend error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/scrapped")
    public ResponseEntity<Object> scrappedMD(HttpServletRequest request, @RequestBody DeviceStatusOperation deviceInfo){
        try{
            deviceInfo.setUpdater(Integer.parseInt(request.getSession().getAttribute("accountId").toString()));
            deviceService.updateStatus(deviceInfo, ComponentStatusFlag.SCRAPPED);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeviceController suspend error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/repair")
    public ResponseEntity<Object> sendForRepairMD(HttpServletRequest request, @RequestBody DeviceStatusOperation deviceInfo){
        try{
            deviceInfo.setUpdater(Integer.parseInt(request.getSession().getAttribute("accountId").toString()));
            deviceService.updateStatus(deviceInfo, ComponentStatusFlag.REPAIR);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeviceController rehabiliate error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/repair/return")
    public ResponseEntity<Object> returnMD(HttpServletRequest request, @RequestBody DeviceStatusOperation deviceInfo){
        try{
            deviceInfo.setUpdater(Integer.parseInt(request.getSession().getAttribute("accountId").toString()));
            // 維修完DEVICE的狀態要壓回2但是DEVICE_REPAIR_RECORD要壓5
            deviceService.updateStatus(deviceInfo, ComponentStatusFlag.REPAIR_DONE);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeviceController delete error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/detail")
    public ResponseEntity<Object> deviceDetail(@Valid DeviceDetailRequest request){
        try{
            return ResponseEntity.ok().body(deviceService.deviceDetail(request));
        }catch(Exception e){
            logger.error("DeviceController detail error => ", e);
            if(e instanceof AppException || e instanceof ParseException) {
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<Object> deviceList(@RequestParam(value = "typeId") Long typeId, @RequestParam(value = "division", required = false) Integer division, @RequestParam(value = "status", required = false) Integer status,@PageableDefault(size = 10) Pageable page){
        try{
            Map<String, Object> result = deviceService.deviceList(typeId, division, status, page);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("DeviceController list error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addDevices(@RequestBody DevicesRequest devicesRequest, HttpServletRequest request){
        try{
            devicesRequest.setUpdater(Integer.parseInt(request.getSession().getAttribute("accountId").toString()));
            List<Device> result = deviceService.createDevices(devicesRequest);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("DeviceController add error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/check/getInfo")
    public ResponseEntity<Object> checkDeviceGetInfo(@RequestParam(value = "qrcode") String qrcode){
        try{
            return ResponseEntity.ok().body(deviceService.checkDeviceGetInfo(qrcode));
        }catch(Exception e){
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            logger.error("Device Controller check getInfo error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/return")
    public ResponseEntity<Object> deviceReturn(@Valid @RequestBody DeviceReturnRequest request){
        try{
            deviceService.deviceReturn(request.getQrcode());
            return ResponseEntity.ok().build();
        }catch(Exception e){
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            logger.error("Device Controller return error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
