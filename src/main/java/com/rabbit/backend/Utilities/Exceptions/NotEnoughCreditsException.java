package com.rabbit.backend.Utilities.Exceptions;

public class NotEnoughCreditsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String errMessage;

    public NotEnoughCreditsException(Integer code, String errMessage) {
        super();
        this.code = code;
        this.errMessage = errMessage;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getErrMessage() {
        return this.errMessage;
    }
}
