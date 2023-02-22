package com.surgical.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.DeliverBatch;

@Repository
public interface DeliverBatchRepository extends JpaRepository<DeliverBatch, Long>{

    public Page<DeliverBatch> findByDeliverToAndStatus(Long stationId, Integer status, Pageable pageable);

    @Query(value = "SELECT DISTINCT db FROM DeliverBatchDetail dbd INNER JOIN DeliverBatch db ON db.id = dbd.deliverBatchId " 
    + "WHERE dbd.trackingId IN :trackingIds AND db.status = 1 ORDER BY db.createTime ASC ")
    public Page<DeliverBatch> getUnsterilizedDeliverBatch(@Param(value = "trackingIds") List<Long> trackingIds, Pageable pageable);
}
