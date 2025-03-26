package by.modsen.authservice.service;

import by.modsen.authservice.dto.request.AdminSignIn;
import by.modsen.authservice.dto.request.SignUp;
import by.modsen.authservice.dto.request.UserSignIn;
import by.modsen.authservice.dto.response.KeycloakAdminTokenResponse;
import by.modsen.authservice.dto.response.KeycloakUserTokenResponse;

public interface AuthService {

    void signUp(SignUp signUp);

    KeycloakUserTokenResponse userSignIn(UserSignIn userSignIn);

    KeycloakAdminTokenResponse adminSignIn(AdminSignIn adminSignIn);

}
