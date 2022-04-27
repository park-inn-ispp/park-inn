package com.parkinn.service;

import com.parkinn.repository.ClientRepository;

import java.util.List;

import com.parkinn.model.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

	@Autowired
    private ClientRepository repository;
    
    
	public List<Client> findAll(){
        return repository.findAll();
    }

	public Client findById(Long id){ 
        return repository.findById(id).orElse(null);
    }

    public Client findByEmail(String email){ 
        return repository.findByEmail(email).orElse(null);
    }

    public Client findByNameOrEmail(String name, String email){ 
        return repository.findByNameOrEmail(name,email).orElse(null);
    }

    public Boolean existsByEmail(String email){ 
        return repository.existsByEmail(email);
    }

    public Boolean existsByPhone(String email){ 
        return repository.existsByPhone(email);
    }

    public Client save(Client client){ 
        return repository.save(client);
    }

    public void deleteById(Long id){ 
        repository.deleteById(id);
    }
    
}
