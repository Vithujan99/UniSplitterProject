package splitter.api;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandlerAdvice {

  @ExceptionHandler(InvalidDefinitionException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Long> handleException(InvalidDefinitionException exception) {
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }
}
