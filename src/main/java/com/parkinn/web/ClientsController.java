package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.parkinn.model.Client;
import com.parkinn.model.Reserva;
import com.parkinn.model.Role;
import com.parkinn.repository.ClientRepository;
import com.parkinn.repository.PlazaRepository;
import com.parkinn.repository.ReservaRepository;
import com.parkinn.repository.RoleRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin(origins = "*",methods = {RequestMethod.GET,RequestMethod.PUT,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.HEAD}, maxAge = 1800)
@RestController
@RequestMapping("/clients")
public class ClientsController {

    private final ClientRepository clientRepository;
    
    private final PlazaRepository plazaRepository;
    
    private final RoleRepository roleRepository;

    public ClientsController(ClientRepository clientRepository, PlazaRepository plazaRepository, RoleRepository roleRepository) {
        this.clientRepository = clientRepository;
        this.plazaRepository = plazaRepository;
        this.roleRepository = roleRepository;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

  //  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public Client getClient(@PathVariable Long id) {
        return clientRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/usuariopormail/{email}")
    public Client getByEmail(@PathVariable String email) {
        return clientRepository.findByEmail(email).orElseThrow(RuntimeException::new);
    }


    @PostMapping
    public ResponseEntity createClient(@RequestBody Client client) throws URISyntaxException {
        Client savedClient = clientRepository.save(client);
        return ResponseEntity.created(new URI("/clients/" + savedClient.getId())).body(savedClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateClient(@PathVariable Long id, @RequestBody Client client) {
        Client currentClient = clientRepository.findById(id).orElseThrow(RuntimeException::new);
        currentClient.setName(client.getName());
        currentClient.setEmail(client.getEmail());
        currentClient = clientRepository.save(client);

        return ResponseEntity.ok(currentClient);
    }


    
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/banear")
    public ResponseEntity banClient(@PathVariable Long id) {
    	Client currentClient = clientRepository.findById(id).orElseThrow(RuntimeException::new);
    	currentClient.setRoles(null);
        
        return ResponseEntity.ok(currentClient);
    }

    	
    /*   
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteClient(@PathVariable Long id) {
    	plazaService.findUserById(id);
    	
    	clientRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
 */ 
    
    
    
    
    
    
}