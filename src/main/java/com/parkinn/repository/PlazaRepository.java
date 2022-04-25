package com.parkinn.repository;

import org.springframework.dao.DataAccessException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import com.parkinn.model.Client;
import com.parkinn.model.Plaza;

public interface PlazaRepository extends JpaRepository<Plaza, Long> {

    @Query(value = "SELECT DISTINCT p.* FROM PLAZAS p LEFT JOIN HORARIOS h ON h.plaza_id = p.id WHERE (:max is null or precio_hora<=:max) and ((:inicio is null or fecha_fin > :inicio) and (:fin is null or fecha_inicio < :fin)) and (:zona is null or direccion LIKE %:zona%)",
        nativeQuery = true)
    public List<Plaza> filter(@Param("max") Double max, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, @Param("zona") String zona);

    @Query(value = "SELECT p FROM Park p WHERE p.direccion LIKE :direccion", nativeQuery=true)
   	public Plaza findByAddress(@Param("direccion")String direccion) throws DataAccessException;
    
    public List<Plaza> findByDireccionAndAdministrador(String direccion, Client administrador);
    
    @Query(value = "SELECT DISTINCT p.* FROM Plazas p WHERE p.user_id LIKE :usuario_id", nativeQuery=true)
    public List<Plaza> findByUserId(@Param("usuario_id") Long id) throws DataAccessException;

}

