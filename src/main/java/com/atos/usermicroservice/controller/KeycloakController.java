package com.atos.usermicroservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/demo")
public class KeycloakController {

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('Student')")
    public String hello(){
        return "Hello from spring boot & Keyclock";
    }

    @GetMapping("/user/hello-2")
    @PreAuthorize("hasRole('Teacher')")
    public String hello2(){
        return "Hello from spring boot & Keyclock - ADMIN";
    }

    @GetMapping("/home")
    public String signup(){
        return "Let's create new user";
    }
}