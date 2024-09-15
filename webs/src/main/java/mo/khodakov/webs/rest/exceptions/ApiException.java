package mo.khodakov.webs.rest.exceptions;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getData());
    }
}
