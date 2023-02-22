package com.surgical.repositories;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.PackageDeviceRecord;

@Repository
public interface PackageDeviceRecordRepository extends JpaRepository<PackageDeviceRecord, Long>{
    public Optional<PackageDeviceRecord> findByPackageIdAndDeviceId(Long packageId, Long deviceId);
    
    public Optional<PackageDeviceRecord> findByPackageIdAndDeviceIdAndDeviceOutTimeIsNull(Long packageId, Long deviceId);

    @Query(value = "SELECT t.id AS trackingId, t.createTime AS trackingCreateTime, pdr.deviceInTime AS in, pdr.deviceOutTime AS out FROM PackageDeviceRecord pdr "
        + "INNER JOIN Tracking t ON t.packageId = pdr.packageId "
        + "WHERE pdr.deviceId = :deviceId AND NOT ((pdr.deviceOutTime IS NOT NULL AND pdr.deviceOutTime < :start) OR pdr.deviceInTime > :end) ")
    public List<Map<String, Object>> queryTrackingInfos(@Param("deviceId") Long deviceId, @Param("start") Date start, @Param("end") Date end);
    
    @Query(value = "SELECT pdr FROM PackageDeviceRecord pdr "
                + "WHERE pdr.packageId = :packageId "
                + "AND :createTime >= pdr.deviceInTime AND :createTime < COALESCE(pdr.deviceOutTime, NOW()) ")
    public Optional<List<PackageDeviceRecord>> queryPackageDeviceRecords(@Param("packageId") Long packageId, @Param("createTime") Date createTime);
}
