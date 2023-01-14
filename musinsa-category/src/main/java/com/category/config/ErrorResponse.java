package com.category.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private String errorCode;
    
    public static ErrorResponse systemError (String errorCode , String message ) {
    	
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.errorCode = errorCode;
        errorResponse.message = message;
        
        return errorResponse;
        
    }
    
}
