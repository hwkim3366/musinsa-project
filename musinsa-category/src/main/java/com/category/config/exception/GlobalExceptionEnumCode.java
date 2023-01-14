package com.category.config.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GlobalExceptionEnumCode implements EnumCode {
	
     NOT_FOUND_DATA("No data available")
    ,NOT_FOUND_PARENT_CATEGORY("No parent category")
    ,CAN_NOT_UPDATE("Parent category cannot be edited")
	,VALIDATION_ERROR("required fields are missing")
	;
    
	private String message;
	
    @Override
    public String getName() {
    	
        return this.name();
    }
    
    @Override
    public String getMessage() {
    	
        return message;
    }

    
}
