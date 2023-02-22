package com.surgical.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.surgical.config.StorageProperties;
import com.surgical.entities.DeviceImage;
import com.surgical.entities.FileResource;
import com.surgical.exception.AppException;
import com.surgical.exception.StorageException;
import com.surgical.exception.StorageFileNotFoundException;
import com.surgical.repositories.DeviceImageRepository;
import com.surgical.repositories.FileResourceRepository;
import com.surgical.vo.FileDeleteVo;

@Service
public class FileSystemStorageService{

    private final Path rootLocation = Paths.get("/opt/sims/upload");

    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);

    public final static Map<String, List<String>> uploadAllowedExt;

    public final static String FILEUPLOAD = "fileupload/";

    public final static String GROUPLOAD = "lineusergroup/savefiles";
    
    @Autowired
    private FileResourceRepository fileResourceRepository;

    @Autowired
    private DeviceImageRepository deviceImageRepository;
    
    static{
        Map<String, List<String>> m = new HashMap<>();
        List<String> fileupload_ = Arrays.asList("csv", "jpg", "jpeg", "png", "mp4", "mp3", "wav", "m4a");
        m.put("fileupload/", fileupload_);
        List<String> lineusergroup_savefiles = Arrays.asList("csv");
        m.put("lineusergroup/savefiles", lineusergroup_savefiles);
        uploadAllowedExt = m;
    }

    public static String getExt(String fileName){
        String ext = "";
        if (fileName == null){
            return ext;
        }
        if (fileName.lastIndexOf(".") >= 0){
            ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return ext;
    }

    public static boolean isExtAllowed(String module, String fileName){
        String ext = getExt(fileName);
        boolean allowed = false;
        if (uploadAllowedExt.containsKey(module)){
            allowed = uploadAllowedExt.get(module).contains(ext);
        }
        return allowed;
    }

    public static boolean isDeleted(String writeDir, String srcFile, int waitSecs){
        boolean deleted = false;
        String ext = getExt(srcFile);
        String randFile = writeDir + UUID.randomUUID().toString() + "." + ext;
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));){
            Files.copy(bis, Paths.get(randFile), StandardCopyOption.REPLACE_EXISTING);
        }catch(Exception e){
            throw new StorageException("Failed to write file from " + srcFile, e);
        }
        try{
            Thread.sleep(waitSecs * 1000);
        }catch(InterruptedException e){
            logger.error(e.getMessage());
        }
        File f = new File(randFile);
        if (!f.exists()){
            deleted = true;
        }
        return deleted;
    }

    public static boolean isDeleted(String writeDir, MultipartFile file, int waitSecs){
        boolean deleted = false;
        String ext = getExt(file.getOriginalFilename());
        String randFile = writeDir + UUID.randomUUID().toString() + "." + ext;
        try{
            InputStream is = file.getInputStream();
            Files.copy(is, Paths.get(randFile), StandardCopyOption.REPLACE_EXISTING);
        }catch(Exception e){
            throw new StorageException("Failed to write file from " + file.getOriginalFilename(), e);
        }
        try{
            Thread.sleep(waitSecs * 1000);
        }catch(InterruptedException e){
            logger.error(e.getMessage());
        }
        File f = new File(randFile);
        if (!f.exists()){
            deleted = true;
        }
        return deleted;
    }

    public String store(MultipartFile file, Long fileId){
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try{
            if (file.isEmpty()){
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")){
                // This is a security check
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);
            }
            int lastIndexOfDot = filename.lastIndexOf('.');
            String fileExtension = null;
            if (lastIndexOfDot > 0){
                fileExtension = filename.substring(lastIndexOfDot + 1);
            }
            filename = UUID.randomUUID().toString() + "." + fileExtension;
            Files.createDirectories(rootLocation);
            try(InputStream inputStream = file.getInputStream()){
                Path target = this.rootLocation.resolve(filename);
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
                //delete old image
                if (fileId > 0){
                    Optional<FileResource> fr = fileResourceRepository.findById(fileId);
                    if (fr.isPresent()){
                        Path oldPath = Paths.get(fr.get().getLocalPath());
                        Files.deleteIfExists(oldPath);
                    }
                }
            }
        }catch(IOException e){
            throw new StorageException("Failed to store file " + filename, e);
        }
        return filename;
    }

    public Stream<Path> loadAll(){
        try{
            return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation)).map(this.rootLocation::relativize);
        }catch(IOException e){
            throw new StorageException("Failed to read stored files", e);
        }
    }

    public Path load(String filename){
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename){
        try{
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        }catch(MalformedURLException e){
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(FileDeleteVo request) throws IOException{
        Set<FileResource> files = fileResourceRepository.findByIdIn(request.getFileIds());
        if(request.getFileIds().size() != files.size()) {
            throw new AppException("欲刪除圖片有問題");
        }
        if(request.getCheckMapping() == true) {
            List<DeviceImage> oriMapping = deviceImageRepository.findByFileIdIn(request.getFileIds());
            deviceImageRepository.deleteAll(oriMapping);
        }
        for(FileResource file : files) {
            Path oldPath = Paths.get(file.getLocalPath());
            //把 images 換成 rootLocation
            Path target = this.rootLocation.resolve(oldPath.getFileName());
            Files.deleteIfExists(target);
        }
        fileResourceRepository.deleteAll(files);
        logger.debug(this.rootLocation.toString());
    }

    public void init(){
        try{
            Files.createDirectories(rootLocation);
        }catch(IOException e){
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
