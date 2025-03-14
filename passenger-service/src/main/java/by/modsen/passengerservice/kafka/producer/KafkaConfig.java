package by.modsen.passengerservice.kafka.producer;

import by.modsen.passengerservice.kafka.KafkaConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {

    @Value(KafkaConstants.BOOTSTRAP_ADDRESS)
    private List<String> bootstrapAddress;

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
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
