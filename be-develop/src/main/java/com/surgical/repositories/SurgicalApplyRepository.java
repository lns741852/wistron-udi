package com.surgical.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.surgical.entities.SurgicalApply;

@Repository
public interface SurgicalApplyRepository extends JpaRepository<SurgicalApply, Long> ,JpaSpecificationExecutor<SurgicalApply>{
    
    Page<SurgicalApply> findAllByOrderByOperatingDate (Pageable page);
    
    Page<SurgicalApply>findByStatusInOrderByOperatingDate(List<Integer> status, Pageable page);

}
