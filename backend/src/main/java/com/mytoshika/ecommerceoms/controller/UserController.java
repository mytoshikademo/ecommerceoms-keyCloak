package com.mytoshika.ecommerceoms.controller;

import com.mytoshika.ecommerceoms.dto.*;
import com.mytoshika.ecommerceoms.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "USER API",
        description = "User management APIs"
)
@RestController
@RequestMapping("/api/v1")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/users/{id}")
    public ResponseEntity<APIResponse> getProfile(@PathVariable  Long id){
        log.info("Received request to fetch profile. Id:{}", id);
        UserResponse response = userService.getProfileById(id);
        log.info("fetch profile request completed and response provided. Id: {}, Email: {}", response.getId(), response.getEmail());
        return ResponseEntity.ok(buildResponse("Profile fetched successfully",response));
    }

    @PostMapping("/users/register")
    public ResponseEntity<APIResponse> register(@RequestBody @Valid UserRequest dto){
        log.info("Received request to register user. Email: {}", dto.getEmail());
        UserResponse response = userService.registerUser(dto);
        log.info("user register request completed. Email: {}, Id: {}", response.getEmail(), response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse("User registered successfully",response));
    }
    @PostMapping("/auth/login")
    public ResponseEntity<APIResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("Received login request. Email: {}", request.getEmail());
        LoginResponse loginResponse = userService.login(request);
        log.info("Login successful. Email: {}", request.getEmail());
        return ResponseEntity.ok(buildResponse("Login successful",loginResponse));
    }

    @GetMapping("/users/me")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<APIResponse> getMyProfile() {
        UserResponse response = userService.getMyProfile();
        return ResponseEntity.ok(buildResponse("My profile fetched successfully",response));
    }

    //Helper method
    private APIResponse buildResponse(String message, Object response){
        return APIResponse.builder().success(true).message(message).data(response).build();
    }
}
