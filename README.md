# 🛡️ 4GUARD WMS — Backend REST API

Este repositorio contiene el **Backend REST API** de **4GUARD WMS (Warehouse Management System)**, diseñado e implementado bajo una rigurosa **Arquitectura Hexagonal (Ports and Adapters)**. El sistema está construido con un enfoque modular, limpio y desacoplado, asegurando alta mantenibilidad, escalabilidad y facilidad para realizar pruebas unitarias y de integración.

---

## 🚀 Tecnologías y Stack Técnico

* **Lenguaje:** Java 17
* **Framework Principal:** Spring Boot 3.4.1 (Spring Web, Spring Security, Spring Cache, Spring Validation, Actuator)
* **Gestión de Dependencias:** Maven
* **Seguridad:** JSON Web Tokens (JWT) mediante filtros personalizados de autenticación y seguridad basada en métodos (`@PreAuthorize`)
* **Base de Datos:** PostgreSQL
* **Control de Versiones de BD:** Flyway Migrations
* **Caché:** Redis (desacoplado y configurable)
* **Mapeo de Objetos:** MapStruct 1.6.3 (con procesador Lombok integrado)
* **Boilerplate Reduction:** Lombok 1.18.36
* **Documentación del API:** SpringDoc OpenAPI 2.7.0 (Swagger UI)
* **Testing:** JUnit 5 & Mockito (con cobertura sobre controladores y servicios)

---

## 🏗️ Arquitectura Hexagonal

El proyecto está estructurado de forma que la lógica de negocio y las reglas de dominio permanezcan completamente aisladas del exterior (frameworks, base de datos, utilerías externas):

```text
com.fourguard.wms
│
├── domain                  <-- Capa de Dominio (Cero dependencias externas de Spring/JPA)
│   ├── model               <-- Modelos puros del negocio (User, Role, Organization, etc.)
│   ├── enums               <-- Enums de dominio (UserStatus, etc.)
│   ├── exception           <-- Excepciones de negocio (EntityNotFoundException, etc.)
│   └── ports               <-- Contratos
│       ├── in              <-- Puertos de Entrada / Casos de Uso (Interfaces de entrada)
│       └── out             <-- Puertos de Salida / Repositorios (Interfaces de salida)
│
├── application             <-- Capa de Aplicación
│   ├── usecase             <-- Implementación de los puertos de entrada (Lógica de Casos de Uso)
│   ├── dto                 <-- Objetos de Transferencia de Datos (Requests y Responses)
│   └── mapper              <-- Mapeadores MapStruct (Conversión DTO <-> Dominio <-> Entidad)
│
├── infrastructure          <-- Capa de Infraestructura (Implementación técnica y frameworks)
│   ├── configuration       <-- Configuraciones del sistema (SecurityConfig, SwaggerConfig, etc.)
│   ├── security            <-- Servicios de JWT y UserDetailsService
│   └── persistence         <-- Repositorios JPA y Entidades de Base de Datos
│       ├── entity          <-- Entidades JPA mapeadas a la base de datos
│       ├── repository      <-- Repositorios Spring Data JPA
│       └── adapter         <-- Implementación de los puertos de salida (Adapters de persistencia)
│
├── presentation            <-- Capa de Presentación (Controladores REST y adaptadores HTTP)
│   ├── controller          <-- Endpoints REST (AuthController, UserController)
│   ├── advice              <-- Manejo global y centralizado de excepciones (HTTP status mapping)
│   └── filter              <-- Filtros HTTP (RequestLoggingFilter)
│
└── shared                  <-- Recursos Compartidos (Constantes, auditorías y envoltorios de respuesta)
```

---

## 🛠️ Características Principales Implementadas

1. **Gestión Completa de Usuarios (CRUD)**:
   * **Crear, Leer, Actualizar y Eliminar** usuarios de forma física en base de datos.
   * Resolución de relaciones críticas (`Organization`, `Branch`, `Role`) en la capa de servicio.
   * Validación automática y rigurosa de entradas de datos (`@Valid`).
   * Validaciones de unicidad de negocio para `username` y `email` en la capa de aplicación.

2. **Seguridad y Control de Acceso**:
   * Seguridad por endpoints mediante **JWT**.
   * Autorización basada en privilegios con `@PreAuthorize("hasAuthority('SYS_ADMIN')")` para restringir operaciones de escritura de usuarios únicamente al rol de administrador.

3. **Formato Unificado de Respuesta**:
   * Todas las APIs retornan un formato homogéneo estructurado:
     ```json
     {
       "success": true,
       "message": "Usuario creado con éxito",
       "data": { ... },
       "timestamp": "2026-06-30T13:00:00"
     }
     ```

4. **Documentación Interactiva (Swagger UI)**:
   * APIs totalmente documentadas interactivamente para el desarrollo front-end.

---

## 🧪 Pruebas Unitarias

La lógica del negocio y los controladores se validan mediante pruebas unitarias exhaustivas con JUnit 5 y Mockito, alcanzando un excelente nivel de cobertura:

* **Servicios (`UserServiceTest`)**:
  * Creación y actualización de usuarios válidos.
  * Captura y validación de duplicados de `username`/`email`.
  * Validación de lanzamiento de excepciones (`EntityNotFoundException`, `ValidationException`).
* **Controladores (`UserControllerTest`)**:
  * Prueba aislada con `MockMvc Standalone Setup`.
  * Verificación de códigos HTTP (200 OK, 400 Bad Request, 404 Not Found).
  * Validación estructural del wrapper `ApiResponse`.

---

## 🚦 Instrucciones para Ejecución Local

### Prerrequisitos
* **Java Development Kit (JDK)** 17 o superior.
* **Apache Maven** instalado (o usando el wrapper `./mvnw`).
* Base de datos **PostgreSQL** corriendo localmente o en un contenedor docker.

### Configuración del Entorno (`application-dev.yml`)
Configura las credenciales de tu base de datos PostgreSQL local en el perfil de desarrollo:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/fourguard_wms
    username: tu_usuario
    password: tu_contraseña
```

### Ejecutar las Pruebas
Para ejecutar toda la suite de pruebas unitarias y de integración:
```bash
mvn clean test
```

### Correr la Aplicación
Para levantar el servidor en el puerto por defecto (`8080`):
```bash
mvn spring-boot:run
```

Una vez levantada la aplicación, la documentación de la API de Swagger se puede visualizar en:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**
