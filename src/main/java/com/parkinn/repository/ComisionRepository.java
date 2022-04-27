package com.parkinn.repository;


import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


import com.parkinn.model.Comision;

public interface ComisionRepository extends JpaRepository<Comision, Long> {

    
    Optional<Comision> findByPorcentaje(float porcentaje);
    boolean existsById(Long id);
    
    
}

