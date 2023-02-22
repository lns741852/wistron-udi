package com.surgical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.TrackingRecord;
import com.surgical.vo.TrackingRecordInfo;

@Repository
public interface TrackingRecordRepository extends JpaRepository<TrackingRecord, Long>{
    
    @Query("SELECT NEW com.surgical.vo.TrackingRecordInfo(tr.id, tr.status, tr.createTime, tr.creator, a.name) "
        + "FROM TrackingRecord tr LEFT JOIN Admin a ON a.id = tr.creator "
        + "WHERE tr.trackingId = :trackingId ")
    public List<TrackingRecordInfo> queryTrackingRecordInfo(@Param("trackingId") Long trackingId);
}
