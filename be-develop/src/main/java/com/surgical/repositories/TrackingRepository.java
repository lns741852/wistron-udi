package com.surgical.repositories;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.Tracking;
import com.surgical.enums.PackageStatus;
import com.surgical.vo.DeviceDetailUseRecord;
import com.surgical.vo.PackageExpiredListResponse;
import com.surgical.vo.PackageTrackingResponse;
import com.surgical.vo.ReportPackageHistoryListResponse;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long>{
    
    @Query("SELECT p.packageConfigId AS configId, COUNT(*) AS packingCount FROM Tracking t INNER JOIN Package p ON t.packageId = p.id "
        + "WHERE t.createTime >= :start AND t.createTime <= :end GROUP BY p.packageConfigId ORDER BY p.packageConfigId")
    public List<Map<String, Long>> countForReportPackagePacking(@Param("start") Date start, @Param("end") Date end);

    @Query("SELECT NEW com.surgical.vo.ReportPackageHistoryListResponse (t.id AS trackingId, p.id AS packageId, p.serialNo AS serialNo, pc.configCode AS configCode, pc.packageName AS configName, "
        + "pc.divisionId AS divisionId, sb.finishTime AS sterilizationDate, sb.sterilizer AS sterilizer, t.expireTime AS expireDate, sa.medicalRecordNumber AS medicalRecordNo, t.useTime AS usedDate ) "
        + "FROM Tracking t "
        + "LEFT JOIN Package p ON p.id = t.packageId "
        + "LEFT JOIN PackageConfig pc ON pc.id = p.packageConfigId "
        + "LEFT JOIN SterilizedBatchDetail sbd ON sbd.trackingId = t.id "
        + "INNER JOIN SterilizedBatch sb ON sbd.sterilizedBatchId = sb.id AND sb.status = '1' "
        + "LEFT JOIN PackageOrderMapping pom ON pom.trackingId = t.id "
        + "LEFT JOIN PackageOrder po ON pom.packageOrderId = po.id "
        + "LEFT JOIN SurgicalApply sa ON po.surgicalApplyId = sa.id "
        + "WHERE t.finishTime IS NOT NULL "
        + "AND t.useTime BETWEEN COALESCE(:start, t.useTime) AND COALESCE(:end, t.useTime) "
        + "AND pc.configCode LIKE CONCAT('%', COALESCE(:configCode, ''), '%') "
        + "AND p.serialNo LIKE CONCAT('%', COALESCE(:serialNo, ''), '%') "
        + "AND sb.sterilizer = COALESCE(:sterilizer, sb.sterilizer) "
        + "AND sa.medicalRecordNumber LIKE CONCAT('%', COALESCE(:medicalRecordNo, ''), '%') "
        + "AND sb.finishTime BETWEEN COALESCE(:sterilizationDateStart, sb.finishTime) AND COALESCE (:sterilizationDateEnd, sb.finishTime) "
        + "ORDER BY t.finishTime DESC ")
    public Page<ReportPackageHistoryListResponse> queryForPackageHistoryList(@Param("start") Date start, @Param("end") Date end, @Param("configCode") String configCode, @Param("serialNo") String serialNo, @Param("sterilizer") String sterilizer, @Param("medicalRecordNo") String medicalRecordNo, 
        @Param("sterilizationDateStart") Date sterilizationDateStart, @Param("sterilizationDateEnd") Date sterilizationDateEnd, Pageable pageable);

    @Query(value = "SELECT NEW com.surgical.vo.DeviceDetailUseRecord(sa.id, sa.medicalRecordNumber, sa.name, sa.operatingDate, sa.operatingNumber, sa.divisionId) "
        + "FROM PackageOrderMapping pom "
        + "INNER JOIN PackageOrder po ON pom.packageOrderId = po.id "
        + "INNER JOIN SurgicalApply sa ON sa.id = po.surgicalApplyId "
        + "WHERE pom.trackingId in :trackingIds "
        + "AND (sa.operatingDate BETWEEN :start AND :end) ORDER BY sa.operatingDate ASC")
    public List<DeviceDetailUseRecord> queryFromTrackingIdsForSurgeryInfo(@Param("trackingIds") List<Long> trackingids, @Param("start") Date start, @Param("end") Date end);
    
    
    @Query(" SELECT NEW com.surgical.vo.PackageExpiredListResponse(t.id, t.packageId, p.serialNo, pc.configCode, pc.packageName, p.position, pc.divisionId, sb.startTime, sb.sterilizer, t.expireTime) " 
        + " FROM Package p " 
        + " INNER JOIN Tracking t ON t.id = p.trackingId " 
        + " LEFT JOIN PackageConfig pc ON pc.id = p.packageConfigId " 
        + " LEFT JOIN SterilizedBatchDetail sbd ON sbd.trackingId = t.id " 
        + " LEFT JOIN SterilizedBatch sb ON sb.id = sbd.sterilizedBatchId " 
        + " WHERE p.status = :status " 
        + " AND sb.status = 1 "
        + " AND "
        + " ( "
        + "     (:start IS NOT NULL AND :end IS NOT NULL AND t.expireTime BETWEEN :start AND :end) "
        + "     OR "
        + "     (:start IS NULL AND :end IS NULL AND t.expireTime <= CURDATE()) "
        + " ) "
        + " ORDER BY t.expireTime ASC ")
    public Page<PackageExpiredListResponse> queryPackageExpiredList(@Param("status") Integer status, @Param("start") Date start, @Param("end") Date end, Pageable pageable);

    @Query("SELECT NEW com.surgical.vo.PackageTrackingResponse(p.id, p.serialNo, p.code, p.packageConfigId, pc.packageName, pc.configCode, pc.divisionId) "
        + "FROM Tracking t LEFT JOIN Package p ON p.id = t.packageId "
        + "LEFT JOIN PackageConfig pc ON pc.id = p.packageConfigId "
        + "WHERE t.id = :trackingId ")
    public PackageTrackingResponse queryForPackageTracking(@Param("trackingId") Long trackingId);
}
