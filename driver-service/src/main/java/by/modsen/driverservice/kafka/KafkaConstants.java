package by.modsen.driverservice.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaConstants {

    public static final String BOOTSTRAP_ADDRESS = "${spring.kafka.consumer.bootstrap-servers}";
    public static final String AUTO_OFFSET_RESET_CONFIG_VALUE = "earliest";
    public static final String TRUSTED_PACKAGES_VALUE = "*";
    public static final String TOPIC_NAME_AVERAGE_RATING = "driver-average-ratings-topic";
    public static final String GROUP_ID_AVERAGE_RATING = "driver-consumer";
    public static final String TARGET_DTO_FOR_DESERIALIZATION_PROPERTY =
        "spring.json.value.default.type=by.modsen.driverservice.dto.response.AverageRatingResponse";
    public static final String KAFKA_LISTENER_CONTAINER_FACTORY = "kafkaListenerContainerFactory";

}
