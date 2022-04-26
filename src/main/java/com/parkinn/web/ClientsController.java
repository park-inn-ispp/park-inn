package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Optional;
import java.util.Set;

import java.util.Map;


import com.parkinn.model.Client;
import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.model.Role;
import com.parkinn.repository.ClientRepository;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.PlazaRepository;
import com.parkinn.repository.ReservaRepository;
import com.parkinn.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final ClientRepository clientRepository;
    
    private final PlazaRepository plazaRepository;
    
    private final RoleRepository roleRepository;
    
    private final HorarioRepository horarioRepository;
    
    private final ReservaRepository reservasRepository;

    public ClientsController(ClientRepository clientRepository, PlazaRepository plazaRepository, RoleRepository roleRepository, HorarioRepository horarioRepository, ReservaRepository reservasRepository) {
        this.clientRepository = clientRepository;
        this.plazaRepository = plazaRepository;
        this.roleRepository = roleRepository;
        this.horarioRepository = horarioRepository;
        this.reservasRepository = reservasRepository;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping
    public ResponseEntity getClients() {
    Map<String,Object> response = new HashMap<>();
    List<String> errores = new ArrayList<String>();
    List<Client> clients = clientRepository.findAll();
    if(clients.isEmpty()){
        errores.add("No existen usuarios en la base de datos");
        response.put("errores", errores);
	    return ResponseEntity.badRequest().body(response);

    }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
        return ResponseEntity.ok(clients);
    }else{
        errores.add("No tienes acceso");
        response.put("errores", errores);
        return ResponseEntity.badRequest().body(response);
    }
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity getClient(@PathVariable Long id) {

    Map<String,Object> response = new HashMap<>();
    List<String> errores = new ArrayList<String>();
    Optional<Client> cliente = clientRepository.findById(id);
    if(!cliente.isPresent()){
        errores.add("Este usuario no existe");
        response.put("errores", errores);
	    return ResponseEntity.badRequest().body(response);

    }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || cliente.get().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
        Client currentClient = clientRepository.findById(id).orElseThrow(RuntimeException::new);
        return ResponseEntity.ok(currentClient);
    }else{
        errores.add("Solo puedes acceder a tus datos");
        response.put("errores", errores);
        return ResponseEntity.badRequest().body(response);
    }
}

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/usuariopormail/{email}")
    public ResponseEntity getByEmail(@PathVariable String email) {
    Map<String,Object> response = new HashMap<>();
    List<String> errores = new ArrayList<String>();
    Optional<Client> cliente = clientRepository.findByEmail(email);
    if(!cliente.isPresent()){
        errores.add("Este usuario no existe");
        response.put("errores", errores);
	    return ResponseEntity.badRequest().body(response);

    }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || cliente.get().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
        return ResponseEntity.ok(cliente);
    }else{
        errores.add("No tienes acceso");
        response.put("errores", errores);
        return ResponseEntity.badRequest().body(response);
    }
    }


    @SuppressWarnings("rawtypes")
	@PostMapping
    public ResponseEntity createClient(@RequestBody Client client) throws URISyntaxException {
        Client savedClient = clientRepository.save(client);
        return ResponseEntity.created(new URI("/clients/" + savedClient.getId())).body(savedClient);
    }


    

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PutMapping("/{id}/edit")
    @SuppressWarnings("rawtypes")
    public ResponseEntity updateClient(@PathVariable Long id, @RequestBody Client client) {
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<String>();
        Optional<Client> cliente = clientRepository.findById(id);
        if(!cliente.isPresent()){
            errores.add("Este usuario no existe");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || cliente.get().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            Client currentClient = clientRepository.findById(id).orElseThrow(RuntimeException::new);
            currentClient.setName(client.getName());
            currentClient.setEmail(client.getEmail());
            currentClient.setPhone(client.getPhone());
            currentClient.setSurname(client.getSurname());
            currentClient.setPassword(passwordEncoder.encode(client.getPassword()));
            currentClient = clientRepository.save(currentClient);
            return ResponseEntity.ok(currentClient);
        }else{
            errores.add("Solo puedes editar los datos de tu perfil");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);

        }
        
    }


