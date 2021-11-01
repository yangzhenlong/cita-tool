package com.github.yzl.cita.exception;

public class BlockchainException extends RuntimeException {

    private String code;
    private String message;

    public BlockchainException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BlockchainException(BlockchainExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.code = exceptionType.getCode();
        this.message = exceptionType.getMessage();
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
