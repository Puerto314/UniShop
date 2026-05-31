package co.edu.unbosque.unishop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final UserDetailsService userDetailsService;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
			throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource)).csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						// Rutas públicas
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

						// Solo admins gestionan admins
						.requestMatchers("/admin/**").hasRole("ADMIN")

						// Ver clientes
						.requestMatchers(HttpMethod.GET, "/cliente/mostrartodo").hasAnyRole("USER", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/cliente/buscar/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/cliente/**").hasRole("ADMIN")

						// Productos
						.requestMatchers(HttpMethod.GET, "/producto/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/producto/**").hasRole("ADMIN")

						// ── Órdenes de compra ────────────────────────────────────────
						// Crear pedido y ver los propios: USER o ADMIN
						.requestMatchers(HttpMethod.POST, "/ordenes").hasAnyRole("USER", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/ordenes/mias").hasAnyRole("USER", "ADMIN")
						// Ver TODOS los pedidos: solo ADMIN
						.requestMatchers(HttpMethod.GET, "/ordenes/todas").hasRole("ADMIN")

						.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(
				Arrays.asList("http://localhost:4200", "https://unishopueb.netlify.app", "https://gpcueb.org"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("Authorization"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
