package org.keumbang.security;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtManager {
	private static final String CLAIM_USER_ID = "id";
	private static final String CLAIM_USERNAME = "username";
	private static final String CLAIM_USER_ROLE = "role";

	private final Key secretKey;

	@Value("${jwt.token-validity-in-seconds}")
	private long tokenValidityInseconds;

	//jjwt: String secretKey -> Key 객체 방식으로 대체됨
	public JwtManager(@Value("${jwt.secret}") String secret) {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		this.secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
	}

	// JWT 토큰 생성
	public String generateToken(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		Long id = userDetails.getMemberId();
		String username = userDetails.getUsername();
		String role = userDetails.getRole();

		// JWT 토큰에 포함되는 정보(페이로드): jwt의 주체 memberId로
		Claims claims = Jwts.claims().setSubject(String.valueOf(id));
		claims.put(CLAIM_USER_ID, id);
		claims.put(CLAIM_USERNAME, username);
		claims.put(CLAIM_USER_ROLE, role);  // 역할 정보를 JWT에 추가

		Date now = new Date();
		Date validity = new Date(now.getTime() + tokenValidityInseconds * 1000);

		String token = Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)   // 발급 시간
			.setExpiration(validity)    // 만료 시간
			.signWith(secretKey, SignatureAlgorithm.HS256)  // 서명
			.compact();

		log.info("JWT 토큰 생성: 사용자 계정 = {}, 만료시간 = {}", username, validity);
		return token;
	}

	public String generateTokenForRefreshToken(UserDetails userDetails) {
		CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
		Long id = customUserDetails.getMemberId();
		String username = customUserDetails.getUsername();
		String role = customUserDetails.getRole();

		// JWT 페이로드 설정
		Claims claims = Jwts.claims().setSubject(String.valueOf(id));
		claims.put(CLAIM_USER_ID, id);
		claims.put(CLAIM_USERNAME, username);
		claims.put(CLAIM_USER_ROLE, role);

		Date now = new Date();
		Date validity = new Date(now.getTime() + tokenValidityInseconds * 1000);

		// JWT 생성
		String token = Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)   // 발급 시간
			.setExpiration(validity)    // 만료 시간
			.signWith(secretKey, SignatureAlgorithm.HS256)  // 서명
			.compact();

		log.info("Refresh Token 생성: 사용자 계정 = {}, 만료시간 = {}", username, validity);
		return token;
	}



	// HTTP 요청 헤더에서 JWT 토큰을 추출
	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);  // "Bearer " 이후의 토큰 실제부분만 추출
		}
		return null;
	}

	public Authentication getAuthentication(String token, UserDetails userDetails) {
		return new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());
	}

	// jjwt : parser() 메서드가 parserBuilder()로 대체됨
	// JWT 토큰에서 memberId 추출 (Body: 페이로드)
	public Long getMemberId(String token) {
		return Long.parseLong(Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject());
	}

	// JWT 토큰에서 username 추출
	public String getUsername(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.get(CLAIM_USERNAME, String.class);
	}

	// JWT 토큰에서 role 추출
	public String getRole(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.get(CLAIM_USER_ROLE, String.class);  // role을 추출
	}

	// JWT 토큰 유효성 검증
	public boolean isValidToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			log.debug("JWT 토큰 유효성 검사 통과: {}", token);
			return true;
		} catch (Exception e) {
			log.error("JWT 토큰 유효성 검사 실패: {}", token, e);
			return false;
		}
	}
}