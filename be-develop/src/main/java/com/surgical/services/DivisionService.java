package com.surgical.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surgical.entities.Division;
import com.surgical.enums.ComponentStatusFlag;
import com.surgical.exception.AppException;
import com.surgical.repositories.DivisionRepository;
import com.surgical.vo.DivisionInfo;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Transactional
public class DivisionService{
    @Autowired
    DivisionRepository divisionRepository;
    
    public List<DivisionInfo> getList(Integer status){
        List<DivisionInfo> result = new ArrayList<DivisionInfo>();
        List<Division> divisions = (null == status)? divisionRepository.findAll() : divisionRepository.findByStatus(status);
        if (!divisions.isEmpty()){
            for(Division division : divisions){
                DivisionInfo divisionInfo = new DivisionInfo();
                divisionInfo.setId(division.getId());
                divisionInfo.setCode(division.getCode());
                divisionInfo.setName(division.getName());
                divisionInfo.setStatus(division.getStatus());
                divisionInfo.setCreateTime(division.getCreateTime());
                divisionInfo.setUpdateTime(division.getUpdateTime());
                result.add(divisionInfo);
            }
        }
        return result;
    }
    
    public void createDivison(DivisionInfo info, Long accountId) {
        Optional<Division> divisionOpt = divisionRepository.findByNameOrCode(info.getName(), info.getCode());
        Date now = new Date();
        if(divisionOpt.isPresent()) {
            throw new AppException("科別名稱或代號已存在");
        }
        Division division = new Division();
        division.setName(info.getName());
        division.setCode(info.getCode());
        division.setStatus(0);
        division.setCreateTime(now);
        division.setUpdateTime(now);
        division.setUpdater(accountId.intValue());
        divisionRepository.save(division);
    }
    
    public void updateStatus(DivisionInfo info, Long accountId) {
        Optional<Division> divisionOpt = divisionRepository.findById(info.getId());
        Division division = divisionOpt.orElseThrow(()->new AppException("查無此科別"));
        if(division.getStatus().equals(info.getStatus())) {
            log.error("division ID: "+info.getId()+" 更新狀態失敗, 舊狀態與新狀態一致");
            throw new AppException("更新狀態失敗, 舊狀態與新狀態一致");
        }
        division.setStatus(info.getStatus());
        division.setUpdateTime(new Date());
        division.setUpdater(accountId.intValue());
        divisionRepository.save(division);
    }
}
