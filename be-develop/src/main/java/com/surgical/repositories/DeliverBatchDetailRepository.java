package com.surgical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.surgical.entities.DeliverBatchDetail;

@Repository
public interface DeliverBatchDetailRepository extends JpaRepository<DeliverBatchDetail, Long>{
    
    public List<DeliverBatchDetail> findByDeliverBatchId(Long deliverBatchId);
    
    public List<DeliverBatchDetail> findByDeliverBatchIdIn(List<Long> deliverBatchIds);
    
    public Long countByDeliverBatchId(Long deliverBatchId);
}
