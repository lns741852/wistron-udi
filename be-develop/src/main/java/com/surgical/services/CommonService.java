package com.surgical.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surgical.enums.Attribute;
import com.surgical.exception.AppException;
import com.surgical.repositories.PackageRepository;
import com.surgical.repositories.SterilizedBatchRepository;
import com.surgical.repositories.WashingBatchRepository;

@Service
public class CommonService{
    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private WashingBatchRepository washingBatchRepository;

    @Autowired
    private SterilizedBatchRepository sterilizedBatchRepository;

    public List<String> queryList(String attribute){
        Attribute attr = Attribute.convertByValue(attribute);
        if (attr == null){
            throw new AppException("查詢失敗，輸入屬性錯誤。");
        }
        switch(attr){
            case WASHING_MACHINE:
                return washingBatchRepository.findDistinctWashingMachine();
            case STERILIZER:
                return sterilizedBatchRepository.findDistinctSterilizer();
            case PACKAGE_POSITION:
                return packageRepository.findDistinctPosition();    
            default:
        }
        return new ArrayList<String>();
    }
}
