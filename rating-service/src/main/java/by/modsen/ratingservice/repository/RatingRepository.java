package by.modsen.ratingservice.repository;

import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {

    boolean existsByRideIdAndRatedBy(@NonNull Long rideId, @NonNull RatedBy ratedBy);

    boolean existsById(@NonNull String id);

    @NonNull
    Page<Rating> findAll(@NonNull Pageable pageable);

    @NonNull
    Page<Rating> findAllByPassengerIdAndRatedBy(@NonNull Long passengerId,
                                               @NonNull RatedBy ratedBy,
                                               @NonNull Pageable pageable);

    List<Rating> findAllByPassengerIdAndRatedBy(@NonNull Long passengerId,
                                               @NonNull RatedBy ratedBy);

    @NonNull
    Page<Rating> findAllByDriverIdAndRatedBy(@NonNull Long driverId,
                                            @NonNull RatedBy ratedBy,
                                            @NonNull Pageable pageable);

    List<Rating> findAllByDriverIdAndRatedBy(@NonNull Long driverId,
                                            @NonNull RatedBy ratedBy);

    boolean existsByPassengerId(@NonNull Long passengerId);

    boolean existsByDriverId(@NonNull Long driverId);

    @NonNull
    Optional<Rating> findById(@NonNull String id);

}
