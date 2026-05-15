package org.plrest.graphql.security;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация защиты GraphQL API от вредоносных запросов.
 */
@Configuration
public class GraphQLSecurityConfig {

    /**
     * Максимальная глубина вложенности запроса (20 уровней).
     * Стандартный introspection-запрос имеет глубину ~15 уровней,
     * поэтому лимит 20 безопасен для всех легитимных запросов.
     */
    @Bean
    public Instrumentation maxQueryDepthInstrumentation() {
        return new MaxQueryDepthInstrumentation(20);
    }

    /**
     * Максимальная сложность запроса (200 единиц).
     * Каждое запрошенное поле добавляет 1 к сложности.
     */
    @Bean
    public Instrumentation maxQueryComplexityInstrumentation() {
        return new MaxQueryComplexityInstrumentation(200);
    }
}