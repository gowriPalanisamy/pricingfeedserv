package com.tigeranalytics.pricingfeed.filter;

import com.tigeranalytics.pricingfeed.exception.PricingFeedError;
import com.tigeranalytics.pricingfeed.exception.ResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Controller
public class GlobalExceptionHandler {

    @ExceptionHandler(PricingFeedError.class)
    public ResponseEntity<ResponseVO> handleResourceNotFoundException(PricingFeedError error) {
        ResponseVO responseVO=new ResponseVO();
        responseVO.setName(error.getPricingFeedErrorType().name());
        responseVO.setMessage(error.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseVO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " +
                ex.getMessage());
    }
}
