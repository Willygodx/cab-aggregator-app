package by.modsen.driverservice.repository;

import by.modsen.driverservice.model.Car;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Page<Car> findAllByIsDeletedIsFalse(Pageable pageable);

    Boolean existsCarByCarNumberAndIsDeletedIsFalse(String carNumber);

    Boolean existsCarByCarNumberAndIsDeletedIsTrue(String carNumber);

    Optional<Car> findCarByIdAndIsDeletedIsFalse(Long id);

}
