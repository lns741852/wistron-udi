package com.surgical.controllers;

import java.text.ParseException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.exception.AppException;
import com.surgical.services.ReportService;
import com.surgical.vo.PackagePackingReportRequest;
import com.surgical.vo.PackagePackingReportResponse;
import com.surgical.vo.ResponseMsg;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/report")
public class ReportController{

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    
    @Autowired
    private ReportService reportService;

    @GetMapping("/sterilization/sterilizer")
    public ResponseEntity<Object> sterilizationSterilizer(@RequestParam(value = "start") String start, @RequestParam(value = "end") String end){
        try{
            return ResponseEntity.ok().body(reportService.sterilizationSterilizer(start, end));
        }catch(Exception e){
            logger.error("ReportController sterilizationSterilizer error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/package/packing")
    public ResponseEntity<Object> packagePacking(@Valid PackagePackingReportRequest request){
        try{
            PackagePackingReportResponse result = reportService.packagePackingReport(request);
            return ResponseEntity.ok(result);
        }catch(Exception e){
            logger.error("ReportController packagePacking error => ", e);
            if (e instanceof AppException || e instanceof ParseException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/circulation/washing")
    public ResponseEntity<Object> circulationWashing(@RequestParam(value = "start") String start, @RequestParam(value = "end") String end){
        try{
            return ResponseEntity.ok().body(reportService.circulationWashing(start, end));
        }catch(Exception e){
            logger.error("ReportController circulationWashing error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
