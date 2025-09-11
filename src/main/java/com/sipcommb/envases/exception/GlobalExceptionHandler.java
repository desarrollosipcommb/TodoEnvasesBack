package com.sipcommb.envases.exception;

import com.sipcommb.envases.dto.CustomApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<CustomApiResponse> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                          WebRequest webRequest) {

    System.out.println("prin");
    CustomApiResponse response=new CustomApiResponse(ex.getMessage(), webRequest.getDescription(false));
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

}
