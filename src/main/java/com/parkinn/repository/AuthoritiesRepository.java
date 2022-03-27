package com.parkinn.repository;

import org.springframework.data.repository.CrudRepository;

import com.parkinn.model.Authorities;

public interface AuthoritiesRepository extends  CrudRepository<Authorities, String>{
	
}
