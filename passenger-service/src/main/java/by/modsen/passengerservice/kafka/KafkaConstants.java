package by.modsen.passengerservice.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaConstants {

    public static final String BOOTSTRAP_ADDRESS = "${spring.kafka.consumer.bootstrap-servers}";
    public static final String AUTO_OFFSET_RESET_CONFIG_VALUE = "earliest";
    public static final String TRUSTED_PACKAGES_VALUE = "*";
    public static final String TOPIC_NAME_AVERAGE_RATING = "passenger-average-ratings-topic";
    public static final String GROUP_ID_AVERAGE_RATING = "passenger-consumer";
    public static final String TARGET_DTO_FOR_DESERIALIZATION_PROPERTY =
        "spring.json.value.default.type=by.modsen.passengerservice.dto.response.AverageRatingResponse";
    public static final String KAFKA_LISTENER_CONTAINER_FACTORY = "kafkaListenerContainerFactory";
    public static final int AMOUNT_OF_REPLICAS = 3;
    public static final int AMOUNT_OF_PARTITIONS = 3;
    public static final String LOGS_TOPIC_NAME = "logs";

}
