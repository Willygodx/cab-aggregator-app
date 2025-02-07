package by.modsen.ridesservice.exception;

import lombok.Getter;

@Getter
public class MessageSourceException extends RuntimeException {

    private final String messageKey;
    private final Object[] args;

    public MessageSourceException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

}
