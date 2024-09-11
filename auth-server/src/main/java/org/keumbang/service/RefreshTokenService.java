package org.keumbang.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.keumbang.entity.RefreshToken;
import org.keumbang.exception.TokenRefreshException;
import org.keumbang.repository.RefreshTokenRepository;
import org.keumbang.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Value("${jwt.refresh-token-expiration-ms}")
	private Long refreshTokenExpirationMs;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	// Refresh Token 생성
	public RefreshToken createRefreshToken(Long userId) {
		RefreshToken refreshToken = RefreshToken.builder()
			.user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
			.token(UUID.randomUUID().toString())  // 고유한 토큰 값 생성
			.expiryDate(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs)))  //  밀리초 단위로 시간을 설정하고 LocalDateTime.now()에 더하여 만료 시간 설정
			.build();
		return refreshTokenRepository.save(refreshToken);
	}

	// Refresh Token 만료 여부 확인
	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
			refreshTokenRepository.delete(token);  // 만료된 토큰 삭제
			throw new TokenRefreshException("Refresh token이 만료되었습니다.");
		}
		return token;
	}

	// Refresh Token 삭제
	public void deleteByUserId(Long userId) {
		refreshTokenRepository.deleteByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
	}
}
