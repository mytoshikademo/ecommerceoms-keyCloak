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
        UserResponse profile = userService.getProfileById(id);
        APIResponse response = APIResponse.builder().success(true).message("Profile fetched successfully").data(profile).build();
        log.info("fetch profile request completed and response provided. Id: {}, Email: {}", profile.getId(), profile.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/register")
    public ResponseEntity<APIResponse> register(@RequestBody @Valid UserRequest dto){
        log.info("Received request to register user. Email: {}", dto.getEmail());
        UserResponse register = userService.registerUser(dto);
        APIResponse response = APIResponse.builder().success(true).message("User registered successfully").data(register).build();
        log.info("user register request completed. Email: {}, Id: {}", register.getEmail(), register.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/auth/login")
    public ResponseEntity<APIResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("Received login request. Email: {}", request.getEmail());
        LoginResponse loginResponse = userService.login(request);
        APIResponse response = APIResponse.builder().success(true).message("Login successful").data(loginResponse).build();
        log.info("Login successful. Email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }
}
