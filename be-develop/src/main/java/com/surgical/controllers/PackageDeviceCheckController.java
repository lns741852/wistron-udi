package com.surgical.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.enums.ComponentStatusFlag;
import com.surgical.exception.AppException;
import com.surgical.services.PackageDeviceCheckService;
import com.surgical.vo.DeviceCheckGetInfoVo;
import com.surgical.vo.PackageDeviceCheckDetailResponse;
import com.surgical.vo.PackageDeviceCheckDto;
import com.surgical.vo.ResponseMsg;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/packageDeviceCheck")
public class PackageDeviceCheckController{

    private static final Logger logger = LoggerFactory.getLogger(PackageDeviceCheckController.class);

    @Autowired
    private PackageDeviceCheckService packageDeviceCheckService;

    @GetMapping("/detail/{trackingId}")
    public ResponseEntity<Object> detail(@PathVariable(value = "trackingId") Long trackingId){
        try{
            PackageDeviceCheckDetailResponse result = packageDeviceCheckService.detail(trackingId);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("PackageDeviceCheckController detail error => {} ", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @PostMapping("/process/{trackingId}")
    public ResponseEntity<Object> process(HttpServletRequest request, @PathVariable(value = "trackingId")Long trackingId, @RequestBody PackageDeviceCheckDto packageDeviceCheckDto){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            packageDeviceCheckService.doProcess(accountId, trackingId, packageDeviceCheckDto);
            return ResponseEntity.ok().build();
        }catch (AppException appException) {
            logger.error("PackageDeviceCheckController process error => {} ", appException);
            return ResponseEntity.badRequest().body(new ResponseMsg(appException.getMessage()));
        }catch(Exception e){
            logger.error("PackageDeviceCheckController process error => {} ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/scan/process/{trackingId}")
    public ResponseEntity<Object> scanProcess(HttpServletRequest request, @PathVariable(value = "trackingId", required  = true)Long trackingId, @RequestBody(required  = true) List<DeviceCheckGetInfoVo> devices){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            if(CollectionUtils.isEmpty(devices)) {
                throw new AppException("傳入物件不得為空值");
            }
            List<Integer> statusIntegers = new ArrayList<Integer>();
            statusIntegers.add(ComponentStatusFlag.RECEIVE_SCAN_DONE.getValue());
            statusIntegers.add(ComponentStatusFlag.UNSCANABLE.getValue());
            statusIntegers.add(ComponentStatusFlag.MISSING.getValue());
            for(DeviceCheckGetInfoVo deviceCheckGetInfoVo : devices){
                if(null==deviceCheckGetInfoVo.getId()||0>=deviceCheckGetInfoVo.getId()) {
                    throw new AppException("傳入物件ID有誤");
                }else if(null==deviceCheckGetInfoVo.getStatus()|| !statusIntegers.contains(deviceCheckGetInfoVo.getStatus())){
                    throw new AppException("傳入物件狀態有誤");
                }
            }
            packageDeviceCheckService.scanProcess(accountId, trackingId, devices);
            return ResponseEntity.ok().build();
        }catch (AppException e) {
            logger.error("PackageDeviceCheckController detail error => {} ", e);
            return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
        }catch(Exception e){
            logger.error("PackageDeviceCheckController detail error => {} ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
