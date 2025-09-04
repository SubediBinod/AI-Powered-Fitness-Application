package com.binod.AI_SERVICE.conflg;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    /*
      FLOW:
      1. Create Queue (activityQueue) → messages will be stored here
      2. Create Exchange (activityExchange) → decides how messages are routed
      3. Bind Queue + Exchange with routingKey → ensures correct delivery
      4. Define MessageConverter → convert Java object ↔ JSON automatically
    */

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Bean
    public Queue activityQueue() // here make sure the queue is from amp not util package
    {
        return new Queue(queue, true); // true indicates the message still exist even if the mq restarts
    }

    @Bean
    public DirectExchange activityExchange(){
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange){
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    } // this bean converts java object to json before sending them to mq
}
