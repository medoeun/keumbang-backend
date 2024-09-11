package org.keumbang.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenRefreshRequest {
	private String refreshToken;  // 클라이언트에서 보내는 Refresh Token
}
