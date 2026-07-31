package com.mytoshika.ecommerceoms.service.interfaces;

import com.mytoshika.ecommerceoms.dto.LoginRequest;
import com.mytoshika.ecommerceoms.dto.TokenResponse;
import com.mytoshika.ecommerceoms.dto.UserRequest;

public interface KeycloakUserService {
    String createUser(UserRequest request);
    TokenResponse login(LoginRequest request);
}
