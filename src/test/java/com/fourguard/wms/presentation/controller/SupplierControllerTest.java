package com.fourguard.wms.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourguard.wms.application.dto.request.CreateSupplierRequest;
import com.fourguard.wms.application.dto.request.SupplierContactRequest;
import com.fourguard.wms.application.dto.request.UpdateSupplierStatusRequest;
import com.fourguard.wms.application.dto.response.SupplierResponse;
import com.fourguard.wms.domain.ports.in.SupplierUseCase;
import com.fourguard.wms.presentation.advice.DomainExceptionHandler;
import com.fourguard.wms.presentation.advice.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SupplierUseCase supplierUseCase;

    @InjectMocks
    private SupplierController supplierController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(supplierController)
                .setControllerAdvice(new GlobalExceptionHandler(), new DomainExceptionHandler())
                .build();
    }

    @Test
    void whenCreateSupplier_thenReturn200() throws Exception {
        SupplierContactRequest contact = SupplierContactRequest.builder()
                .fullName("Carlos Mendoza")
                .email("cmendoza@empaquesnorte.com.mx")
                .phone("8183456789")
                .build();

        CreateSupplierRequest request = CreateSupplierRequest.builder()
                .organizationId(UUID.randomUUID())
                .legalName("Empaques Nacionales del Norte S.A. de C.V.")
                .taxId("ENN980415HG8")
                .type("PACKAGING")
                .scopeType("GLOBAL")
                .contact(contact)
                .build();

        SupplierResponse response = SupplierResponse.builder()
                .id(UUID.randomUUID())
                .code("PRV-0001")
                .legalName(request.getLegalName())
                .build();

        when(supplierUseCase.createSupplier(any(CreateSupplierRequest.class))).thenReturn(response);

        mockMvc.perform(post("/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedor creado con éxito"))
                .andExpect(jsonPath("$.data.code").value("PRV-0001"))
                .andExpect(jsonPath("$.data.legalName").value(request.getLegalName()));
    }

    @Test
    void whenGetSupplierById_thenReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        SupplierResponse response = SupplierResponse.builder()
                .id(id)
                .code("PRV-0001")
                .legalName("Empaques S.A.")
                .build();

        when(supplierUseCase.getSupplierById(id)).thenReturn(response);

        mockMvc.perform(get("/suppliers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("PRV-0001"));
    }

    @Test
    void whenUpdateSupplierStatus_thenReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateSupplierStatusRequest request = UpdateSupplierStatusRequest.builder()
                .status("INACTIVE")
                .reason("Reason for status change")
                .build();

        SupplierResponse response = SupplierResponse.builder()
                .id(id)
                .code("PRV-0001")
                .status("INACTIVE")
                .statusReason("Reason for status change")
                .build();

        when(supplierUseCase.updateSupplierStatus(any(UUID.class), any(UpdateSupplierStatusRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/suppliers/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }
}
