package com.mytoshika.ecommerceoms.service.impl;

import com.mytoshika.ecommerceoms.dto.*;
import com.mytoshika.ecommerceoms.entity.User;
import com.mytoshika.ecommerceoms.exception.*;
import com.mytoshika.ecommerceoms.repository.UserRepository;
import com.mytoshika.ecommerceoms.service.interfaces.EmailService;
import com.mytoshika.ecommerceoms.service.interfaces.KeycloakUserService;
import com.mytoshika.ecommerceoms.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final KeycloakUserService keycloakUserService;

    @Override
    public UserResponse getProfileById(Long id) {
        log.info("fetching profile for user id: {}", id);
        User targetUser = getUser(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(isAdmin(authentication) || targetUser.getKeycloakUserId().equals(getLoginUserKeycloakId())){
            log.info("profile fetched successfully for user id: {}. Email: {}", id, targetUser.getEmail());
            return buildUserResponse(targetUser, extractRoles(authentication));
        }
        throw new AccessDeniedException("You are not allowed to access this profile.");
    }

    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        log.info("Registering User. Email: {}", userRequest.getEmail());
        validateEmail(userRequest.getEmail());
        log.info("Creating new user. Email: {}", userRequest.getEmail());
        String keycloakUserId = null;
        try {
            keycloakUserId = keycloakUserService.createUser(userRequest);
            User newUser = createUser(userRequest, keycloakUserId);
            sendWelcomeEmail(newUser);
            return buildUserResponse(newUser, List.of("CUSTOMER"));
        } catch (Exception ex) {
            if (keycloakUserId != null) {
                keycloakUserService.deleteUser(keycloakUserId);
            }
            throw ex;
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
            TokenResponse tokenResponse = keycloakUserService.login(request);
            return LoginResponse.builder()
                    .accessToken(tokenResponse.getAccessToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .tokenType(tokenResponse.getTokenType())
                    .expiresIn(tokenResponse.getExpiresIn())
                    .build();
        }

    @Override
    public UserResponse getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByKeycloakUserId(getLoginUserKeycloakId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserResponse userResponse = buildUserResponse(user, extractRoles(authentication));
        userResponse.setUserId(extractUserId(userResponse.getRole(), userResponse.getId()));
        return  userResponse;
    }

    // --------  Helper Methods   ----------

    private  User getUser(Long id){
        return userRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with user id {}", id);
                    return new ResourceNotFoundException("User not found");
                });
    }

    private UserResponse buildUserResponse(User user, List<String> roles){
        UserResponse response = modelMapper.map(user, UserResponse.class);
        response.setRole(roles);
        return response;
    }

    private void validateEmail(String email){
        if (userRepo.existsByEmail(email)) {
            log.warn("User with this email already exist. Email:{}", email);
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }

    private User createUser(UserRequest userRequest, String keycloakUserId){
        User user = modelMapper.map(userRequest, User.class);
        user.setKeycloakUserId(keycloakUserId);
        return userRepo.save(user);
    }

    private void sendWelcomeEmail(User user){
        try {
            emailService.sendWelcomeEmail(user);
            log.info("User Registered successfully. and E-mail sent, Email: {}, Id: {}", user.getEmail(), user.getId());
        }catch (Exception ex){
            log.error("User registered but welcome email failed. Email: {}, Id: {}", user.getEmail(), user.getId(), ex);
        }
    }

    private boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(authority->authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private List<String> extractRoles(Authentication authentication){
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toList();
    }

    private String extractUserId(List<String> roles, Long id){
        if(roles.contains("ADMIN")){
            return "ADMIN-"+id;
        }
        return "CUS-"+id;
    }
    private String getLoginUserKeycloakId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }
}
