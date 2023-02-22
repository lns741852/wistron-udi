package com.surgical.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.SterilizedBatch;
import com.surgical.vo.SterilizerVo;

@Repository
public interface SterilizedBatchRepository extends JpaRepository<SterilizedBatch, Long>,JpaSpecificationExecutor<SterilizedBatch>{
    
    @Query(value = "SELECT DISTINCT sb FROM SterilizedBatchDetail sbd INNER JOIN SterilizedBatch sb ON sbd.sterilizedBatchId = sb.id "
        + "WHERE sbd.trackingId IN :trackingIds AND sb.status = 1 ORDER BY sb.createTime ASC ")
    public Page<SterilizedBatch> getUnsentSterilizedBatch(@Param(value = "trackingIds") List<Long> trackingIds, Pageable pageable);
    
    @Query("SELECT DISTINCT sb.sterilizer FROM SterilizedBatch sb")
    List<String> findDistinctSterilizer();
    
    @Query("SELECT NEW com.surgical.vo.SterilizerVo( sb.sterilizer, COUNT(sbd.packageId) AS sterilizedPackages, COUNT(DISTINCT sbd.sterilizedBatchId) AS sterilizedCount ) "
        + "FROM SterilizedBatch sb "
        + "LEFT JOIN SterilizedBatchDetail sbd ON sbd.sterilizedBatchId = sb.id "
        + "WHERE sb.finishTime IS NOT NULL "
        + "AND ( sb.finishTime BETWEEN :start AND :end ) "
        + "GROUP BY sb.sterilizer "
        + "ORDER BY sb.sterilizer ASC ")
    List<SterilizerVo> getSterilizersStatistics(@Param(value = "start") Date start, @Param(value = "end") Date end);
    
    @Query("SELECT COUNT(*) "
        + "FROM SterilizedBatch sb "
        + "LEFT JOIN SterilizedBatchDetail sbd ON sbd.sterilizedBatchId = sb.id "
        + "WHERE sb.status = 0 "
        + "AND sb.finishTime IS NOT NULL "
        + "AND ( sb.finishTime BETWEEN :start AND :end ) ")
    Long getFailedSterilizedCount(@Param(value = "start") Date start, @Param(value = "end") Date end);
}
