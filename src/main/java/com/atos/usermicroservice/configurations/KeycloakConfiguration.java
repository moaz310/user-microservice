package com.atos.usermicroservice.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix= "keycloak.info")
public class KeycloakConfiguration {

    private String url;

    private String realm;

    private String clientId;

    private String clientSecrets;

    private String adminName;

    private String adminPassword;
}
