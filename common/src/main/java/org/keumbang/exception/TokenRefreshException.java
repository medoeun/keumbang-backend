package org.keumbang.exception;

public class TokenRefreshException extends RuntimeException {

	// 직렬화 버전 ID
	private static final long serialVersionUID = 1L;


	public TokenRefreshException(String message) {
		super(message);
	}

	public TokenRefreshException(String message, Throwable cause) {
		super(message, cause);
	}
}
