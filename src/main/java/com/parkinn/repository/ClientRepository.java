package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkinn.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
