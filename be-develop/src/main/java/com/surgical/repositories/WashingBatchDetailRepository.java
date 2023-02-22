package com.surgical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.surgical.entities.WashingBatchDetail;

@Repository
public interface WashingBatchDetailRepository extends JpaRepository<WashingBatchDetail, Long>{
}
