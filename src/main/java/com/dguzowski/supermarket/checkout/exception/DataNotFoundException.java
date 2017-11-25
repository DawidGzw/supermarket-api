package com.dguzowski.supermarket.checkout.exception;

import org.springframework.util.StringUtils;

import java.text.MessageFormat;

public class DataNotFoundException extends RuntimeException {

    private Class<?> dataType;
    private Object[] params;

    protected DataNotFoundException(String message, Class<?> dataType, Object... params){
        super(MessageFormat.format(message, params));
        this.dataType = dataType;
        this.params = params;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public Object[] getParams() {
        return params;
    }
}
