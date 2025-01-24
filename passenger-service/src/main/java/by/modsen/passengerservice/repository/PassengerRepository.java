package by.modsen.passengerservice.repository;

import by.modsen.passengerservice.model.Passenger;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

  Page<Passenger> findAllByIsDeletedIsFalse(Pageable pageable);

  Optional<Passenger> findPassengerByIdAndIsDeletedIsFalse(Long id);

  Boolean existsPassengerByEmailAndIsDeletedIsFalse(String email);

  Boolean existsPassengerByPhoneNumberAndIsDeletedIsFalse(String phoneNumber);

  Boolean existsPassengerByEmailAndIsDeletedIsTrue(String email);

  Boolean existsPassengerByPhoneNumberAndIsDeletedIsTrue(String phoneNumber);

}
