package com.surgical.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.exception.AppException;
import com.surgical.services.DeliverBatchService;
import com.surgical.vo.DeliverBatchDeliveryRequest;
import com.surgical.vo.DeliverBatchReceiveRequest;
import com.surgical.vo.ResponseMsg;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/deliverBatch")
public class DeliverBatchController{

    private static final Logger logger = LoggerFactory.getLogger(DeliverBatchController.class);

    @Autowired
    private DeliverBatchService deliverBatchService;

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestParam(value = "stationId") Long stationId, @PageableDefault(size = 10) Pageable pageable){
        try{
            return ResponseEntity.ok().body(deliverBatchService.list(stationId, pageable));
        }catch(Exception e){
            logger.error("DeliverBatchController deliverBatch list error => {}", e);
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @PostMapping("/delivery")
    public ResponseEntity<Object> delivery(@Valid @RequestBody DeliverBatchDeliveryRequest deliveryRequest, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            deliverBatchService.delivery(deliveryRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeliverBatchController delivery error => {} ",e);
            if(e instanceof AppException || e instanceof MethodArgumentNotValidException) {
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/package/list")
    public ResponseEntity<Object> packageList(@RequestParam(value = "deliverBatchId") Long deliverBatchId){
        try{
            return ResponseEntity.ok().body(deliverBatchService.packageList(deliverBatchId));
        }catch(Exception e){
            logger.error("DeliverBatchController deliverBatch list error => {}", e);
            return ResponseEntity.badRequest().body(e);
        }
    }
    @PostMapping("/receive")
    public ResponseEntity<Object> receive (@Valid @RequestBody DeliverBatchReceiveRequest receiveRequest, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            deliverBatchService.receive(receiveRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("DeliverBatchController receive error => {}", e);
            if(e instanceof AppException || e instanceof MethodArgumentNotValidException) {
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
}
