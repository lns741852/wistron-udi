package com.surgical.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.surgical.entities.DeviceModel;

@Repository
public interface DeviceModelRepository extends JpaRepository<DeviceModel, Long>{
    
    public Optional<DeviceModel> findByBrandAndManufactureId(String brand, String manufactureId);
    
}
