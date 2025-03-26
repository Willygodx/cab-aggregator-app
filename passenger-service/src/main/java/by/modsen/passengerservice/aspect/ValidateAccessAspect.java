package by.modsen.passengerservice.aspect;

import by.modsen.passengerservice.constants.ApplicationConstants;
import by.modsen.passengerservice.constants.ApplicationExceptionMessages;
import by.modsen.passengerservice.exception.security.AccessDeniedException;
import by.modsen.passengerservice.service.PassengerService;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidateAccessAspect {

    private final PassengerService passengerService;

    @Around("@annotation(by.modsen.passengerservice.aspect.ValidateAccess)")
    public Object hasPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) args[args.length - 1];
        UUID resourceId = (UUID) args[0];

        if (isAdmin(jwt)) {
            return joinPoint.proceed();
        }

        if (!isResourceOwner(jwt, resourceId)) {
            throw new AccessDeniedException(ApplicationExceptionMessages.DEFAULT_ACCESS_DENIED_MESSAGE);
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

        UUID resourceOwnerId = passengerService.getPassengerById(resourceId).id();

        return userSub.equals(resourceOwnerId.toString());
    }

}
