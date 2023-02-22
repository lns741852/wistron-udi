package com.surgical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.surgical.entities.SterilizedMonitor;

@Repository
public interface SterilizedMonitorRepository extends JpaRepository<SterilizedMonitor, Long>{
}
