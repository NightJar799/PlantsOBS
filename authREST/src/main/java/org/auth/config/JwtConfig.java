package org.auth.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);
    
    private String secret;
    private long expiration;
    private long refreshExpiration;
    
    @PostConstruct
    public void validate() {
        log.info("JWT конфиг загружен - срок: {} ms, перегрузка: {} ms", expiration, refreshExpiration);
        
        if (secret == null || secret.isEmpty()) {
            log.warn("JWT секрет пуст");
            this.secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        }
        
        if (secret.length() < 32) {
            log.error("JWT секрет слишко короткий. длина: {}", secret.length());
            throw new IllegalStateException("JWT длинна секрета миниум 32 символа");
        }
    }
}