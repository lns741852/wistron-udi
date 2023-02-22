package com.surgical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.surgical.entities.PackageOrder;

@Repository
public interface PackageOrderRepository extends JpaRepository<PackageOrder, Long>, JpaSpecificationExecutor<PackageOrder>{

    public Optional<List<PackageOrder>> findBySurgicalApplyId(Long surgicalApplyId);

    public Optional<PackageOrder> findById(Long orderId);
}
