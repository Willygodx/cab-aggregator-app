package by.modsen.ridesservice.model;

import by.modsen.ridesservice.model.enums.RideStatus;
import by.modsen.ridesservice.model.enums.converter.RideStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "ride")
public class Ride {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "driver_id", nullable = false)
  private Long driverId;

  @Column(name = "passenger_id", nullable = false)
  private Long passengerId;

  @Column(name = "pickup_address", nullable = false)
  private String pickupAddress;

  @Column(name = "destination_address", nullable = false)
  private String destinationAddress;

  @Column(name = "ride_status", nullable = false)
  @Convert(converter = RideStatusConverter.class)
  private RideStatus rideStatus;

  @Column(name = "order_date_time", nullable = false)
  private LocalDateTime orderDateTime;

  @Column(name = "cost", nullable = false)
  private BigDecimal cost;

}
