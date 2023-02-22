package com.surgical.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.DeviceCheckDiff;

@Repository
public interface DeviceCheckDiffRepository extends JpaRepository<DeviceCheckDiff, Long>{

    @Query(value = "SELECT pdc.step AS step, dcd.deviceTypeId AS deviceTypeId, dcd.amount AS amount FROM PackageDeviceCheck pdc INNER JOIN DeviceCheckDiff dcd ON dcd.packageDeviceCheckId = pdc.id "
    + "WHERE pdc.trackingId = :trackingId ")
    public List<Map<String, Object>> getAllDeviceCheckDiffsByTrackingIg(@Param(value = "trackingId") Long trackingId);
}
