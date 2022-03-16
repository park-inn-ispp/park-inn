package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkinn.model.Parking;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
