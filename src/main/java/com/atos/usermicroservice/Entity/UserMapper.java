package com.atos.usermicroservice.Entity;

import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;

public class UserMapper {

    public static User UserDtoToUser(UserDTO userDto){
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static UserRepresentation userToKeycloak(User user) {
        UserRepresentation userKc = new UserRepresentation();
        userKc.setEnabled(true);
        userKc.setUsername(user.getUserName());
        userKc.setFirstName(user.getFirstName());
        userKc.setLastName(user.getLastName());
        userKc.setEmail(user.getEmail());
        userKc.setAttributes(Collections.singletonMap("origin", List.of("demo")));
        return userKc;
    }
}
