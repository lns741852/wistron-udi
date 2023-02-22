package com.surgical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.surgical.entities.PackageOrderDetail;

@Repository
public interface PackageOrderDetailRepository extends JpaRepository<PackageOrderDetail, Long>{
    
    public List<PackageOrderDetail> findByPackageOrderId(Long orderId);
}
