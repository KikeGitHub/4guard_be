package com.fourguard.wms.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application-level bean configuration.
 *
 * <p>Contains infrastructure beans that are used across multiple layers:
 * <ul>
 *   <li>{@link PasswordEncoder} — BCrypt, used by auth use cases and V2 seed data</li>
 * </ul>
 *
 * <p>The following beans will be added in Phase 3:
 * <ul>
 *   <li>{@code AuthenticationManager} — required by the login use case</li>
 *   <li>{@code DaoAuthenticationProvider} — wired with UserDetailsService + PasswordEncoder</li>
 * </ul>
 */
@Configuration
public class ApplicationConfig {

    /**
     * BCrypt password encoder — strength 12.
     *
     * <p>Shared bean used by:
     * <ul>
     *   <li>{@code LoginUseCaseImpl} — to verify passwords</li>
     *   <li>{@code ResetPasswordUseCaseImpl} — to hash the new password</li>
     *   <li>The V2 seed data script (hash generated offline with this encoder)</li>
     * </ul>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
