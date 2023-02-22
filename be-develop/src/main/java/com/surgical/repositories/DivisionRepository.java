package com.surgical.repositories;

import com.surgical.entities.Division;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long>{
    
    public List<Division> findByStatus(Integer status);

    public Optional<Division> findByIdAndStatus(Long id, Integer status);
    
    public Optional<Division> findByNameOrCode(String name, String code);
}
