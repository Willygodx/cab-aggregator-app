package by.modsen.ridesservice.repository;

import by.modsen.ridesservice.model.Ride;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    boolean existsById(@NonNull Long id);

    @NonNull
    Page<Ride> findAll(@NonNull Pageable pageable);

    @NonNull
    Page<Ride> findAllByDriverId(@NonNull Pageable pageable, @NonNull UUID driverId);

    @NonNull
    Page<Ride> findAllByPassengerId(@NonNull Pageable pageable, @NonNull UUID passengerId);

}
