package com.eureka.ip.team1.urjung_main.common.exception;

import com.eureka.ip.team1.urjung_main.common.ApiResponse;
import com.eureka.ip.team1.urjung_main.common.enums.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateFieldException.class)
    public ResponseEntity<ApiResponse> handleDuplicateField(DuplicateFieldException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
                .result(Result.DUPLICATED)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiResponse> handleDatabaseException(DatabaseException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
                .result(Result.SERVER_ERROR)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse> handleForbiddenException(ForbiddenException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
                .result(Result.FORBIDDEN)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse> handleInvalidInputException(InvalidInputException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
                .result(Result.INVALIDATED)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFoundException(NotFoundException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
                .result(Result.NOT_FOUND)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleTokenExpiredException(TokenExpiredException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
                .result(Result.UNAUTHORIZED)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity handleTokenInvalidException(TokenInvalidException exception) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .result(Result.UNAUTHORIZED)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity handleInternalServerErrorException(InternalServerErrorException exception) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .result(Result.SERVER_ERROR)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity handleMailSendException(MailSendException exception) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .result(Result.FAIL)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }


    @ExceptionHandler(ChatBotException.class)
    public ResponseEntity handleChatBotException(ChatBotException exception){
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .result(Result.FAIL)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
