package com.surgical.repositories;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.surgical.entities.Station;

@Repository
public interface StationRepository extends JpaRepository<Station, Long>{

    List<Station> findAll(Specification<Station> specification);

    public Optional<Station> findByIdAndType(Long id, String type);
}
