package by.modsen.driverservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "car")
public class Car {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "color", nullable = false)
  private String color;

  @Column(name = "car_brand", nullable = false)
  private String carBrand;

  @Column(name = "car_number", nullable = false, unique = true)
  private String carNumber;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  @ManyToMany(mappedBy = "cars")
  private Set<Driver> drivers = new HashSet<>();

}
