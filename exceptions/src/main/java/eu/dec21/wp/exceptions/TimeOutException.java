package eu.dec21.wp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
public class TimeOutException extends ResponseStatusException {
    private static final long serialVersionUID = 1L;

    public TimeOutException(String message) {
        super(HttpStatus.REQUEST_TIMEOUT, message);
    }
}
