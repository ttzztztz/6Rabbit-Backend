package com.rabbit.backend.Utilities;

import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice(
        basePackages = {"com.rabbit.backend.Controller.*"},
        annotations = {Controller.class, RestController.class}
)
public class RabbitControllerAdvice {
    @ExceptionHandler(value = NotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFoundException(HttpServletRequest request, NotFoundException ex) {
        return GeneralResponse.generator(404, ex.getErrMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Map<String, Object> handleException(HttpServletRequest request, Exception ex) {
        return GeneralResponse.generator(500, ex.getMessage());
    }
}
