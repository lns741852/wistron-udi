package com.surgical.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.surgical.services.DeviceService;
import com.surgical.services.FileSystemStorageService;
import com.surgical.vo.FileDeleteVo;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FileUploadController{

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/fileupload/{fileId}")
    public ResponseEntity<Object> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable(value = "fileId") Long fileId, HttpServletRequest request){
        String fileName = file.getOriginalFilename();
        boolean isAllowed = FileSystemStorageService.isExtAllowed(FileSystemStorageService.FILEUPLOAD, fileName);
        if (!isAllowed){
            return ResponseEntity.badRequest().body("illegal ext name!");
        }
        Map<String, Object> result = new HashMap<>();
        String newfileName = fileSystemStorageService.store(file, fileId);
        logger.info("Saved file successfully, fileName=> " + newfileName);
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (StringUtils.isBlank(ipAddress)){
            ipAddress = request.getRemoteAddr();
        }
        Long newFileId = deviceService.saveDeviceImageFile(ipAddress, newfileName, file.getSize(), fileId);
        result.put("fileName", newfileName);
        result.put("fileId", newFileId);
        return ResponseEntity.ok().body(result);
    }
    
    @PostMapping("/fileDelete")
    public ResponseEntity<Object> fileDelete(@RequestBody FileDeleteVo request){
        try{
            fileSystemStorageService.deleteAll(request);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
