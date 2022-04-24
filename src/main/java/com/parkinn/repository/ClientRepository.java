package com.parkinn.repository;

import org.springframework.dao.DataAccessException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


import com.parkinn.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {


    Optional<Client> findByEmail(String email);
    Optional<Client> findByNameOrEmail(String name, String email);
    Optional<Client> findByName(String name);
    Boolean existsByName(String name);
    Boolean existsByEmail(String email);
    Boolean existsBySurname(String surname);
    Boolean existsByPhone(String phone);

    @Query(value = "SELECT  p FROM Clients p WHERE p.user_email LIKE :usuario_email", nativeQuery=true)
    public Client Perfil(@Param("usuario_email") Long id) throws DataAccessException;

}
