package by.modsen.ratingservice.configuration;

import by.modsen.ratingservice.model.enums.converter.RatedByReadConverter;
import by.modsen.ratingservice.model.enums.converter.RatedByWriteConverter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(
            new RatedByReadConverter(),
            new RatedByWriteConverter()
        ));
    }

}