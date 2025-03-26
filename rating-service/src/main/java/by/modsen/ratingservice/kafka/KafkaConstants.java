package by.modsen.ratingservice.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaConstants {

    public static final String DRIVER_TOPIC_NAME = "driver-average-ratings-topic";
    public static final String PASSENGER_TOPIC_NAME = "passenger-average-ratings-topic";
    public static final String BOOTSTRAP_ADDRESS = "${spring.kafka.producer.bootstrap-servers}";
    public static final int AMOUNT_OF_REPLICAS = 3;
    public static final int AMOUNT_OF_PARTITIONS = 3;
    public static final String LOGS_TOPIC_NAME = "logs";

}
