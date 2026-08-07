package com.mytoshika.ecommerceoms.service.impl;

import com.mytoshika.ecommerceoms.dto.UserRequest;
import com.mytoshika.ecommerceoms.dto.UserResponse;
import com.mytoshika.ecommerceoms.entity.User;
import com.mytoshika.ecommerceoms.repository.UserRepository;
import com.mytoshika.ecommerceoms.service.interfaces.EmailService;
import com.mytoshika.ecommerceoms.service.interfaces.KeycloakUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceRegisterTest {

    @Mock private UserRepository userRepo;
    @Mock private ModelMapper modelMapper;
    @Mock private EmailService emailService;
    @Mock private KeycloakUserService keycloakUserService;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void shouldRegisterUserSuccessfully() {

        // Arrange
        UserRequest request = createUserRequest();
        User mappedUser = createMappedUser();
        User savedUser = createSavedUser();
        UserResponse mappedResponse = createUserResponse();

        when(userRepo.existsByEmail(request.getEmail())).thenReturn(false);
        when(keycloakUserService.createUser(request)).thenReturn("kc123");
        when(modelMapper.map(request, User.class)).thenReturn(mappedUser);
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponse.class)).thenReturn(mappedResponse);

        // Act
        UserResponse response = userService.registerUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("Abhishek", response.getName());
        assertEquals("abc@gmail.com", response.getEmail());
        assertEquals(List.of("CUSTOMER"), response.getRole());

        verify(userRepo).existsByEmail(request.getEmail());
        verify(keycloakUserService).createUser(request);
        verify(userRepo).save(any(User.class));
        verify(emailService).sendWelcomeEmail(savedUser);
    }


    // -------- Helper Methods ----------------

    private UserRequest createUserRequest() {
        UserRequest request = new UserRequest();
        request.setName("Abhishek");
        request.setEmail("abc@gmail.com");
        request.setPassword("Admin@123");
        return request;
    }

    private User createMappedUser() {
        return User.builder()
                .name("Abhishek")
                .email("abc@gmail.com")
                .build();
    }

    private User createSavedUser() {
        return User.builder()
                .id(1L)
                .name("Abhishek")
                .email("abc@gmail.com")
                .keycloakUserId("kc123")
                .build();
    }

    private UserResponse createUserResponse() {
        return UserResponse.builder()
                .id(1L)
                .name("Abhishek")
                .email("abc@gmail.com")
                .role(List.of("CUSTOMER"))
                .build();
    }
}