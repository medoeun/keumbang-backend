package org.keumbang.repository;

import java.util.Optional;

import org.keumbang.entity.RefreshToken;
import org.keumbang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);
	void deleteByUser(User user);

}
