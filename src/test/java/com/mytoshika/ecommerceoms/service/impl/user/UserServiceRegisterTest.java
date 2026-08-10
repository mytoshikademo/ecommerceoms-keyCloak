package com.mytoshika.ecommerceoms.service.impl.user;

import com.mytoshika.ecommerceoms.dto.UserRequest;
import com.mytoshika.ecommerceoms.dto.UserResponse;
import com.mytoshika.ecommerceoms.entity.User;
import com.mytoshika.ecommerceoms.exception.EmailAlreadyExistsException;
import com.mytoshika.ecommerceoms.repository.UserRepository;
import com.mytoshika.ecommerceoms.service.impl.UserServiceImpl;
import com.mytoshika.ecommerceoms.service.interfaces.EmailService;
import com.mytoshika.ecommerceoms.service.interfaces.KeycloakUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceRegisterTest {
    @Mock private UserRepository userRepo;
    @Mock private ModelMapper modelMapper;
    @Mock private EmailService emailService;
    @Mock private KeycloakUserService keycloakUserService;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void shouldRegisterUserSuccessfully(){
        //Arrange
        UserRequest request = createUserRequest();
        User mappedUser =createMappedUser();
        User savedUser = createSavedUser();
        UserResponse mappedResponse = createUserResponse();

        when(userRepo.existsByEmail(request.getEmail())).thenReturn(false);
        when(keycloakUserService.createUser(request)).thenReturn("AbhiKey123");
        when(modelMapper.map(request, User.class)).thenReturn(mappedUser);
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponse.class)).thenReturn(mappedResponse);


        //Act
        UserResponse response = userService.registerUser(request);

        //Assert
        assertNotNull(response);
        assertEquals("Abhishek", response.getName());
        assertEquals("abc@gmail.com", response.getEmail());
        assertEquals(List.of("CUSTOMER"), response.getRole());

        verify(userRepo).existsByEmail(request.getEmail());
        verify(keycloakUserService).createUser(request);
        verify(userRepo).save(any(User.class));
        verify(emailService).sendWelcomeEmail(savedUser);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExist(){
        //Arrange
        UserRequest request = createUserRequest();

        when(userRepo.existsByEmail(request.getEmail())).thenReturn(true);
        //Act
        EmailAlreadyExistsException exception =
                assertThrows(EmailAlreadyExistsException.class, ()->
            userService.registerUser(request)
        );
        //Assert
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepo).existsByEmail(request.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenKeycloakUserCreationFails(){
        //Arrange
        UserRequest request = createUserRequest();
        when(userRepo.existsByEmail(request.getEmail())).thenReturn(false);
        when(keycloakUserService.createUser(request))
                .thenThrow(new RuntimeException("Keycloak user creation failed"));
        //Act
        RuntimeException exception = assertThrows(RuntimeException.class, ()->userService.registerUser(request));

        //Assert
        assertEquals("Keycloak user creation failed", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserSaveFails(){
        //Arrange
        UserRequest request = createUserRequest();
        User mappedUser = createMappedUser();

        when(userRepo.existsByEmail(request.getEmail())).thenReturn(false);
        when(keycloakUserService.createUser(request)).thenReturn("AbhiKey123");
        when(modelMapper.map(request,User.class)).thenReturn(mappedUser);
        when(userRepo.save(mappedUser))
                .thenThrow(new RuntimeException("Database save failed"));

        //Act
        RuntimeException exception = assertThrows(RuntimeException.class,()->userService.registerUser(request));

        //Assert
        assertEquals("Database save failed", exception.getMessage());
    }

    @Test
    void shouldRegisterUserSuccessfullyWhenEmailServiceFails(){
        //Arrange
        UserRequest request = createUserRequest();
        User mappedUser = createMappedUser();
        User savedUser = createSavedUser();
        UserResponse mappedResponse = createUserResponse();

        when(userRepo.existsByEmail(request.getEmail())).thenReturn(false);
        when(keycloakUserService.createUser(request)).thenReturn("AbhiKey123");
        when(modelMapper.map(request, User.class)).thenReturn(mappedUser);
        when(userRepo.save(mappedUser)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponse.class)).thenReturn(mappedResponse);
        doThrow(new RuntimeException("Email Sending fail"))
                .when(emailService).sendWelcomeEmail(savedUser);

        //Act
        UserResponse response = userService.registerUser(request);

        //Assert
        assertNotNull(response);
        assertEquals("Abhishek",response.getName());
        assertEquals("abc@gmail.com", response.getEmail());
        assertEquals(List.of("CUSTOMER"), response.getRole());

        verify(userRepo).existsByEmail(request.getEmail());
        verify(keycloakUserService).createUser(request);
        verify(userRepo).save(any(User.class));
        verify(emailService).sendWelcomeEmail(savedUser);
    }

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
