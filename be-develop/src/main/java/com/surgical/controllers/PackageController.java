package com.surgical.controllers;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

import com.surgical.entities.PackageGetInfoRequest;
import com.surgical.exception.AppException;
import com.surgical.services.PackageService;
import com.surgical.vo.AddPackageAmountRecordRequest;
import com.surgical.vo.AddPackageAmountRecordResponse;
import com.surgical.vo.PackageConfigInfoResponseVo;
import com.surgical.vo.ApplyAddAmountRequest;
import com.surgical.vo.ApplyUpdateAmountRequest;
import com.surgical.vo.PackageApplyDetailResponseVo;
import com.surgical.vo.PackageConfigListVo;
import com.surgical.vo.PackageExpiredListRequest;
import com.surgical.vo.PackageInfo;
import com.surgical.vo.PackageRequestVo;
import com.surgical.vo.ProcessRequest;
import com.surgical.vo.RepackRequest;
import com.surgical.vo.ReportPackageHistoryListRequest;
import com.surgical.vo.ReportPackageHistoryListResponse;
import com.surgical.vo.ResponseMsg;

import io.swagger.annotations.Api;

@Api(tags="PackageController",description="類別檔案說明")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/package")
public class PackageController{

    private static final Logger logger = LoggerFactory.getLogger(PackageController.class);

    @Autowired
    PackageService packageService;

    @PostMapping("/config/create")
    public ResponseEntity<Object> configCreate(HttpServletRequest request, @Valid @RequestBody PackageRequestVo packageRequest){
        try{
            Long accountId = Long.parseLong(request.getSession().getAttribute("accountId").toString());
            packageService.configCreate(accountId, packageRequest);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("PackageController config create error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }

    @PostMapping("/apply/list")
    public ResponseEntity<Object> applyList(@RequestBody AddPackageAmountRecordRequest request, @PageableDefault(size = 10) Pageable page){
        try{
            Page<AddPackageAmountRecordResponse> result = packageService.applyList(request, page);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("PackageController apply list error => {}", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/apply/cancelApplication")
    public ResponseEntity<Object> cancelApplication(@RequestBody ApplyUpdateAmountRequest cancelRequest, HttpServletRequest request){
        try{
            Long accountId = Long.parseLong(request.getSession().getAttribute("accountId").toString());
            packageService.cancelAddPackageAmountRecord(cancelRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("PackageController cancelApplication error => {}", e);
            return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
        }
    }

    @GetMapping("/config/list")
    public ResponseEntity<Object> configList(@RequestParam(value = "divisionId", required = false) Integer divisionId, 
                                            @RequestParam(value = "isActive", required = false) Integer isActive, 
                                            @RequestParam(value = "configName", required = false) String configName, 
                                            @RequestParam(value = "configCode", required = false) String configCode,
                                            @RequestParam(value = "showCount", required = false) Integer showCount,
                                            @PageableDefault(size = 10) Pageable page){
        try{
            Page<PackageConfigListVo> result = packageService.getConfigList(divisionId, isActive, configName, configCode, showCount, page);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("PackageController config list error => {}", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/apply/addAmount")
    public ResponseEntity<Object> applyAddPackageAmount(@Valid @RequestBody ApplyAddAmountRequest applyRequest, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            packageService.applyAddPackageAmount(applyRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("PackageController apply package amount error => {}", e);
            if (e instanceof AppException || e instanceof IllegalArgumentException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/apply/updateAmount")
    public ResponseEntity<Object> applyUpdatePackageAmount(@Valid @RequestBody ApplyUpdateAmountRequest updateRequest, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            packageService.applyUpdatePackageAmount(updateRequest, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("PackageController apply update package amount error => {}", e);
            if (e instanceof AppException || e instanceof IllegalArgumentException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/config/detail")
    public ResponseEntity<Object> configDetail(@RequestParam(value = "configId") Long configId){
        try{
            PackageConfigInfoResponseVo packageConfigInfoResponse = packageService.configDetail(configId);
            return ResponseEntity.ok().body(packageConfigInfoResponse);
        }catch(Exception e){
            logger.error("PackageController config detail error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }
    
    @GetMapping("/apply/detail")
    public ResponseEntity<Object> applyDetail(@RequestParam(value = "id", required = true) Long id){
        try{
            PackageApplyDetailResponseVo packageApplyDetailResponse = packageService.applyDetail(id);
            return ResponseEntity.ok().body(packageApplyDetailResponse);
        }catch(Exception e){
            logger.error("PackageController apply detail error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e);
        }
    }

    @PostMapping("/process")
    public ResponseEntity<Object> process(@Valid @RequestBody ProcessRequest processReqeust, HttpServletRequest request){
        try{
            Long accountId = (Long) request.getSession().getAttribute("accountId");
            packageService.process(processReqeust, accountId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            if (e instanceof AppException || e instanceof MethodArgumentNotValidException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }else if(e instanceof DataIntegrityViolationException) {
                return ResponseEntity.badRequest().body(new ResponseMsg("輸入之 qrcode 已有綁定的 包盤"));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getInfo")
    public ResponseEntity<Object> getInfo(PackageGetInfoRequest packageGetInfoRequest, HttpServletRequest request){
        try{
            if (packageGetInfoRequest.getQrcode() == null && packageGetInfoRequest.getPackageCode() == null){
                return ResponseEntity.badRequest().body("沒帶qrcode or packageCode");
            }
            PackageInfo result = packageService.getInfo(packageGetInfoRequest);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            logger.error("PackageController getInfo error => {}", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/repack/{packageId}")
    public ResponseEntity<Object> repack(@Valid @RequestBody RepackRequest request, @PathVariable(name = "packageId",required = true) Long packageId, HttpServletRequest httpServletRequest){
        try{
            Long accountId = (Long) httpServletRequest.getSession().getAttribute("accountId");
            packageService.repack(request, accountId, packageId);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            if (e instanceof AppException || e instanceof MethodArgumentNotValidException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<Object> dashboard(){
        try{
            return ResponseEntity.ok().body(packageService.dashboard());
        }catch(Exception e){
            logger.error("PackageController dashboard error => {}", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/history/list")
    public ResponseEntity<Object> historyList(@Valid ReportPackageHistoryListRequest request){
        try{
            Page<ReportPackageHistoryListResponse> result = packageService.historyList(request);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            logger.error("PackageController history list error => {}", e);
            if (e instanceof AppException || e instanceof ParseException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/expired/list")
    public ResponseEntity<Object> expiredList(@Valid PackageExpiredListRequest packageExpiredListRequest){
        try{
            return ResponseEntity.ok().body(packageService.expiredList(packageExpiredListRequest));
        }catch(Exception e){
            logger.error("PackageController expired list error => {}", e);
            if (e instanceof AppException || e instanceof ParseException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tracking/device/list/{trackingId}")
    public ResponseEntity<Object> trackingDeviceList(@PathVariable(value = "trackingId") Long trackingId){
        try{
            return ResponseEntity.ok().body(packageService.trackingDeviceList(trackingId));
        }catch(Exception e){
            logger.error("PackageController tracking device list error => {}", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/tracking/{trackingId}")
    public ResponseEntity<Object> tracking(@PathVariable(value = "trackingId") Long trackingId){
        try{
            return ResponseEntity.ok().body(packageService.tracking(trackingId));
        }catch(Exception e){
            logger.error("PackageController tracking error => {}", e);
            if (e instanceof AppException ){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
