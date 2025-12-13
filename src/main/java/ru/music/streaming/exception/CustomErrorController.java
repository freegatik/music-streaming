package ru.music.streaming.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<GlobalExceptionHandler.ErrorResponse> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = 500;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        String errorMessage = "Внутренняя ошибка сервера";
        if (message != null) {
            errorMessage = message.toString();
        } else if (exception instanceof Exception) {
            errorMessage = ((Exception) exception).getMessage();
        }

        GlobalExceptionHandler.ErrorResponse error = new GlobalExceptionHandler.ErrorResponse(
                LocalDateTime.now(),
                statusCode,
                HttpStatus.valueOf(statusCode).getReasonPhrase(),
                errorMessage
        );

        return new ResponseEntity<>(error, HttpStatus.valueOf(statusCode));
    }
}

