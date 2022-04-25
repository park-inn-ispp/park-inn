package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.parkinn.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
    Optional<Client> findByNameOrEmail(String name, String email);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
}
