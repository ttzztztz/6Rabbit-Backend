package com.rabbit.backend.Utilities;

import com.rabbit.backend.Utilities.Exceptions.NotEnoughCreditsException;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import io.jsonwebtoken.ExpiredJwtException;
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
        return GeneralResponse.generate(404, ex.getErrMessage());
    }

    @ExceptionHandler(value = NotEnoughCreditsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotEnoughCreditsException(HttpServletRequest request, NotEnoughCreditsException ex) {
        return GeneralResponse.generate(ex.getCode(), ex.getErrMessage());
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleExpiredJWTException(HttpServletRequest request, ExpiredJwtException ex) {
        return GeneralResponse.generate(400, "JWT Expired.");
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(HttpServletRequest request, Exception ex) {
        return GeneralResponse.generate(500, ex.getMessage());
    }
}
