package com.surgical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.SterilizedBatchDetail;
import com.surgical.vo.SterilizedBatchPackageResponse;

@Repository
public interface SterilizedBatchDetailRepository extends JpaRepository<SterilizedBatchDetail, Long>{
    Long countBySterilizedBatchId(Long sterilizedBatchId);
    
    SterilizedBatchDetail findFirstByTrackingIdOrderByCreateTimeDesc(Long trackingId);

    @Query(value = "SELECT new com.surgical.vo.SterilizedBatchPackageResponse(sbd.trackingId, sbd.packageId, p.serialNo, pc.configCode, pc.packageName, pc.divisionId, t.expireTime, t.useTime) "
        + "FROM SterilizedBatchDetail sbd "
        + "INNER JOIN Tracking t ON t.id = sbd.trackingId "
        + "INNER JOIN Package p ON p.id = sbd.packageId "
        + "INNER JOIN PackageConfig pc ON p.packageConfigId = pc.id "
        + "WHERE sbd.sterilizedBatchId = :id")
    List<SterilizedBatchPackageResponse> findForSterilizedBatchPackageList(@Param(value = "id") Long id);
    
    List<SterilizedBatchDetail> findAllByTrackingId(Long trackingId); 
}
