package org.keumbang.exception;

import org.keumbang.BaseApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	// UsernameNotFoundException 처리
	@ExceptionHandler(UsernameNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<BaseApiResponse<String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		return new ResponseEntity<>(
			BaseApiResponse.failure("사용자를 찾을 수 없습니다: " + ex.getMessage()),
			HttpStatus.NOT_FOUND
		);
	}

	// IllegalArgumentException 처리
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<BaseApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
		return new ResponseEntity<>(
			BaseApiResponse.failure("잘못된 요청입니다: " + ex.getMessage()),
			HttpStatus.BAD_REQUEST
		);
	}

	// 기타 예외 처리
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<BaseApiResponse<String>> handleGeneralException(Exception ex) {
		return new ResponseEntity<>(
			BaseApiResponse.failure("서버에서 문제가 발생했습니다: " + ex.getMessage()),
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}
}
