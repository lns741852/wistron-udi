package com.surgical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.surgical.entities.PackageOrderMapping;

@Repository
public interface PackageOrderMappingRepository extends JpaRepository<PackageOrderMapping, Long>{

    public List<PackageOrderMapping> findByPackageOrderId(Long packageOrderId);

    public PackageOrderMapping findByPackageIdAndTrackingId(Long packageId, Long trackingId);
    
    public Optional<PackageOrderMapping> findByTrackingId(Long trackingId);
    
    public List<PackageOrderMapping> findByPackageOrderIdInAndStatus(List<Long> packageOrderIds, Integer status);
}
