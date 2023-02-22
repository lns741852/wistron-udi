package com.surgical.controllers;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.surgical.services.WashingBatchService;
import com.surgical.vo.ResponseMsg;
import com.surgical.vo.WashingBatchCreateRequest;
import com.surgical.vo.WashingBatchListDto;
import com.surgical.vo.WashingBatchListVo;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/washingBatch")
public class WashingBatchController{

    private static final Logger logger = LoggerFactory.getLogger(WashingBatchController.class);

    @Autowired
    private WashingBatchService washingBatchService;

    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody WashingBatchCreateRequest createRequest, HttpServletRequest request){
        try{
            Long accountId = Long.parseLong(request.getSession().getAttribute("accountId").toString());
            washingBatchService.create(createRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("WashingBatchController create error => {}", e);
            if (e instanceof AppException || e instanceof MethodArgumentNotValidException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<Object> getWashingBatchList(WashingBatchListDto washingBatchDto, @PageableDefault(size = 10) Pageable page){
        try{
            Page<WashingBatchListVo> vos = washingBatchService.getWashingBatchList(washingBatchDto, page);
            return ResponseEntity.ok().body(vos);
        }catch (AppException appException) {
            logger.error("WashingBatchController create error => {}", appException);
            return ResponseEntity.badRequest().body(new ResponseMsg(appException.getMessage()));
        }
        catch(Exception e){
            logger.error("WashingBatchController create error => {}", e);
            return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
        }
    }
    
    @PostMapping("/finish")
    public ResponseEntity<Object> finish(@RequestParam(value = "id", required = true) Long id, @RequestParam(value = "isSuccess", required = false, defaultValue = "true") Boolean isSuccess, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            washingBatchService.finish(id, isSuccess, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("SterilizedBatchController finish error => ", e);
            if (e instanceof AppException || e instanceof MethodArgumentNotValidException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
}
