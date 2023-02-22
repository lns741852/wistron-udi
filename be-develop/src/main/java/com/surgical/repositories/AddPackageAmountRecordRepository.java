package com.surgical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.surgical.entities.AddPackageAmountRecord;

@Repository
public interface AddPackageAmountRecordRepository extends JpaRepository<AddPackageAmountRecord, Long>, JpaSpecificationExecutor<AddPackageAmountRecord>{
}
