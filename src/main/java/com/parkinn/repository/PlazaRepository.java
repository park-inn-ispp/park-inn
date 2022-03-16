package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import com.parkinn.model.Plaza;

public interface PlazaRepository extends JpaRepository<Plaza, Long> {

    @Query(value = "SELECT DISTINCT p.* FROM PLAZAS p LEFT JOIN HORARIOS h ON h.plaza_id = p.id WHERE (:max is null or precio_hora<=:max) and ((:inicio is null or fecha_fin > :inicio) and (:fin is null or fecha_inicio < :fin)) and (:zona is null or direccion LIKE %:zona%)",
        nativeQuery = true)
    public List<Plaza> filter(@Param("max") Double max, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, @Param("zona") String zona);
}
