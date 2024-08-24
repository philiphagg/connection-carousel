package hagg.philip.connectioncarousel.domain;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.BAD_REQUEST, reason = "No active strategy")
public class NoActiveStrategyException extends RuntimeException {
    public NoActiveStrategyException() {
        super("No active strategy");
    }
}
