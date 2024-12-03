package dev.hyun.playground.global.config;

import dev.hyun.playground.domain.chatting.dto.ChattingDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String consumerServer;
    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String producerServer;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChattingDto.ChatRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChattingDto.ChatRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ChattingDto.ChatRequest> consumerFactory() {
        JsonDeserializer<ChattingDto.ChatRequest> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");

        Map<String, Object> consumerConfigurations = new HashMap<>();
        consumerConfigurations.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerServer);
        consumerConfigurations.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        consumerConfigurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        consumerConfigurations.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }

    @Bean
    public ProducerFactory<String, ChattingDto.ChatRequest> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigurations());
    }

    @Bean
    public Map<String, Object> producerConfigurations() {
        Map<String, Object> producerConfigurations = new HashMap<>();
        producerConfigurations.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerServer);
        producerConfigurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return producerConfigurations;
    }

    @Bean
    public KafkaTemplate<String, ChattingDto.ChatRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
