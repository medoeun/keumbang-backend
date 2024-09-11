package org.keumbang.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtManager jwtManager;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		// Swagger ui 관련 요청은 필터링하지 않음
		String path = request.getRequestURI();
		if (path.startsWith("/swagger-ui") || path.startsWith("/webjars/") || path.startsWith("/v3/api-docs")) {
			filterChain.doFilter(request, response);
			return;
		}
		// request에서 토큰 파싱, 토큰 문자열 반환해 이후 인증 확인
		String token = jwtManager.resolveToken(request);

		if (token != null && jwtManager.isValidToken(token)) {
			String username = jwtManager.getUsername(token);
			String role = jwtManager.getRole(token);

			// JWT에서 권한 정보 추출
			GrantedAuthority authority = new SimpleGrantedAuthority(role);

			// UserDetailsService를 통해 UserDetails를 로드
			var userDetails = userDetailsService.loadUserByUsername(username);

			// 토큰이 유효한 경우 Authentication 생성
			UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken)jwtManager.getAuthentication(
				token, userDetails);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			// SecurityContext에 객체 설정
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		// 다음 필터로 요청 전달
		filterChain.doFilter(request, response);
	}
}
