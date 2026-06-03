package org.grpc.plant.robotlistener.config;

import org.plantrmq.RoutingKeys;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMQConfig {

    public static final String ROBOT_CREATED_QUEUE = "q.robot-customise.created";
    public static final String ROBOT_UPDATED_QUEUE = "q.robot-customise.updated";
    public static final String ROBOT_DLQ = "q.robot-customise.dlq";

    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public TopicExchange eventsExchange() {
        return ExchangeBuilder.topicExchange(RoutingKeys.EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(RoutingKeys.EXCHANGE + ".dlx").durable(true).build();
    }

    @Bean
    public Queue robotCreatedQueue() {
        return QueueBuilder.durable(ROBOT_CREATED_QUEUE)
                .deadLetterExchange(RoutingKeys.EXCHANGE + ".dlx")
                .deadLetterRoutingKey(ROBOT_DLQ)
                .build();
    }

    @Bean
    public Queue robotUpdatedQueue() {
        return QueueBuilder.durable(ROBOT_UPDATED_QUEUE)
                .deadLetterExchange(RoutingKeys.EXCHANGE + ".dlx")
                .deadLetterRoutingKey(ROBOT_DLQ)
                .build();
    }

    @Bean
    public Queue robotDlq() {
        return QueueBuilder.durable(ROBOT_DLQ).build();
    }

    @Bean
    public Binding robotCreatedBinding(Queue robotCreatedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(robotCreatedQueue).to(eventsExchange).with(RoutingKeys.ROBOT_CREATED);
    }

    @Bean
    public Binding robotUpdatedBinding(Queue robotUpdatedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(robotUpdatedQueue).to(eventsExchange).with(RoutingKeys.ROBOT_UPDATED);
    }

    @Bean
    public Binding robotDlqBinding(Queue robotDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(robotDlq).to(deadLetterExchange).with(ROBOT_DLQ);
    }
}