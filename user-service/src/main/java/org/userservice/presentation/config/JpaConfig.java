package org.userservice.presentation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration for auditing
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}