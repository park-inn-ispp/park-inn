package com.parkinn.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import com.parkinn.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

    public Optional<Client> findByEmail(@Param("email")String email) throws DataAccessException;

}
