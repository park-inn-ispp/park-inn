package com.parkinn.service;

import com.parkinn.repository.PlazaRepository;
import com.parkinn.model.Plaza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlazaService {
    
    @Autowired
    private PlazaRepository repository;

    public List<Plaza> filtrarPlazas(Double max, LocalDateTime inicio, LocalDateTime fin, String zona){
        List<Plaza> plazas = repository.filter(max, inicio, fin, zona);
        return plazas;
    }

    public List<Plaza> findAll(){
        return repository.findAll();
    }

    public Plaza guardarPlaza(Plaza plaza){
        Plaza savedPlaza = repository.save(plaza);
        return savedPlaza;
    }

    public Plaza findById(Long id){
        Plaza plaza = repository.findById(id).orElseThrow(RuntimeException::new);
        return plaza;
    }
    
    public List<Plaza> findUserById(Long id){
    	List<Plaza> plazas = repository.findByUserId(id);
        return plazas;
    }
    
    public void deleteById(Long id){
        repository.deleteById(id);
    }
    
 
    public List<Plaza> findJson(){
        List<Plaza> plazas= repository.query("select * from agencia ", new RowMapper<Plaza>() {

            public Plaza mapRow (ResultSet rs, int argl) throws SQLException{
                Plaza plaza = new Plaza (rs.getString("dirección"));
                return plaza;
            }

			
            });
        return plazas;

    }
   
    
    
    
    
    
    
}
