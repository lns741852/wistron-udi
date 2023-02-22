package com.surgical.services;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surgical.entities.Station;
import com.surgical.enums.StationTypeEnum;
import com.surgical.repositories.StationRepository;
import com.surgical.vo.StationListVo;

@Service
public class StationService{
    @Autowired
    StationRepository stationRepository;
    
    public StationListVo getStationList(){
        List<Station> stations = stationRepository.findAll();
        StationListVo stationListVo = new StationListVo();
        for(Station station : stations){
            if (StationTypeEnum.SUPPLY.getValue().equals(station.getType())){
                stationListVo.setSupply(station.getId());
            }else if (StationTypeEnum.PACKING.getValue().equals(station.getType())) {
                stationListVo.setPacking(station.getId());
            }else if (StationTypeEnum.STERILIZATION.getValue().equals(station.getType())) {
                stationListVo.setSterilization(station.getId());
            }
        }
        return stationListVo;
    }
    
}
