package com.surgical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.surgical.entities.PackageConfigDetail;

@Repository
public interface PackageConfigDetailRepository extends JpaRepository<PackageConfigDetail, Long>, JpaSpecificationExecutor<PackageConfigDetail>{
  
    public List<PackageConfigDetail> findByPackageConfigId(Long configId);
}
