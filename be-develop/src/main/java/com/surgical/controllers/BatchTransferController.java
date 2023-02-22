package com.surgical.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.exception.AppException;
import com.surgical.services.BatchTransferService;
import com.surgical.vo.ResponseMsg;
import com.surgical.vo.TransferListBatchResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/batch/transfer")
public class BatchTransferController{
    
    private static final Logger logger = LoggerFactory.getLogger(BatchTransferController.class);
    
    @Autowired
    private BatchTransferService batchTransferService;
    
    @GetMapping("list")
    public ResponseEntity<Object> transferList(@RequestParam(value = "type",required = true) Integer type, @PageableDefault(size = 10) Pageable page){
        try{
            Page<TransferListBatchResponse> result = batchTransferService.transferList(type, page);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("BatchTransferController list error => {}", e);
            if(e instanceof AppException) {
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
}
