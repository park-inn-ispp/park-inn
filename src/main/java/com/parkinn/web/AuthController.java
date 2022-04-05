package com.parkinn.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import java.util.Collections;

import com.parkinn.model.Client;
import com.parkinn.model.Role;
import com.parkinn.payload.LoginDto;
import com.parkinn.payload.SignUpDto;
import com.parkinn.repository.ClientRepository;
import com.parkinn.repository.RoleRepository;




@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getnameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);


        Client userlogged = clientRepository.findByNameOrEmail(loginDto.getnameOrEmail(), loginDto.getnameOrEmail()).get();
        userlogged.setLoggedIn(true);
        clientRepository.save(userlogged);
        return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){

        // add check for name exists in a DB
        if(clientRepository.existsByName(signUpDto.getName()) && clientRepository.existsBySurname(signUpDto.getSurname())){
            return new ResponseEntity<>("Ya hay una persona registrada con ese nombre y esos apellidos", HttpStatus.BAD_REQUEST);
        }

        // add check for email exists in DB
        if(clientRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("Este email ya está registrado", HttpStatus.BAD_REQUEST);
        }

        if(!signUpDto.getPassword().equals(signUpDto.getConfirm())){
            return new ResponseEntity<>("Las contraseñas no coinciden", HttpStatus.BAD_REQUEST); 
        }

        if(!signUpDto.getPhone().startsWith("6")){
            return new ResponseEntity<>("El número de teléfono ha de empezar por 6", HttpStatus.BAD_REQUEST); 
        }

        // create user object
        Client user = new Client();
        user.setName(signUpDto.getName());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setLoggedIn(true);
        user.setPhone(signUpDto.getPhone());
        user.setSurname(signUpDto.getSurname());

        Role roles = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singleton(roles));

        clientRepository.save(user);

        return new ResponseEntity<>("Usuario registrado correctamente", HttpStatus.OK);

    }


   
    
}
