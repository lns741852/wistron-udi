package com.surgical.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.surgical.exception.AppException;
import com.surgical.services.SystemPropertiesService;
import com.surgical.vo.ResponseMsg;
import com.surgical.vo.SystemPropertiesRequestVo;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/systemProperties")
public class SystemPropertiesController{

    private static final Logger logger = LoggerFactory.getLogger(SystemPropertiesController.class);
    
    @Autowired
    SystemPropertiesService systemPropertiesService;

    @PostMapping("/create")
    public ResponseEntity<Object> create(HttpServletRequest request, @Valid @RequestBody SystemPropertiesRequestVo systemPropertiesRequest){
        try{
            Long accountId = Long.parseLong(request.getSession().getAttribute("accountId").toString());
            return ResponseEntity.ok().body(systemPropertiesService.create(accountId, systemPropertiesRequest));
        }catch(Exception e){
            logger.error("SystemPropertiesController create error => ", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Object> update(HttpServletRequest request, @PathVariable(value = "id") Long id,  @Valid @RequestBody SystemPropertiesRequestVo systemPropertiesRequest){
        try{
            Long accountId = Long.parseLong(request.getSession().getAttribute("accountId").toString());
            systemPropertiesService.update(accountId, id, systemPropertiesRequest);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("SystemPropertiesController update error => ", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestParam(value = "search", required = false) String search){
        try{
            return ResponseEntity.ok().body(systemPropertiesService.list(search));
        }catch(Exception e){
            logger.error("SystemPropertiesController list error => ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Long id){
        try{
            systemPropertiesService.delete(id);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            logger.error("SystemPropertiesController delete error => ", e);
            if (e instanceof AppException){
                return ResponseEntity.badRequest().body(new ResponseMsg(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
