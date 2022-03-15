package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.parkinn.model.Plaza;

public interface PlazaRepository extends JpaRepository<Plaza, Long> {

    public List<Plaza> findByPrecioHoraLessThanEqual(Double max);

}
