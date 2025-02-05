package by.modsen.ridesservice.service.impl;

import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.PageResponse;
import by.modsen.ridesservice.dto.response.RideResponse;
import by.modsen.ridesservice.mapper.PageResponseMapper;
import by.modsen.ridesservice.mapper.RideMapper;
import by.modsen.ridesservice.model.Ride;
import by.modsen.ridesservice.repository.RideRepository;
import by.modsen.ridesservice.service.RideService;
import by.modsen.ridesservice.service.component.RideServicePriceGenerator;
import by.modsen.ridesservice.service.component.validation.RideServiceValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideServiceValidation rideServiceValidation;
    private final RideMapper rideMapper;
    private final PageResponseMapper pageResponseMapper;
    private final RideRepository rideRepository;
    private final RideServicePriceGenerator rideServicePriceGenerator;

    @Override
    @Transactional
    public RideResponse createRide(RideRequest rideRequest) {
        Ride ride = rideMapper.toEntity(rideRequest, rideServicePriceGenerator);

        ride = rideRepository.save(ride);

        return rideMapper.toResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse updateRide(RideRequest rideRequest, Long id) {
        Ride ride = rideServiceValidation.findRideByIdWithCheck(id);

        rideMapper.updateRideFromDto(rideRequest, ride);

        ride = rideRepository.save(ride);

        return rideMapper.toResponse(ride);
    }

    @Override
    @Transactional
    public RideResponse updateRideStatus(RideStatusRequest rideStatusRequest, Long id) {
        Ride ride = rideServiceValidation.findRideByIdWithCheck(id);
        rideServiceValidation.validChangeRideStatus(ride, rideStatusRequest);

        rideMapper.updateRideFromDto(rideStatusRequest, ride);

        ride = rideRepository.save(ride);

        return rideMapper.toResponse(ride);
    }

    @Override
    public PageResponse<RideResponse> getAllRides(Integer offset, Integer limit) {
        Page<RideResponse> ridesPageDto = rideRepository
            .findAll(PageRequest.of(offset, limit))
            .map(rideMapper::toResponse);

        return pageResponseMapper.toDto(ridesPageDto);
    }

    @Override
    public PageResponse<RideResponse> getAllRidesByDriver(Integer offset, Integer limit,
                                                          Long driverId) {
        Page<RideResponse> ridesPageDto = rideRepository
            .findAllByDriverId(PageRequest.of(offset, limit), driverId)
            .map(rideMapper::toResponse);

        return pageResponseMapper.toDto(ridesPageDto);
    }

    @Override
    public PageResponse<RideResponse> getAllRidesByPassenger(Integer offset, Integer limit,
                                                             Long passengerId) {
        Page<RideResponse> ridesPageDto = rideRepository
            .findAllByPassengerId(PageRequest.of(offset, limit), passengerId)
            .map(rideMapper::toResponse);

        return pageResponseMapper.toDto(ridesPageDto);
    }

    @Override
    @Transactional
    public void deleteRideById(Long rideId) {
        rideServiceValidation.checkRideExistsById(rideId);

        rideRepository.deleteById(rideId);
    }

    @Override
    public RideResponse getRideById(Long rideId) {
        Ride ride = rideServiceValidation.findRideByIdWithCheck(rideId);

        return rideMapper.toResponse(ride);
    }

}
