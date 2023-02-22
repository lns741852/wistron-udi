package com.surgical.controllers;

import java.util.List;

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
import com.surgical.services.SurgicalApplyService;
import com.surgical.vo.PackageOrderCreateRequest;
import com.surgical.vo.PackageOrderInfoListVo;
import com.surgical.vo.ResponseMsg;
import com.surgical.vo.SurgicalApplyCancelRequest;
import com.surgical.vo.SurgicalApplyDto;
import com.surgical.vo.SurgicalApplyGrantDetailInfoVo;
import com.surgical.vo.SurgicalApplyGrantProcessRequest;
import com.surgical.vo.SurgicalApplyCreateRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/surgicalApply")
public class SurgicalApplyController{
    private static final Logger logger = LoggerFactory.getLogger(SurgicalApplyController.class);
    
    @Autowired
    SurgicalApplyService surgicalApplyService;

    @PostMapping("/grant/process")
    public ResponseEntity<Object> grantProcess(@Valid @RequestBody SurgicalApplyGrantProcessRequest grantRequest, HttpServletRequest request){
        try{
            Long accountId = Long.parseLong(request.getSession().getAttribute("accountId").toString());
            surgicalApplyService.grantProcess(grantRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("SurgicalApplyController grant process error => {} ", e);
            if (e instanceof AppException || e instanceof MethodArgumentNotValidException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @GetMapping("/detail")
    public ResponseEntity<Object> getDetail(SurgicalApplyDto surgicalApplyDto){
        try{
            SurgicalApplyGrantDetailInfoVo vo = surgicalApplyService.getSurgicalApplyDetail(surgicalApplyDto);
            return ResponseEntity.ok().body(vo);
        }catch(AppException appException){
            return ResponseEntity.badRequest().body(new ResponseMsg(appException.getMessage()));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @GetMapping("/order/list")
    public ResponseEntity<Object> orderList(@RequestParam(value = "status", required = false) Integer status, @RequestParam(value = "sort", defaultValue = "ASC") String sort, @PageableDefault(size = 10) Pageable page){
        try{
            Page<PackageOrderInfoListVo> result = surgicalApplyService.orderList(status, sort, page);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("SurgicalApplyController grant list error => {}", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestParam(value = "qrcode", required = false) String qrcode, @RequestParam(value = "status", required = false) List<Integer> statuses, @PageableDefault(size = 10) Pageable page){
        try{
            Page<PackageOrderInfoListVo> result = surgicalApplyService.getList(qrcode, statuses, page);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("SurgicalApplyController list error => {}", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody SurgicalApplyCreateRequest request, HttpServletRequest servletRequest ){
        try{
            Long accountId = Long.parseLong(servletRequest.getSession().getAttribute("accountId").toString());
            surgicalApplyService.create(request, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("SurgicalApplyController create error => {}", e);
            if (e instanceof AppException || e instanceof MethodArgumentNotValidException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @PostMapping("/order/cancel")
    public ResponseEntity<Object> orderCancel(HttpServletRequest servletRequest, @Valid @RequestBody SurgicalApplyCancelRequest surgicalApplyCancelRequest){
        try{
            Long accountId = Long.parseLong(servletRequest.getSession().getAttribute("accountId").toString());
            surgicalApplyService.orderCancel(surgicalApplyCancelRequest.getOrderId(), accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("SurgicalApplyController order cancel error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @PostMapping("/order/create")
    public ResponseEntity<Object> orderCreate(@Valid @RequestBody PackageOrderCreateRequest packageOrderCreate){
    	try {
    		surgicalApplyService.packageOrderCreate(packageOrderCreate);
    		return ResponseEntity.ok().build();
    	}catch(Exception e) {
    		logger.error("SurgicalApplyController order cancel error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
    	}
    }
}
