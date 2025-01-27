package com.christmas.letter.sender.config;

import java.util.Arrays;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Slf4j
public class SecurityConfig {

	@Bean
	@Profile("prod")
	SecurityFilterChain defaultFilterChain(
			HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource)
			throws Exception {
		return getSecurityFilterChain(httpSecurity, true);
	}

	@Bean
	@Profile("!prod")
	SecurityFilterChain devFilterChain(
			HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource)
			throws Exception {
		return getSecurityFilterChain(httpSecurity, false);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
		corsConfiguration.setAllowedMethods(
				Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);

		return source;
	}

	private SecurityFilterChain getSecurityFilterChain(HttpSecurity httpSecurity, boolean isProd)
			throws Exception {
		httpSecurity
				.sessionManagement(
						sessionConfig ->
								sessionConfig.sessionCreationPolicy(
										SessionCreationPolicy.STATELESS))
				.cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable);

		if (isProd) {
			httpSecurity.requiresChannel(rcc -> rcc.anyRequest().requiresSecure());
		} else {
			httpSecurity.requiresChannel(rcc -> rcc.anyRequest().requiresInsecure());
		}

		httpSecurity
				.authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
				.oauth2ResourceServer(
						rsc ->
								rsc.jwt(
										jwtConfigurer ->
												jwtConfigurer.jwtAuthenticationConverter(
														new JwtAuthenticationConverter())));

		return httpSecurity.build();
	}
}
