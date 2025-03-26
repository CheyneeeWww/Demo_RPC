package org.example.common.exception;

/**
 * @Author cnwang
 * @Date created in 13:45 2025/3/26
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String message){
        super(message);
    }

    public SerializeException(String message,Throwable cause){
        super(message,cause);
    }
}
