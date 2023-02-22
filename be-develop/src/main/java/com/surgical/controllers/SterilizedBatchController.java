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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.exception.AppException;
import com.surgical.services.SterilizedBatchService;
import com.surgical.vo.ResponseMsg;
import com.surgical.vo.SterilizedBatchCreateRequest;
import com.surgical.vo.SterilizedBatchFinishRequest;
import com.surgical.vo.SterilizedBatchVo;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/sterilizedBatch")
public class SterilizedBatchController{

    private static final Logger logger = LoggerFactory.getLogger(SterilizedBatchController.class);

    @Autowired
    private SterilizedBatchService sterilizedBatchService;

    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody SterilizedBatchCreateRequest createRequest, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            sterilizedBatchService.create(createRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("SterilizedBatchController create error => ", e);
            if (e instanceof AppException || e instanceof MethodArgumentNotValidException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @PostMapping("/finish")
    public ResponseEntity<Object> finish(@Valid @RequestBody SterilizedBatchFinishRequest finishRequest, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            sterilizedBatchService.finish(finishRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("SterilizedBatchController finish error => ", e);
            if (e instanceof AppException || e instanceof MethodArgumentNotValidException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestParam(value = "type", required = false) Integer type, @RequestParam(value = "name", required = false) String name, @PageableDefault(size = 10) Pageable page){
        try{
            Page<SterilizedBatchVo> result = sterilizedBatchService.getList(type, name, page);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("SterilizedBatchController list error => ", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<Object> detail(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "trackingId", required = false) Long trackingId){
        try{
            return ResponseEntity.ok().body(sterilizedBatchService.detail(id, trackingId));
        }catch(Exception e){
            logger.error("SterilizedBatchController detail error => ", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/package/list")
    public ResponseEntity<Object> packageList(@RequestParam(value = "id", required = true) Long id){
        try{
            return ResponseEntity.ok().body(sterilizedBatchService.packageList(id));
        }catch(Exception e){
            logger.error("SterilizedBatchController package list error => ", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
