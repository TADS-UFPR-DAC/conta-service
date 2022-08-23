package bantads.conta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

public class ApiRequestException extends ResponseStatusException {
    public ApiRequestException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
