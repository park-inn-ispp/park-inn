package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import com.parkinn.model.Client;
import com.parkinn.model.Status;
import com.parkinn.repository.ClientRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin(origins = "*",methods = {RequestMethod.GET,RequestMethod.PUT,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.HEAD}, maxAge = 1800)
@RestController
@RequestMapping("/clients")
public class ClientsController {

    private final ClientRepository clientRepository;

    public ClientsController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/{id}")
    public Client getClient(@PathVariable Long id) {
        return clientRepository.findById(id).orElseThrow(RuntimeException::new);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity deleteClient(@PathVariable Long id) {
        clientRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login")
    public Status loginClient(@RequestBody Client client) {
        List<Client> clients = clientRepository.findAll();
        for (Client other : clients) {
            if (other.equals(client)) {
                if(other.isLoggedIn()){
                    return Status.USER_ALREADY_EXISTS;

                }else{
                    other.setLoggedIn(true);
                    clientRepository.save(other);
                    return Status.SUCCESS;
                }
               
            }
        }
        return Status.FAILURE;
    }
    @PostMapping("/logout")
    public Status logUserOut( @RequestBody Client client) {
        List<Client> clients = clientRepository.findAll();
        for (Client other : clients) {
            if (Objects.equals(other.getEmail(), client.getEmail())) {
                if(!other.isLoggedIn()){
                    return Status.USER_NOT_LOGGED;

                }else{
                    other.setLoggedIn(false);
                    clientRepository.save(other);
                    return Status.SUCCESS;
                }
               
            }
        }
        return Status.FAILURE;
    }
}