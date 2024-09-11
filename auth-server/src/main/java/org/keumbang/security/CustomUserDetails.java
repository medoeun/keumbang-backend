package org.keumbang.security;

import java.util.Collection;
import java.util.Collections;

import org.keumbang.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CustomUserDetails implements UserDetails {

	private final User user;

	public CustomUserDetails(User user) {
		this.user = user;
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}
	public String getRole() {
		return user.getRole();  // 역할 정보를 직접 반환
	}

	@Override
	public String getPassword() {
		log.info("CustomUserDetails.getPassword() 호출 - 반환된 비밀번호: {}", user.getPassword());
		return user.getPassword();
	}

	// 계정 만료 여부 확인 로직
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// 계정 잠김 여부 확인 로직
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 자격 증명(비밀번호) 만료 여부 확인 로직
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return "권한".equals(user.getRole().toString());  // 인증된 상태만 활성화, 추후 role 설정 후 변경
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Collections.singletonList : 하나의 요소를 가진 불변 리스트 생성
		return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
	}

	public Long getMemberId() {
		return user.getId();
	}
}
