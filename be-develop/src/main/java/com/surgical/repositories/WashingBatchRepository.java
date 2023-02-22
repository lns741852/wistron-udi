package com.surgical.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.WashingBatch;
import com.surgical.vo.WashingMachineVo;

@Repository
public interface WashingBatchRepository extends JpaRepository<WashingBatch, Long>, JpaSpecificationExecutor<WashingBatch>{
    @Query("SELECT DISTINCT wb.washingMachine FROM WashingBatch wb")
    List<String> findDistinctWashingMachine();
    
    @Query("SELECT NEW com.surgical.vo.WashingMachineVo( wb.washingMachine, COUNT(wbd.packageId) AS washedPackages, COUNT(DISTINCT wbd.washingBatchId) AS washedCount ) "
            + "FROM WashingBatch wb "
            + "LEFT JOIN WashingBatchDetail wbd ON wbd.washingBatchId = wb.id "
            + "WHERE wb.finishTime IS NOT NULL "
            + "AND ( wb.finishTime BETWEEN :start AND :end ) "
            + "GROUP BY wb.washingMachine "
            + "ORDER BY wb.washingMachine ASC ")
    List<WashingMachineVo> getWashingMachinesStatistics(@Param(value = "start") Date start, @Param(value = "end") Date end);
    
    @Query("SELECT COUNT(*) "
            + "FROM WashingBatch wb "
            + "LEFT JOIN WashingBatchDetail wbd ON wbd.washingBatchId = wb.id "
            + "WHERE wb.status = 0 "
            + "AND wb.finishTime IS NOT NULL "
            + "AND ( wb.finishTime BETWEEN :start AND :end ) ")
    Long getFailedWashedCount(@Param(value = "start") Date start, @Param(value = "end") Date end);
}
