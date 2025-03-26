package by.modsen.driverservice.exception;

import lombok.Getter;

@Getter
public class MessageSourceException extends RuntimeException {

    private final String messageKey;
    private final transient Object[] args;

    public MessageSourceException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

}
