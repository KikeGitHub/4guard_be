package com.fourguard.wms.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourguard.wms.application.dto.request.auth.LoginRequest;
import com.fourguard.wms.application.dto.request.auth.LogoutRequest;
import com.fourguard.wms.application.dto.request.auth.RefreshTokenRequest;
import com.fourguard.wms.application.dto.response.auth.AuthResponse;
import com.fourguard.wms.domain.ports.in.LoginUseCase;
import com.fourguard.wms.domain.ports.in.LogoutUseCase;
import com.fourguard.wms.domain.ports.in.RefreshTokenUseCase;
import com.fourguard.wms.presentation.advice.GlobalExceptionHandler;
import com.fourguard.wms.presentation.advice.DomainExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock private LoginUseCase loginUseCase;
    @Mock private RefreshTokenUseCase refreshTokenUseCase;
    @Mock private LogoutUseCase logoutUseCase;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler(), new DomainExceptionHandler())
                .build();
    }

    @Test
    void whenLogin_thenReturn200() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .identifier("user")
                .password("password")
                .build();
        AuthResponse authResponse = AuthResponse.builder().accessToken("token").build();

        when(loginUseCase.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sesión iniciada con éxito"));
    }

    @Test
    void whenRefresh_thenReturn200() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("oldToken")
                .build();
        AuthResponse authResponse = AuthResponse.builder().accessToken("newToken").build();

        when(refreshTokenUseCase.refresh(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tokens refrescados con éxito"));
    }

    @Test
    void whenLogoutAuthenticated_thenReturn200() throws Exception {
        LogoutRequest request = LogoutRequest.builder()
                .refreshToken("someRefreshToken")
                .build();

        doNothing().when(logoutUseCase).logout(any(LogoutRequest.class), eq("operator"));

        mockMvc.perform(post("/auth/logout")
                        .principal(() -> "operator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sesión cerrada con éxito"));
    }

    @Test
    void whenLogoutUnauthenticated_thenReturn401() throws Exception {
        LogoutRequest request = LogoutRequest.builder()
                .refreshToken("someRefreshToken")
                .build();

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
