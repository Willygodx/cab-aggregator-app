package by.modsen.ratingservice.model;

import by.modsen.ratingservice.model.enums.RatedBy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Document(collection = "ratings")
public class Rating {

    @Id
    private String id;

    @Field(name = "ride_id")
    private Long rideId;

    @Field(name = "driver_id")
    private Long driverId;

    @Field(name = "passenger_id")
    private Long passengerId;

    @Field(name = "mark")
    private Integer mark;

    @Field(name = "comment")
    private String comment;

    @Field(name = "rated_by")
    private RatedBy ratedBy;

}
