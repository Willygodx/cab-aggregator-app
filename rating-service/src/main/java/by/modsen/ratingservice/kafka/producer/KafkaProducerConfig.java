package by.modsen.ratingservice.kafka.producer;

import by.modsen.ratingservice.dto.AverageRatingMessage;
import by.modsen.ratingservice.kafka.KafkaConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

    @Value(KafkaConstants.BOOTSTRAP_ADDRESS)
    private List<String> bootstrapAddress;

    @Bean
    public ProducerFactory<String, AverageRatingMessage> averageRatingProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AverageRatingMessage> kafkaTemplate() {
        KafkaTemplate<String, AverageRatingMessage> kafkaTemplate = new KafkaTemplate<>(averageRatingProducerFactory());
        kafkaTemplate.setObservationEnabled(true);
        return kafkaTemplate;
    }

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic passengerAverageRatingTopic() {
        return TopicBuilder.name(KafkaConstants.PASSENGER_TOPIC_NAME)
            .replicas(KafkaConstants.AMOUNT_OF_REPLICAS)
            .partitions(KafkaConstants.AMOUNT_OF_PARTITIONS)
            .build();
    }

    @Bean
    public NewTopic driverAverageRatingTopic() {
        return TopicBuilder.name(KafkaConstants.DRIVER_TOPIC_NAME)
            .replicas(KafkaConstants.AMOUNT_OF_REPLICAS)
            .partitions(KafkaConstants.AMOUNT_OF_PARTITIONS)
            .build();
    }

    @Bean
    public KafkaAdmin.NewTopics logsTopic() {
        return new KafkaAdmin.NewTopics(
            TopicBuilder.name(KafkaConstants.LOGS_TOPIC_NAME)
                .replicas(KafkaConstants.AMOUNT_OF_REPLICAS)
                .partitions(KafkaConstants.AMOUNT_OF_PARTITIONS)
                .build());
    }

}
