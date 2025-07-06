package com.jakhongir.gelocation.exceptionHandler;

import com.jakhongir.gelocation.exception.ExternalApiException;
import com.jakhongir.gelocation.exception.InvalidIpAddressException;
import com.jakhongir.gelocation.exception.ProviderUnavailableException;
import com.jakhongir.gelocation.exception.RateLimitExceededException;
import com.jakhongir.gelocation.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidIpAddressException.class)
    public ResponseEntity<ErrorResponse> handleInvalidIpAddress(InvalidIpAddressException e) {
        log.warn("Invalid IP address: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse("INVALID_IP_ADDRESS", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(RateLimitExceededException e) {
        log.warn("Rate limit exceeded: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse("RATE_LIMIT_EXCEEDED", e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }

    @ExceptionHandler(ProviderUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleProviderUnavailableException(ProviderUnavailableException e) {
        log.error("Provider unavailable: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse("PROVIDER_UNAVAILABLE", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(ExternalApiException e) {
        log.error("External API error: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse("EXTERNAL_AP_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
