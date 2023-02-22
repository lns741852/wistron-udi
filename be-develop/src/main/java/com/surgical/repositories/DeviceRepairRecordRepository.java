package com.surgical.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.DeviceRepairRecord;

@Repository
public interface DeviceRepairRecordRepository extends JpaRepository<DeviceRepairRecord, Long>{

    @Query(value = "SELECT SUM(drr.previousUsedCount) AS counter " 
                    + "FROM DeviceRepairRecord drr " 
                    + "WHERE drr.deviceId = :deviceId " 
                    + "AND drr.status != 9 ")
    public Optional<Long> findUsedCountBeforeRepair(@Param("deviceId") Long deviceId);

    public Optional<DeviceRepairRecord> findByDeviceIdAndStatusOrderByCreateTimeDesc(Long deviceId, Integer status);
}
