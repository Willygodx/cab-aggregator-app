package by.modsen.ratingservice.kafka.producer;

import by.modsen.ratingservice.dto.AverageRatingMessage;
import by.modsen.ratingservice.kafka.KafkaConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingProducer {

    private final KafkaTemplate<String, AverageRatingMessage> kafkaTemplate;

    public void sendPassengerAverageRating(AverageRatingMessage message) {
        kafkaTemplate.send(KafkaConstants.PASSENGER_TOPIC_NAME, message);
    }

    public void sendDriverAverageRating(AverageRatingMessage message) {
        kafkaTemplate.send(KafkaConstants.DRIVER_TOPIC_NAME, message);
    }

}
