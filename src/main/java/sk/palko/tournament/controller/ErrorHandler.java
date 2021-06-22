package sk.palko.tournament.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import sk.palko.tournament.dto.ErrorMessageDto;
import sk.palko.tournament.exception.RestException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@EnableWebMvc
public class ErrorHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorMessageDto handleNoHandlerFound(final NoHandlerFoundException ex) {
    LOGGER.warn(ex.getMessage(), ex);
    return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ErrorMessageDto handleMethodNotSupported(final HttpRequestMethodNotSupportedException ex) {
    LOGGER.warn(ex.getMessage(), ex);
    return createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
  public ErrorMessageDto handleMessageNotReadable(final HttpMessageNotReadableException ex) {
    LOGGER.warn(ex.getMessage(), ex);
    return createErrorResponse(HttpStatus.PRECONDITION_FAILED, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessageDto handleInvalidRequestArgumentType(final MethodArgumentTypeMismatchException ex) {
    LOGGER.warn("Invalid request argument type", ex);
    return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessageDto handleInvalidRequestArgument(final ConstraintViolationException ex) {
    LOGGER.warn("Invalid request argument", ex);
    List<String> messages = ex.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    return createErrorResponse(HttpStatus.BAD_REQUEST, messages);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessageDto handleInvalidRequestBodyData(final MethodArgumentNotValidException ex) {
    LOGGER.warn("Invalid request body data", ex);
    List<String> messages = ex.getBindingResult().getAllErrors().stream()
        .map(ObjectError::getDefaultMessage)
        .collect(Collectors.toList());
    return createErrorResponse(HttpStatus.BAD_REQUEST, messages);
  }

  @ExceptionHandler(RestException.class)
  public ResponseEntity<ErrorMessageDto> handleRestException(RestException ex) {
    LOGGER.warn("An error occurred", ex);
    return new ResponseEntity<>(createErrorResponse(ex.getStatus(), ex.getMessage()), ex.getStatus());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorMessageDto handleException(Exception ex) {
    LOGGER.error("An unexpected error occurred", ex);
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  private ErrorMessageDto createErrorResponse(HttpStatus httpStatus, String message) {
    return new ErrorMessageDto(httpStatus.value(), httpStatus, message);
  }

  private ErrorMessageDto createErrorResponse(HttpStatus httpStatus, List<String> messages) {
    return new ErrorMessageDto(httpStatus.value(), httpStatus, messages);
  }

}
