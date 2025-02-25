package by.modsen.driverservice.controller.impl;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.modsen.driverservice.constants.CarExceptionMessageKeys;
import by.modsen.driverservice.constants.TestDataConstants;
import by.modsen.driverservice.configuration.TestConfig;
import by.modsen.driverservice.constants.DriverExceptionMessageKeys;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.exception.car.CarNotFoundException;
import by.modsen.driverservice.exception.driver.DriverAlreadyExistsException;
import by.modsen.driverservice.exception.driver.DriverNotFoundException;
import by.modsen.driverservice.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(DriverController.class)
@Import(TestConfig.class)
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    @Autowired
    private MessageSource messageSource;

    private String getMessage(String messageKey, Object... args) {
        return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }

    @Test
    void getAllDrivers_ReturnsPageDriverResponse_ValidRequest() throws Exception {
        PageResponse<DriverResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.DRIVER_RESPONSE)
        );
        when(driverService.getAllDrivers(anyInt(), anyInt()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_DRIVERS_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAllDrivers_ReturnsBadRequest_WhenInvalidData() throws Exception {
        mockMvc.perform(get(TestDataConstants.INVALID_ARGS_GET_ALL_DRIVERS_ENDPOINT))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("offset"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_OFFSET_MESSAGE));
    }

    @Test
    void getDriverById_ReturnsDriverResponse_ValidRequest() throws Exception {
        DriverResponse driverResponse = TestDataConstants.DRIVER_RESPONSE;
        Long driverId = TestDataConstants.DRIVER_ID;

        when(driverService.getDriverById(anyLong()))
            .thenReturn(driverResponse);

        mockMvc.perform(get(TestDataConstants.GET_DRIVER_BY_ID_ENDPOINT, driverId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(driverResponse)));
    }

    @Test
    void getDriverById_ReturnsNotFound_WhenDriverDoesNotExist() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};
        String message = getMessage(messageKey, driverId);

        when(driverService.getDriverById(driverId))
            .thenThrow(new DriverNotFoundException(messageKey, args));

        mockMvc.perform(get(TestDataConstants.GET_DRIVER_BY_ID_ENDPOINT, driverId))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).getDriverById(driverId);
    }

    @Test
    void createDriver_ReturnsCreatedDriverResponse_ValidRequest() throws Exception {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        DriverResponse driverResponse = TestDataConstants.DRIVER_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_DRIVER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.createDriver(driverRequest))
            .thenReturn(driverResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(driverResponse)));
    }

    @Test
    void createDriver_ReturnsConflict_WhenDriverAlreadyExistsByEmail() throws Exception {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String email = driverRequest.email();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_EMAIL_MESSAGE_KEY;
        Object[] args = new Object[]{email};
        String message = getMessage(messageKey, email);

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_DRIVER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.createDriver(driverRequest))
            .thenThrow(new DriverAlreadyExistsException(messageKey, args));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).createDriver(driverRequest);
    }

    @Test
    void createDriver_ReturnsConflict_WhenDriverAlreadyExistsByPhoneNumber() throws Exception {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String phoneNumber = driverRequest.phoneNumber();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_PHONE_KEY;
        Object[] args = new Object[]{phoneNumber};
        String message = getMessage(messageKey, phoneNumber);

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_DRIVER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.createDriver(driverRequest))
            .thenThrow(new DriverAlreadyExistsException(messageKey, args));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).createDriver(driverRequest);
    }

    @Test
    void createDriver_ReturnsBadRequest_WhenInvalidData() throws Exception {
        DriverRequest invalidRequest = TestDataConstants.INVALID_DRIVER_REQUEST_DATA;

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_DRIVER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_EMAIL));
    }

    @Test
    void updateDriver_ReturnsUpdatedDriverResponse_ValidRequest() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        DriverResponse driverResponse = TestDataConstants.DRIVER_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.updateDriverById(driverRequest, driverId))
            .thenReturn(driverResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(driverResponse)));
    }

    @Test
    void updateDriver_ReturnsNotFound_WhenDriverDoesNotExist() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};
        String message = getMessage(messageKey, driverId);

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.updateDriverById(driverRequest, driverId))
            .thenThrow(new DriverNotFoundException(messageKey, args));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).updateDriverById(driverRequest, driverId);
    }

    @Test
    void updateDriver_ReturnsConflict_WhenDriverAlreadyExistsByEmail() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String email = driverRequest.email();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_EMAIL_MESSAGE_KEY;
        Object[] args = new Object[]{email};
        String message = getMessage(messageKey, email);

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.updateDriverById(driverRequest, driverId))
            .thenThrow(new DriverAlreadyExistsException(messageKey, args));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).updateDriverById(driverRequest, driverId);
    }

    @Test
    void updateDriver_ReturnsConflict_WhenDriverAlreadyExistsByPhoneNumber() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String phoneNumber = driverRequest.phoneNumber();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_PHONE_KEY;
        Object[] args = new Object[]{phoneNumber};
        String message = getMessage(messageKey, phoneNumber);

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.updateDriverById(driverRequest, driverId))
            .thenThrow(new DriverAlreadyExistsException(messageKey, args));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).updateDriverById(driverRequest, driverId);
    }

    @Test
    void updateDriver_ReturnsBadRequest_WhenInvalidData() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest invalidRequest = TestDataConstants.INVALID_DRIVER_REQUEST_DATA;

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_EMAIL));
    }

    @Test
    void deleteDriver_ReturnsNoContent_ValidRequest() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;

        MockHttpServletRequestBuilder requestBuilder = delete(
            TestDataConstants.DELETE_DRIVER_BY_ID_ENDPOINT,
            driverId
        );

        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteDriver_ReturnsNotFound_WhenDriverDoesNotExist() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};
        String message = getMessage(messageKey, driverId);

        doThrow(new DriverNotFoundException(messageKey, args))
            .when(driverService).deleteDriverById(driverId);

        mockMvc.perform(delete(TestDataConstants.DELETE_DRIVER_BY_ID_ENDPOINT, driverId))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).deleteDriverById(driverId);
    }

    @Test
    void addCarToDriver_ReturnsOk_ValidRequest() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        Long carId = TestDataConstants.CAR_ID;

        MockHttpServletRequestBuilder requestBuilder = post(
            TestDataConstants.ADD_CAR_TO_DRIVER_ENDPOINT,
            driverId,
            carId
        );

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk());
    }

    @Test
    void addCarToDriver_ReturnsNotFound_WhenDriverDoesNotExist() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};
        String message = getMessage(messageKey, driverId);

        doThrow(new DriverNotFoundException(messageKey, args))
            .when(driverService).addCarToDriver(driverId, carId);

        mockMvc.perform(post(TestDataConstants.ADD_CAR_TO_DRIVER_ENDPOINT, driverId, carId))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).addCarToDriver(driverId, carId);
    }

    @Test
    void addCarToDriver_ReturnsNotFound_WhenCarDoesNotExist() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};
        String message = getMessage(messageKey, carId);

        doThrow(new CarNotFoundException(messageKey, args))
            .when(driverService).addCarToDriver(driverId, carId);

        mockMvc.perform(post(TestDataConstants.ADD_CAR_TO_DRIVER_ENDPOINT, driverId, carId))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).addCarToDriver(driverId, carId);
    }
}