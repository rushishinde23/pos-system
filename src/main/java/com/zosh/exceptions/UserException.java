package com.zosh.exceptions;

import org.aspectj.bridge.IMessage;

public class UserException extends Throwable {
    public UserException(String message) {
        super(message);
    }
}
