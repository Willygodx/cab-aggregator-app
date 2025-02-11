package by.modsen.driverservice.repository;

import by.modsen.driverservice.model.Driver;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Page<Driver> findAllByIsDeletedIsFalse(Pageable pageable);

    Boolean existsDriverByPhoneNumberAndIsDeletedIsFalse(String phoneNumber);

    Boolean existsDriverByEmailAndIsDeletedIsFalse(String email);

    Boolean existsDriverByPhoneNumberAndIsDeletedIsTrue(String phoneNumber);

    Boolean existsDriverByEmailAndIsDeletedIsTrue(String email);

    Optional<Driver> findDriverByIdAndIsDeletedIsFalse(Long id);

}
