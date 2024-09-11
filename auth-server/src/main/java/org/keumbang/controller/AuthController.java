package org.keumbang.controller;

import org.keumbang.BaseApiResponse;
import org.keumbang.dto.JwtResponse;
import org.keumbang.dto.LoginRequest;
import org.keumbang.dto.TokenRefreshRequest;
import org.keumbang.entity.RefreshToken;
import org.keumbang.exception.TokenRefreshException;
import org.keumbang.security.CustomUserDetailsService;
import org.keumbang.security.JwtManager;
import org.keumbang.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtManager jwtManager;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@PostMapping("/login")
	public BaseApiResponse<JwtResponse> login(@RequestBody LoginRequest loginRequest) {

		log.info("로그인 시도: {}", loginRequest.getUsername());

		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				loginRequest.getUsername(), loginRequest.getPassword()
			)
		);
		log.info("AuthenticationManager 호출 후 - 인증 성공");

		// Access Token 생성
		String accessToken = jwtManager.generateToken(authentication);
		log.info("JWT 생성: {}", accessToken);

		// Refresh Token 생성 및 저장
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication.getName());

		log.info("로그인 성공: {}", loginRequest.getUsername());
		JwtResponse jwtRes = new JwtResponse(accessToken, refreshToken.getToken());
		return BaseApiResponse.success("로그인 성공", jwtRes);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<BaseApiResponse<JwtResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
		String refreshToken = request.getRefreshToken();

		return refreshTokenService.findByToken(refreshToken)
			.map(refreshTokenService::verifyExpiration)  // 만료 확인
			.map(RefreshToken::getUser)
			.map(user -> {
				UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
				String newAccessToken = jwtManager.generateTokenForRefreshToken(userDetails);  // 새로운 Access Token 생성
				return ResponseEntity.ok(BaseApiResponse.success("Access Token 갱신 성공", new JwtResponse(newAccessToken, refreshToken)));
			})
			.orElseThrow(() -> new TokenRefreshException("유효하지 않은 Refresh Token 입니다."));
	}
}
