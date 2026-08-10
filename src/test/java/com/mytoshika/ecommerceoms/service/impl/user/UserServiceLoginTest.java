package com.mytoshika.ecommerceoms.service.impl;

import com.mytoshika.ecommerceoms.dto.LoginRequest;
import com.mytoshika.ecommerceoms.dto.LoginResponse;
import com.mytoshika.ecommerceoms.dto.TokenResponse;
import com.mytoshika.ecommerceoms.service.interfaces.KeycloakUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceLoginTest {

    @Mock private KeycloakUserService keycloakUserService;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void shouldLoginUserSuccessfully(){
        //Arrange
        LoginRequest request = createLoginRequest();
        TokenResponse tokenResponse = createTokenResponse();
        when(keycloakUserService.login(request)).thenReturn(tokenResponse);

        //Act
        LoginResponse response = userService.login(request);

        //Assert
        assertNotNull(response);
        assertEquals("access123", response.getAccessToken());
        assertEquals("refresh123", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());

        verify(keycloakUserService).login(request);

    }

    @Test
    void shouldThrowExceptionWhenKeycloakUserLoginFails(){

        //Arrange
        LoginRequest request = createLoginRequest();
        when(keycloakUserService.login(request))
                .thenThrow(new RuntimeException("Keycloak Login failed"));

        //Act
        RuntimeException exception =  assertThrows(
                RuntimeException.class,
                ()->userService.login(request));

        //Assert
        assertEquals("Keycloak Login failed", exception.getMessage());
        verify(keycloakUserService).login(request);

    }

    //Helper Method

    private LoginRequest createLoginRequest() {
         return LoginRequest.builder()
                .email("abc@gmail.com")
                .password("Admin@123")
                .build();
    }

    private TokenResponse createTokenResponse() {
        return TokenResponse.builder()
                .accessToken("access123")
                .refreshToken("refresh123")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }
}
