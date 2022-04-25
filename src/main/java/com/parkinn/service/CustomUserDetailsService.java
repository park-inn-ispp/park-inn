package com.parkinn.service;

import com.parkinn.model.Client;
import com.parkinn.model.Role;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService  {

    @Autowired
    private ClientService clientService;

    @Override
    public UserDetails loadUserByUsername(String nameOrEmail) throws UsernameNotFoundException {
        Client user = clientService.findByNameOrEmail(nameOrEmail, nameOrEmail);
        if (user==null){
            throw(new UsernameNotFoundException("User not found with username or email:" + nameOrEmail));
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
            user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
    
}
