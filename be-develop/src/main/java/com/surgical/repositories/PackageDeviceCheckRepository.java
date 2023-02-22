package com.surgical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.surgical.entities.PackageDeviceCheck;

@Repository
public interface PackageDeviceCheckRepository extends JpaRepository<PackageDeviceCheck, Long>{

    List<PackageDeviceCheck> findByTrackingId(Long trackingId);
}
