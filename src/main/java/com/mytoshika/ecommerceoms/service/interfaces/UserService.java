package com.mytoshika.ecommerceoms.service.interfaces;

import com.mytoshika.ecommerceoms.dto.*;

public interface UserService {

    UserResponse getProfileById(Long id);

    UserResponse registerUser(UserRequest userRequest);

    LoginResponse login(LoginRequest request);

}
