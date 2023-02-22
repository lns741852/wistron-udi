package com.surgical.repositories;

import com.surgical.entities.FileResource;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileResourceRepository extends JpaRepository<FileResource, Long>{
    
    public Set<FileResource> findByIdIn(Set<Long> idList);
}
