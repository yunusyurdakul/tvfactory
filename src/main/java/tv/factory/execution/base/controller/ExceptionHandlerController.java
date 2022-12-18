package tv.factory.execution.base.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tv.factory.execution.base.model.BaseResponse;

@ControllerAdvice
@Log4j2
public class ExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity handle(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.ok(BaseResponse.fail("Production failed for unknown reasons!"));
    }
}