@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}/perfil")
    public Object consultarPerfil(@PathVariable Long id) {
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<String>();
        Optional<Client> cliente = clientRepository.findById(id);
        if(!cliente.isPresent()){
            errores.add("Este usuario no existe");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || cliente.get().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            return ResponseEntity.ok(cliente);
        }else{
            errores.add("No tienes acceso a este perfil");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }
        }


    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/banear")
    public ResponseEntity banClient(@PathVariable Long id) {
    	Optional<Client> optionalClient = clientRepository.findById(id);
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<>();
       
        if(optionalClient.isPresent()){
            Client currentClient= optionalClient.get();
            Set<Role> currentRole = currentClient.getRoles();
            Role roleUser = roleRepository.findByName("ROLE_USER").get();
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").get();
            Role roleBanned = roleRepository.findByName("ROLE_BANNED").get();

            if(currentRole.contains(roleAdmin)){
                errores.add("No puedes banear a un usuario que tiene permisos de administrador");
                response.put("errores",errores);
                return ResponseEntity.badRequest().body(response);
    
            } else if(!currentRole.contains(roleUser)){
               
                errores.add("No puedes banear a un usuario que ya estaba previamente baneado");
                response.put("errores",errores);
                return ResponseEntity.badRequest().body(response);
    
             }else{
                currentRole.clear();
                currentRole.add(roleBanned);

                currentClient.setRoles(currentRole);
                currentClient = clientRepository.save(currentClient);
                return ResponseEntity.ok(currentClient);
            }

        }else{
            
		   
            errores.add("No puedes banear a un usuario que no existe");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);

        }
   
    	
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/desbanear")
    public ResponseEntity unbanClient(@PathVariable Long id) {
    

        Optional<Client> optionalClient = clientRepository.findById(id);
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<>();
        if(optionalClient.isPresent()){
            Client currentClient= optionalClient.get();
            Role roleUser = roleRepository.findByName("ROLE_USER").get();
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").get();
            Set<Role> currentRole = currentClient.getRoles();

            if(currentRole.contains(roleAdmin)){
                errores.add("No puedes desbanear a un usuario que tiene permisos de administrador");
                response.put("errores",errores);
                return ResponseEntity.badRequest().body(response);
    
            }else if(currentRole.contains(roleUser)){
              
                errores.add("No puedes desbanear a un usuario que no estaba previamente baneado");
                response.put("errores",errores);
                return ResponseEntity.badRequest().body(response);
            }else{
                currentRole.clear(); //Borramos los roles que tenga y le ponemos el ROLE_USER
                currentRole.add(roleUser);
                currentClient.setRoles(currentRole);
                currentClient = clientRepository.save(currentClient);
                return ResponseEntity.ok(currentClient);
            }

        }else{
            errores.add("No puedes desbanear a un usuario que no existe");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
        }
    }
    


   
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteClient(@PathVariable Long id) {
        Optional<Client> optionalClient = clientRepository.findById(id);
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<>();

        if (optionalClient.isPresent()){
            Client usuario= optionalClient.get();
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").get();
            Set<Role> currentRole = usuario.getRoles();
            
            if(currentRole.contains(roleAdmin)){
                errores.add("No puedes eliminar a un usuario que tiene permisos de administrador");
                response.put("errores",errores);
                return ResponseEntity.badRequest().body(response);
    
            }else{
                List<Plaza> plazas = plazaRepository.findByUserId(id);
            
                for(int i = 0; i<plazas.size(); i++) {
                        Plaza plaza = plazas.get(i);
                        
                        List<Reserva> reservas = reservasRepository.findByPlazaId(plaza.getId());
                        
                        for(int i1 = 0; i1<reservas.size(); i1++) {
                            Reserva reserva = reservas.get(i1);
                            reserva.setPlaza(null);
                        }
                                    
                        List<Horario> horarios = horarioRepository.findHorariosByPlazaId(plaza.getId());
                        
                        for(int i2 = 0; i2<horarios.size(); i2++) {
                            Horario horario = horarios.get(i2);
                            horarioRepository.delete(horario);
                        }
                        
                        plazaRepository.delete(plaza);
                    }
                clientRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
           
        }else{
           
            errores.add("No puedes eliminar a un usuario que no existe");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
        }
    }

    
     
    
    
}

