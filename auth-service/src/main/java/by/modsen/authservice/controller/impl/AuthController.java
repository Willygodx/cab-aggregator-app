package by.modsen.authservice.controller.impl;

import by.modsen.authservice.dto.request.AdminSignIn;
import by.modsen.authservice.dto.request.SignUp;
import by.modsen.authservice.dto.request.UserSignIn;
import by.modsen.authservice.dto.response.KeycloakAdminTokenResponse;
import by.modsen.authservice.dto.response.KeycloakUserTokenResponse;
import by.modsen.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@Valid @RequestBody SignUp signUp) {
        authService.signUp(signUp);
    }

    @PostMapping("/signin")
    public KeycloakUserTokenResponse userSignIn(@Valid @RequestBody UserSignIn userSignIn) {
        return authService.userSignIn(userSignIn);
    }

    @PostMapping("/signin/admin")
    public KeycloakAdminTokenResponse adminSignIn(@Valid @RequestBody AdminSignIn adminSignIn) {
        return authService.adminSignIn(adminSignIn);
    }

}
