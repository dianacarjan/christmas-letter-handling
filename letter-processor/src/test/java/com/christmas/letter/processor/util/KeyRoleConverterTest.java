package com.christmas.letter.processor.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class KeyRoleConverterTest {

    private KeyRoleConverter keyRoleConverter;

    @BeforeEach
    void setup() {
        keyRoleConverter = new KeyRoleConverter();
    }

    @Test
    void givenJwtWithRoles_whenConverting_thenReturnGrantedAuthorities() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("SANTA", "ELF"));
        when(jwt.getClaims()).thenReturn(Map.of("realm_access", realmAccess));

        // Act
        Collection<GrantedAuthority> grantedAuthorities = keyRoleConverter.convert(jwt);

        // Assert
        assertThat(grantedAuthorities).isNotEmpty();
        assertThat(grantedAuthorities).contains(new SimpleGrantedAuthority("ROLE_SANTA"));
        assertThat(grantedAuthorities).contains(new SimpleGrantedAuthority("ROLE_ELF"));
    }

    @Test
    void givenEmptyRoles_whenConverting_thenReturnEmptyList() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Collections.emptyList());
        when(jwt.getClaims()).thenReturn(Map.of("realm_access", realmAccess));

        // Act
        Collection<GrantedAuthority> grantedAuthorities = keyRoleConverter.convert(jwt);

        // Assert
        assertThat(grantedAuthorities).isEmpty();
    }

    @Test
    void givenMissingRolesField_whenConverting_thenReturnEmptyList() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(Map.of("realm_access", new HashMap<>()));

        // Act
        Collection<GrantedAuthority> grantedAuthorities = keyRoleConverter.convert(jwt);

        // Assert
        assertThat(grantedAuthorities).isEmpty();
    }
}
