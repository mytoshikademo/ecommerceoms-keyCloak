package com.mytoshika.ecommerceoms.service.impl;

import com.mytoshika.ecommerceoms.config.KeycloakConfig;
import com.mytoshika.ecommerceoms.dto.LoginRequest;
import com.mytoshika.ecommerceoms.dto.TokenResponse;
import com.mytoshika.ecommerceoms.dto.UserRequest;
import com.mytoshika.ecommerceoms.service.interfaces.KeycloakUserService;
import jakarta.annotation.Nonnull;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements KeycloakUserService {

    private final Keycloak keycloak;
    private final KeycloakConfig config;
    private final RestClient restClient;

    @Override
    public String createUser(UserRequest request){
        UserRepresentation user = getUserRepresentation(request);
        RealmResource realmResource = keycloak.realm(config.getRealm());
        UsersResource usersResource = realmResource.users();
        String userId = createKeycloakUser(usersResource,user);
        assignCustomerRole(realmResource, usersResource, userId);
        return userId;
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        MultiValueMap<String, String> formData = buildFormData(request);
        return requestToken(formData);
    }


//        ----   Helper Method   -----

    @Nonnull
    private static UserRepresentation getUserRepresentation(UserRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setEmailVerified(true);
        user.setFirstName(request.getName());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));
        return user;
    }

    private String createKeycloakUser(UsersResource usersResource, UserRepresentation user) {
        try (Response response = usersResource.create(user)) {
            if (response.getStatus() != 201) {
                throw new RuntimeException(
                        String.format("Failed to create user in Keycloak. Status: %d, Message: %s",
                                response.getStatus(),
                                response.readEntity(String.class)));
            }
            return CreatedResponseUtil.getCreatedId(response);
        }
    }

    private void assignCustomerRole(RealmResource realmResource, UsersResource usersResource, String userId) {
        RoleRepresentation role = realmResource.roles().get("CUSTOMER").toRepresentation();

        usersResource.get(userId)
                .roles()
                .realmLevel()
                .add(List.of(role));
    }

    private MultiValueMap<String, String> buildFormData(LoginRequest request) {
        MultiValueMap<String, String> formData =  new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", config.getClientId());
        formData.add("client_secret", config.getClientSecret());
        formData.add("username", request.getEmail());
        formData.add("password", request.getPassword());
        return formData;
    }

    private TokenResponse requestToken(MultiValueMap<String, String> formData){
        return restClient.post()
                .uri(String.format("%s/realms/%s/protocol/openid-connect/token",
                                config.getServerUrl(),
                                config.getRealm()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(TokenResponse.class);
    }
}
