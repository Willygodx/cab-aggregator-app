package by.modsen.passengerservice.kafka.consumer;

import by.modsen.passengerservice.constants.PassengerExceptionMessageKeys;
import by.modsen.passengerservice.dto.response.AverageRatingResponse;
import by.modsen.passengerservice.exception.passenger.PassengerNotFoundException;
import by.modsen.passengerservice.kafka.KafkaConstants;
import by.modsen.passengerservice.mapper.PassengerMapper;
import by.modsen.passengerservice.model.Passenger;
import by.modsen.passengerservice.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingConsumer {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @KafkaListener(
        topics = KafkaConstants.TOPIC_NAME_AVERAGE_RATING,
        groupId = KafkaConstants.GROUP_ID_AVERAGE_RATING,
        containerFactory = KafkaConstants.KAFKA_LISTENER_CONTAINER_FACTORY,
        properties = {
            KafkaConstants.TARGET_DTO_FOR_DESERIALIZATION_PROPERTY
        }
    )
    public void consumePassengerAverageRating(AverageRatingResponse averageRatingResponse) {
        Long passengerId = averageRatingResponse.userId();

        Passenger passenger = passengerRepository.findPassengerByIdAndIsDeletedIsFalse(passengerId)
            .orElseThrow(() -> new PassengerNotFoundException(
                PassengerExceptionMessageKeys.PASSENGER_NOT_FOUND_MESSAGE_KEY,
                passengerId
            ));

        passengerMapper.updatePassengerFromDto(averageRatingResponse, passenger);
        passengerRepository.save(passenger);
    }

}
