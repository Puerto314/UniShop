package co.edu.unbosque.unishop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad para generar, parsear y validar tokens JWT.
 */
@Component
public class JwtUtil {

	/** Validez del token: 24 horas en milisegundos. */
	private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000L;

	/**
	 * Clave secreta inyectada desde application.properties (jwt.secret). Debe tener
	 * al menos 32 caracteres para HMAC-SHA256.
	 */
	@Value("${jwt.secret:defaultSecretKeyWhichShouldBeAtLeast32CharactersLong}")
	private String secret;

	// ──────────────────────────────────────────────
	// Métodos de extracción
	// ──────────────────────────────────────────────

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		return claimsResolver.apply(extractAllClaims(token));
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	// ──────────────────────────────────────────────
	// Generación de token
	// ──────────────────────────────────────────────

	/**
	 * Genera un token JWT para el usuario. Incluye el primer rol como claim "role".
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();

		// Incluir el primer rol (ej. ROLE_ADMIN o ROLE_USER) en el claim "role"
		String role = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst()
				.orElse("ROLE_USER");
		claims.put("role", role);

		return createToken(claims, userDetails.getUsername());
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	// ──────────────────────────────────────────────
	// Validación
	// ──────────────────────────────────────────────

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// ──────────────────────────────────────────────
	// Utilidades internas
	// ──────────────────────────────────────────────

	private Key getSigningKey() {
		byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
