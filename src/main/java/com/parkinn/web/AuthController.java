package com.parkinn.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getnameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<>();
        Client userlogged = clientRepository.findByNameOrEmail(loginDto.getnameOrEmail(), loginDto.getnameOrEmail()).get();

        if(userlogged == null){
            errores.add("Este usuario no está registrado. Primero debes registrarte");
            response.put("errores", errores);
            return ResponseEntity.badRequest().body(response);
        }

        userlogged.setLoggedIn(true);
        clientRepository.save(userlogged);
        return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<>();

        // add check for email exists in DB
        if(clientRepository.existsByEmail(signUpDto.getEmail())){
            errores.add("Este email ya está registrado");
            response.put("errores", errores);
            return ResponseEntity.badRequest().body(response);
        }

        if(!signUpDto.getPassword().equals(signUpDto.getConfirm())){
            errores.add("Las contraseñas no coinciden");
            response.put("errores", errores);
            return ResponseEntity.badRequest().body(response);
        }

        if(clientRepository.existsByPhone(signUpDto.getPhone()) == true){
            errores.add("Este número de teléfono ya está registrado");
            response.put("errores", errores);
            return ResponseEntity.badRequest().body(response);
        }

        if(signUpDto.getPhone().length() != 9){
            errores.add("El número de teléfono ha de tener 9 digitos");
            response.put("errores", errores);
            return ResponseEntity.badRequest().body(response);
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
