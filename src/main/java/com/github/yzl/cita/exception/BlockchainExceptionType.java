package com.github.yzl.cita.exception;

public enum BlockchainExceptionType {
    SUCCEED("0", "succeed"),
    SEND_TRANSACTION_FAILED("301", "sendRawTransaction failed"),
    GET_TRANSACTION_FAILED("302", "appGetBlockByNumber failed"),
    GET_BLOCK_FAILED("303", "appGetBlockByNumber failed"),
    UNKNOWN("399", "unknown block chain exception"),
    ;

    String code;
    String message;

    BlockchainExceptionType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
