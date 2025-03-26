package by.modsen.authservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {

    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error occurred!";
    public static final String PHONE_REGEX =
        "^\\+375(25\\d{3}\\d{2}\\d{2}|(29[1-9]\\d{2}\\d{2}\\d{2})|(33\\d{3}\\d{2}\\d{2})|(44\\d{3}\\d{2}\\d{2}))$";
    public static final String PASSWORD_REGEX =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    public static final String REQUEST_LOGGING_MESSAGE =
        "Request. Method: {}, URI: {}, Args -> {}";
    public static final String RESPONSE_LOGGING_MESSAGE =
        "Response. Method: {}, URI: {}, Args -> {} Time Taken: {} ms";
    public static final String ERROR_SERIALIZING_JSON_MESSAGE = "Error serializing object to JSON";
    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SEX_ATTRIBUTE = "sex";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ERROR_MESSAGE_KEY = "errorMessage";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String EMPTY_STRING = "";
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String SERVICE_UNAVAILABLE_MESSAGE_KEY = "service.unavailable";
    public static final String ROLE_PASSENGER = "PASSENGER";

}
