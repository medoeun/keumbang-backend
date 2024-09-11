package org.keumbang.controller;

import org.keumbang.BaseApiResponse;
import org.keumbang.dto.JwtResponse;
import org.keumbang.dto.LoginRequest;
import org.keumbang.security.JwtManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

	@PostMapping("/login")
	public BaseApiResponse<JwtResponse> login(@RequestBody LoginRequest loginRequest) {

		log.info("로그인 시도: {}", loginRequest.getUsername());

		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				loginRequest.getUsername(), loginRequest.getPassword()
			)
		);
		log.info("AuthenticationManager 호출 후 - 인증 성공");

		// 문제 없을 시 JWT 토큰 생성
		String token = jwtManager.generateToken(authentication);
		log.info("JWT 생성: {}", token);

		log.info("로그인 성공: {}", loginRequest.getUsername());
		JwtResponse jwtRes = new JwtResponse(token);

		return BaseApiResponse.success("로그인 성공", jwtRes);

	}
}
