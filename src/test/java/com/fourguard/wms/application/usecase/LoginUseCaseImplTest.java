package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.auth.LoginRequest;
import com.fourguard.wms.application.dto.response.auth.AuthResponse;
import com.fourguard.wms.application.dto.response.auth.UserInfoResponse;
import com.fourguard.wms.application.mapper.UserMapper;
import com.fourguard.wms.domain.enums.UserStatus;
import com.fourguard.wms.domain.exception.InvalidCredentialsException;
import com.fourguard.wms.domain.ports.out.UserRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.RoleEntity;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import com.fourguard.wms.infrastructure.security.jwt.JwtProperties;
import com.fourguard.wms.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseImplTest {

    @Mock private UserRepositoryPort userRepositoryPort;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private JwtProperties jwtProperties;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private LoginUseCaseImpl loginUseCase;

    private UserEntity activeUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        RoleEntity role = RoleEntity.builder().name("ADMIN").level(7).build();

        activeUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("enrique")
                .email("enrique@4guard.com")
                .password("encodedPassword")
                .isEnabled(true)
                .status(UserStatus.ACTIVE)
                .role(role)
                .build();

        loginRequest = LoginRequest.builder()
                .identifier("enrique")
                .password("admin123")
                .build();
    }

    @Test
    void whenLoginWithValidCredentials_thenReturnsAuthResponse() {
        // Arrange
        when(userRepositoryPort.findByUsernameOrEmail("enrique")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken(activeUser)).thenReturn("mock-access-token");
        when(jwtService.generateRefreshToken(activeUser)).thenReturn("mock-refresh-token");
        when(jwtProperties.getAccessTokenExpiration()).thenReturn(3600000L);

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .id(activeUser.getId())
                .username("enrique")
                .email("enrique@4guard.com")
                .role("ADMIN")
                .build();
        when(userMapper.toUserInfoResponse(activeUser)).thenReturn(userInfo);

        // Act
        AuthResponse response = loginUseCase.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mock-access-token", response.getAccessToken());
        assertEquals("mock-refresh-token", response.getRefreshToken());
        assertEquals("enrique", response.getUser().getUsername());
        verify(userRepositoryPort, times(1)).save(activeUser);
    }

    @Test
    void whenLoginWithUnknownUser_thenThrowsInvalidCredentialsException() {
        // Arrange
        when(userRepositoryPort.findByUsernameOrEmail("enrique")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.login(loginRequest));
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void whenLoginWithWrongPassword_thenThrowsInvalidCredentialsException() {
        // Arrange
        when(userRepositoryPort.findByUsernameOrEmail("enrique")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.login(loginRequest));
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void whenLoginWithDisabledUser_thenThrowsInvalidCredentialsException() {
        // Arrange
        activeUser.setIsEnabled(false);
        when(userRepositoryPort.findByUsernameOrEmail("enrique")).thenReturn(Optional.of(activeUser));

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.login(loginRequest));
        verify(userRepositoryPort, never()).save(any());
    }
}
