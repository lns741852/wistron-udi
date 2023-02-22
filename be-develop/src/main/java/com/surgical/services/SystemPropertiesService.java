package com.surgical.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgical.entities.SystemProperties;
import com.surgical.exception.AppException;
import com.surgical.repositories.SystemPropertiesRepository;
import com.surgical.vo.SystemPropertiesRequestVo;
import com.surgical.vo.SystemPropertiesVo;

@Service
public class SystemPropertiesService{
    
    @Autowired
    private SystemPropertiesRepository systemPropertiesRepository;

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Long> create(Long accountId, SystemPropertiesRequestVo systemPropertiesRequest){
        if (systemPropertiesRepository.existsSystemPropertiesByName(systemPropertiesRequest.getName())){
            throw new AppException("系統參數名稱重複");
        }
        SystemProperties systemProperties = new SystemProperties();
        systemProperties.setName(systemPropertiesRequest.getName());
        systemProperties.setValue(systemPropertiesRequest.getValue());
        systemProperties.setDescription(systemPropertiesRequest.getDescription());
        systemProperties.setUpdater(accountId);
        systemProperties.setUpdateTime(new Date());
        systemPropertiesRepository.save(systemProperties);
        return Collections.singletonMap("id", systemProperties.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Long accountId, Long id, SystemPropertiesRequestVo systemPropertiesRequest){
        Optional<SystemProperties> systemPropertiesOpt = systemPropertiesRepository.findById(id);
        SystemProperties systemProperties = systemPropertiesOpt.orElseThrow(() -> new AppException("更新失敗，系統參數不存在或已被刪除。"));
        if (!systemPropertiesRequest.getName().equals(systemProperties.getName())) {
            if (systemPropertiesRepository.existsSystemPropertiesByName(systemPropertiesRequest.getName())){
                throw new AppException("系統參數名稱重複");
            }
            systemProperties.setName(systemPropertiesRequest.getName());
        }
        systemProperties.setValue(systemPropertiesRequest.getValue());
        systemProperties.setDescription(systemPropertiesRequest.getDescription());
        systemProperties.setUpdater(accountId);
        systemProperties.setUpdateTime(new Date());
        systemPropertiesRepository.save(systemProperties);
    }
    
    public List<SystemPropertiesVo> list(String search){
        List<SystemPropertiesVo> resultList = systemPropertiesRepository.findBySearch(search);
        return resultList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id){
        Optional<SystemProperties> systemPropertiesOpt = systemPropertiesRepository.findById(id);
        SystemProperties systemProperties = systemPropertiesOpt.orElseThrow(() -> new AppException("刪除失敗，系統參數不存在或已被刪除。"));
        systemPropertiesRepository.delete(systemProperties);
    }
}
