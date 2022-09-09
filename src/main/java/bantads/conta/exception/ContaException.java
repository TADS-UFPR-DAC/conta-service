package bantads.conta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ContaException extends ResponseStatusException {

    public ContaException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
