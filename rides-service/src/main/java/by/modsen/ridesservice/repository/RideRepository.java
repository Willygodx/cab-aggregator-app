package by.modsen.ridesservice.repository;

import by.modsen.ridesservice.model.Ride;
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

    Page<Ride> findAllByDriverId(Pageable pageable, Long driverId);

    Page<Ride> findAllByPassengerId(Pageable pageable, Long passengerId);

}
