package by.modsen.ridesservice.aspect;

import by.modsen.ridesservice.client.driver.DriverFeignClient;
import by.modsen.ridesservice.client.driver.DriverResponse;
import by.modsen.ridesservice.constants.ApplicationConstants;
import by.modsen.ridesservice.constants.RideExceptionMessageKeys;
import by.modsen.ridesservice.exception.security.AccessDeniedException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidateAccessAspectDriver {

    private final DriverFeignClient driverFeignClient;

    @Around("@annotation(by.modsen.ridesservice.aspect.ValidateAccessDriver)")
    public Object hasPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) args[args.length - 1];
        UUID resourceId = (UUID) args[0];

        if (isAdmin(jwt)) {
            return joinPoint.proceed();
        }

        if (!isResourceOwner(jwt, resourceId)) {
            throw new AccessDeniedException(RideExceptionMessageKeys.ACCESS_DENIED_MESSAGE);
        }

        return joinPoint.proceed();
    }

    private boolean isAdmin(JwtAuthenticationToken token) {
        return token.getAuthorities().stream()
            .filter(Objects::nonNull)
            .anyMatch(authority -> ApplicationConstants.ROLE_ADMIN_VALUE.equals(authority.getAuthority()));
    }

    private boolean isResourceOwner(JwtAuthenticationToken token, UUID resourceId) {
        String userSub = token.getToken().getClaimAsString(ApplicationConstants.SUB_CLAIM);
        if (userSub == null) {
            return false;
        }

        String authToken = token.getToken().getTokenValue();

        DriverResponse driver = driverFeignClient.getDriverById(
            resourceId,
            LocaleContextHolder.getLocale().toLanguageTag(),
            ApplicationConstants.TOKEN_BEARER_PART + authToken
        );

        if (driver == null || driver.id() == null) {
            return false;
        }

        return userSub.equals(driver.id().toString());
    }

}
