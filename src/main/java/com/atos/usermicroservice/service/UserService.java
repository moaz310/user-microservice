package com.atos.usermicroservice.service;

import com.atos.usermicroservice.Entity.User;
import com.atos.usermicroservice.Entity.UserMapper;
import com.atos.usermicroservice.configurations.KeycloakConfiguration;
import com.atos.usermicroservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.models.TokenManager;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.RefreshToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.Response;
import java.util.Collections;


@Slf4j
@Service
public class UserService {

    @Autowired
    KeycloakConfiguration keycloakConfig;

    @Autowired
    UserRepository userRepository;
    public AccessTokenResponse login(String userName, String password){
        Keycloak userToken;
        try {
            userToken = getUser(userName, password);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userToken.tokenManager().getAccessToken();
    }

    public void signUp(User user){
        UserRepresentation userKc = UserMapper.userToKeycloak(user);
        Keycloak admin = this.getUser(this.keycloakConfig.getAdminName(), this.keycloakConfig.getAdminPassword());
        RealmResource realmResource = admin.realm(this.keycloakConfig.getRealm());
        UsersResource usersResource = realmResource.users();

        try {
            Response response = usersResource.create(userKc);
            String userId = CreatedResponseUtil.getCreatedId(response);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(user.getPassword());

            UserResource userResource = usersResource.get(userId);
            userResource.resetPassword(passwordCred);

            RoleRepresentation testerRealmRole = realmResource.roles()
                    .get(user.getRole()).toRepresentation();

            userResource.roles().realmLevel()
                    .add(Collections.singletonList(testerRealmRole));

            ClientRepresentation appClient = realmResource.clients()
                    .findByClientId(this.keycloakConfig.getClientId()).get(0);

            RoleRepresentation userClientRole = realmResource.clients().get(appClient.getId())
                    .roles().get(user.getRole()).toRepresentation();

            userResource.roles()
                    .clientLevel(appClient.getId()).add(Collections.singletonList(userClientRole));
            admin.close();

            log.info(user.getEmail());
            this.userRepository.save(user);
        }catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void logout(String userRefreshToken){
        try {
            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("client_id", this.keycloakConfig.getClientId());
            requestParams.add("client_secret", this.keycloakConfig.getClientSecrets());
            requestParams.add("refresh_token", userRefreshToken);

            logoutUserSession(requestParams);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT);
        }
    }

    private void logoutUserSession(MultiValueMap<String, String> requestParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);
        String url = "http://localhost:8080/realms/ExamSystem/protocol/openid-connect/logout";

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.postForEntity(url, request, Object.class);
    }

    public Keycloak getUser(String userName, String password){
        return KeycloakBuilder.builder()
                .serverUrl(this.keycloakConfig.getUrl())
                .realm(this.keycloakConfig.getRealm())
                .clientId(this.keycloakConfig.getClientId())
                .grantType(OAuth2Constants.PASSWORD)
                .clientSecret(this.keycloakConfig.getClientSecrets())
                .username(userName)
                .password(password)
                .scope("openid")
                .build();
    }
}
