package com.surgical.repositories;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.DeviceBoxRepair;

@Repository
public interface DeviceBoxRepairRepository extends JpaRepository<DeviceBoxRepair, Long>{
    
    @Query(value = "SELECT id AS id, status AS status, previousUsedCount AS preUsedCount, comments AS comments, createTime AS createTime, finishTime AS finishTime FROM DeviceBoxRepair " 
    + "WHERE deviceBoxId = :deviceBoxId ORDER BY createTime ASC ")
    public List<Map<String, Object>> findRepairInfoByDeviceBoxId(@Param("deviceBoxId") Long deviceBoxId);
    
    @Query(value = "SELECT CASE WHEN SUM(dbr.previousUsedCount) IS NULL THEN 0 ELSE SUM(dbr.previousUsedCount) END " + 
        "FROM DeviceBoxRepair dbr WHERE dbr.deviceBoxId = :deviceBoxId ")
    public Long getTotalPreviousUsedCount(@Param("deviceBoxId") Long deviceBoxId);
    
    public Optional<DeviceBoxRepair> findByDeviceBoxIdAndStatusAndFinishTimeIsNull(Long deviceBoxId, Integer status);
    
    public List<DeviceBoxRepair> findByDeviceBoxIdOrderByCreateTimeAsc(Long deviceBoxId);
}
