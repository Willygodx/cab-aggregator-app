package by.modsen.passengerservice.mapper;

import by.modsen.passengerservice.dto.PageResponseDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PageResponseMapper {
  default <T> PageResponseDto<T> toDto(Page<T> page) {
    return PageResponseDto.<T>builder()
        .addValues(page.getContent())
        .addTotalElements(page.getTotalElements())
        .addCurrentOffset(page.getPageable().getPageNumber())
        .addCurrentLimit(page.getPageable().getPageSize())
        .addTotalPages(page.getTotalPages())
        .addSort(page.getSort().toString())
        .build();
  }
}
