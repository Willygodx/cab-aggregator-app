package by.modsen.driverservice.kafka.consumer;

import by.modsen.driverservice.constants.DriverExceptionMessageKeys;
import by.modsen.driverservice.dto.response.AverageRatingResponse;
import by.modsen.driverservice.exception.driver.DriverNotFoundException;
import by.modsen.driverservice.kafka.KafkaConstants;
import by.modsen.driverservice.mapper.DriverMapper;
import by.modsen.driverservice.model.Driver;
import by.modsen.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingConsumer {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @KafkaListener(
        topics = KafkaConstants.TOPIC_NAME_AVERAGE_RATING,
        groupId = KafkaConstants.GROUP_ID_AVERAGE_RATING,
        containerFactory = KafkaConstants.KAFKA_LISTENER_CONTAINER_FACTORY,
        properties = {
            KafkaConstants.TARGET_DTO_FOR_DESERIALIZATION_PROPERTY
        }
    )
    public void consumePassengerAverageRating(AverageRatingResponse averageRatingResponse) {
        Long driverId = averageRatingResponse.userId();

        Driver driver = driverRepository.findDriverByIdAndIsDeletedIsFalse(driverId)
            .orElseThrow(() -> new DriverNotFoundException(
                DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY,
                driverId
            ));

        driverMapper.updateDriverFromDto(averageRatingResponse, driver);
        driverRepository.save(driver);
    }

}
