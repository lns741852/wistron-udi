package com.surgical.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.services.StationService;
import com.surgical.vo.ResponseMsg;
import com.surgical.vo.StationListVo;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/station")
public class StationController{

    private static final Logger logger = LoggerFactory.getLogger(StationController.class);

    @Autowired
    StationService stationService;

    @GetMapping("/list")
    public ResponseEntity<Object> stationList(){
        try{
            StationListVo result = stationService.getStationList();
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("StationController stationList error => {}", e);
            return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
        }
    }
}
