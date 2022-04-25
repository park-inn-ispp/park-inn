package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.parkinn.model.Client;
import com.parkinn.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientService clientService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping
    public List<Client> getClients() {
        return clientService.findAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getClient(@PathVariable Long id) {
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<String>();
        Client c = clientService.findById(id);
        if(c==null){
            errores.add("Este usuario no existe");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else{
            return ResponseEntity.ok().body(c);
        } 
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/usuariopormail/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<String>();
        Client c = clientService.findByEmail(email);
        if(c==null){
            errores.add("Este usuario no existe");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else{
            return ResponseEntity.ok().body(c);
        }
        
    }

    @SuppressWarnings("rawtypes")
	@PostMapping
    public ResponseEntity createClient(@RequestBody Client client) throws URISyntaxException {
        Client savedClient = clientService.save(client);
        return ResponseEntity.created(new URI("/clients/" + savedClient.getId())).body(savedClient);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PutMapping("/{id}/edit")
    @SuppressWarnings("rawtypes")
    public ResponseEntity updateClient(@PathVariable Long id, @RequestBody Client client) {
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<String>();
        Client cliente = clientService.findById(id);
        if(cliente==null){
            errores.add("Este usuario no existe");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || cliente.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            Client currentClient = clientService.findById(id);
            currentClient.setName(client.getName());
            currentClient.setEmail(client.getEmail());
            currentClient.setPhone(client.getPhone());
            currentClient.setSurname(client.getSurname());
            currentClient.setPassword(passwordEncoder.encode(client.getPassword()));
            currentClient = clientService.save(currentClient);
            return ResponseEntity.ok(currentClient);
        }else{
            errores.add("Solo puedes editar los datos de tu perfil");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}/delete")
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteClient(@PathVariable Long id) {
        clientService.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado satisfactoriamente");
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}/perfil")
    public Object consultarPerfil(@PathVariable Long id) {
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<String>();
        Client cliente = clientService.findById(id);
        if(cliente==null){
            errores.add("Este usuario no existe");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || cliente.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            return ResponseEntity.ok(cliente);
        }else{
            errores.add("No tienes acceso a este perfil");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }
    }
}