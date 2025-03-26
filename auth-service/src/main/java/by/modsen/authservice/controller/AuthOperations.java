package by.modsen.authservice.controller;

import by.modsen.authservice.dto.request.AdminSignIn;
import by.modsen.authservice.dto.request.SignUp;
import by.modsen.authservice.dto.request.UserSignIn;
import by.modsen.authservice.dto.response.KeycloakAdminTokenResponse;
import by.modsen.authservice.dto.response.KeycloakUserTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth controller",
    description = "This controller contains CRUD operations for authentication controller in auth service")
public interface AuthOperations {

    @Operation(description = "Sign in as user")
    KeycloakUserTokenResponse userSignIn(@Valid @RequestBody UserSignIn userSignIn);

    @Operation(description = "Sign up")
    void signUp(@Valid @RequestBody SignUp signUp);

    @Operation(description = "Sign in as admin")
    KeycloakAdminTokenResponse adminSignIn(@Valid @RequestBody AdminSignIn adminSignIn);

}
