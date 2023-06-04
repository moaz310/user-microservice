package com.atos.usermicroservice.controller;

import com.atos.usermicroservice.Entity.User;
import com.atos.usermicroservice.Entity.UserDTO;
import com.atos.usermicroservice.Entity.UserMapper;
import com.atos.usermicroservice.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("${api.url.user}")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("${api.url.user.login}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccessTokenResponse login(@RequestBody UserDTO userdto){
        return  this.userService.login(userdto.getUserName(), userdto.getPassword());
    }

    @PostMapping("${api.url.user.signup}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void signUp (@Valid @RequestBody UserDTO userDto){
        User user = UserMapper.UserDtoToUser(userDto);
        this.userService.signUp(user);
    }

    @GetMapping("${api.url.user.logout}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void logout (@RequestBody AccessTokenResponse userToken){
        this.userService.logout(userToken.getRefreshToken());
    }
}
