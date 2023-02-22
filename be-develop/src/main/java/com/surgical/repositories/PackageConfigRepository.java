package com.surgical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.surgical.entities.PackageConfig;

@Repository
public interface PackageConfigRepository extends JpaRepository<PackageConfig, Long>,JpaSpecificationExecutor<PackageConfig>{
    
    public Optional<PackageConfig> findByConfigCodeAndPackageNameAndDivisionId(String configCode, String configName, Long divisionId);
    
    public List<PackageConfig> findByIdIn(List<Long> configIds);
}
