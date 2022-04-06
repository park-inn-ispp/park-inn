package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

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

}
