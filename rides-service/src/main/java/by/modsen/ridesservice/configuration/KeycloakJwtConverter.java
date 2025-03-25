package by.modsen.ratingservice.configuration;

import by.modsen.ratingservice.constants.ApplicationConstants;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakJwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        if (isAdminClient(jwt)) {
            return Collections.singleton(new SimpleGrantedAuthority(ApplicationConstants.ADMIN_ROLE));
        }

        return extractRealmRoles(jwt);
    }

    private boolean isAdminClient(Jwt jwt) {
        return Objects.equals(jwt.getClaim(ApplicationConstants.CLIENT_ID_CLAIM), ApplicationConstants.ADMIN_CLIENT_ID);
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim(ApplicationConstants.REALM_ACCESS_CLAIM);

        if (realmAccess == null) {
            return Collections.emptyList();
        }

        Object roles = realmAccess.get(ApplicationConstants.ROLES_CLAIM);

        if (!(roles instanceof List<?> roleList)) {
            return Collections.emptyList();
        }

        return roleList.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(role -> ApplicationConstants.ROLE_PREFIX + role)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

}