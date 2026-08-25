package com.mytoshika.ecommerceoms.exception;

import com.mytoshika.ecommerceoms.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND,"Not Found",ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT,"Conflict" ,ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        return buildError(HttpStatus.BAD_REQUEST,"Validation Error",String.join(", ", messages));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildError(HttpStatus.UNAUTHORIZED,"Invalid Credentials",ex.getMessage());
    }

    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<ErrorResponse> handleUserDeleted(UserDeletedException ex) {
        return buildError(HttpStatus.FORBIDDEN,"User Deleted",ex.getMessage());
    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<ErrorResponse> handleUserDisabled(UserDisabledException ex) {
        return buildError(HttpStatus.FORBIDDEN,"User Disabled",ex.getMessage());
    }

    @ExceptionHandler(ProductDeletedException.class)
    public ResponseEntity<ErrorResponse> handleProductDeleted(ProductDeletedException  ex){
        return buildError(HttpStatus.GONE,"Deleted",ex.getMessage());
    }

    @ExceptionHandler(ProductAlreadyActiveException.class)
    public ResponseEntity<ErrorResponse> handleProductAlreadyActive(ProductAlreadyActiveException  ex){
        return buildError(HttpStatus.CONFLICT,"Conflict",ex.getMessage());
    }

    @ExceptionHandler({ProductNameConflictException.class,
            DuplicateReviewException.class})
    public ResponseEntity<ErrorResponse> handleProductNameConflict(RuntimeException  ex){
        return buildError(HttpStatus.CONFLICT,"Conflict",ex.getMessage());
    }

        @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error",ex.getMessage());
    }



    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status,
            String error,
            String message) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .build();

        return new ResponseEntity<>(response, status);
    }
}