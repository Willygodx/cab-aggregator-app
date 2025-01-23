package by.modsen.passengerservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder(setterPrefix = "add")
public record PageResponseDto<T>(

    int currentOffset,
    int currentLimit,
    int totalPages,
    long totalElements,
    String sort,
    List<T> values

) {
}
