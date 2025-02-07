package by.modsen.ratingservice.dto.response;

import java.util.List;
import lombok.Builder;

@Builder(setterPrefix = "add")
public record PageResponse<T>(

    int currentOffset,
    int currentLimit,
    int totalPages,
    long totalElements,
    String sort,
    List<T> values

) {
}
