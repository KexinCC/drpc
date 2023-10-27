package org.xiaoheshan.exception;

public class NetWorkException extends RuntimeException {

    public NetWorkException() {
    }

    public NetWorkException(Exception e) {

    }

    public NetWorkException(String message) {
        super(message);
    }
}
