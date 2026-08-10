package com.mytoshika.ecommerceoms.service.impl;

import com.mytoshika.ecommerceoms.dto.UserResponse;
import com.mytoshika.ecommerceoms.entity.User;
import com.mytoshika.ecommerceoms.exception.AccessDeniedException;
import com.mytoshika.ecommerceoms.exception.ResourceNotFoundException;
import com.mytoshika.ecommerceoms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceGetProfileTest {

    @Mock private UserRepository userRepo;
    @Mock private ModelMapper modelMapper;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void shouldGetProfileSuccessfully() {

        // Arrange
        Long id = 1L;
        User targetUser = createTargetUser();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        UserResponse expectedResponse = createUserResponse();
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(userRepo.findById(id)).thenReturn(Optional.of(targetUser));
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("kc123");
        doReturn(List.of(authority)).when(authentication).getAuthorities();
        when(modelMapper.map(targetUser, UserResponse.class)).thenReturn(expectedResponse);
        setAuthentication(authentication);

        // Act
        UserResponse userResponse = userService.getProfileById(id);

        // Assert
        assertNotNull(userResponse);
        assertEquals(id, userResponse.getId());
        assertEquals("Abhishek", userResponse.getName());
        assertEquals("abc@gmail.com", userResponse.getEmail());
        assertEquals(List.of("CUSTOMER"), userResponse.getRole());

        verify(userRepo).findById(id);
        verify(modelMapper).map(targetUser, UserResponse.class);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound(){
        //Arrange
        Long id = 1L;
        when(userRepo.findById(id))
                .thenReturn(Optional.empty());

        //Act
        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        ()-> userService.getProfileById(id));

        //Assert
        assertEquals("User not found", exception.getMessage());
        verify(userRepo).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenUserIdNotSame(){
        //Arrange
        Long id= 1L;
        User targetUser = createTargetUser();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        when(userRepo.findById(id)).thenReturn(Optional.of(targetUser));

        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        doReturn(List.of(authority)).when(authentication).getAuthorities();
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("kc456");
        setAuthentication(authentication);

        //Act
        AccessDeniedException exception =
                assertThrows(AccessDeniedException.class,
                        ()->userService.getProfileById(id));

        //Assert
        assertEquals("You are not allowed to access this profile.", exception.getMessage());
        verify(userRepo).findById(id);

    }

    @Test
    void shouldGetProfileSuccessfullyIfRoleIsAdmin(){
        //Arrange
        Long id = 1L;
        User targetUser = createTargetUser();
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");

        when(userRepo.findById(id)).thenReturn(Optional.of(targetUser));
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("kc456");
        doReturn(List.of(authority)).when(authentication).getAuthorities();
        setAuthentication(authentication);

        //Act
        UserResponse userResponse = userService.getProfileById(id);

        //Assert
        assertNotNull(userResponse);
        assertEquals(id,userResponse.getId());
        assertEquals("Abhishek",userResponse.getName());
        assertEquals("abc@gmail.com",userResponse.getEmail());
        assertEquals(List.of("ADMIN"), userResponse.getRole());

        verify(userRepo).findById(id);
        verify(modelMapper).map(targetUser, UserResponse.class);
    }

    //Helper Method
    private User createTargetUser() {
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

    private void setAuthentication(Authentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

}