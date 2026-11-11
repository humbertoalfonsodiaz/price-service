package com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web.error;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;
import com.inditex.ecommerce.priceservice.domain.exception.PriceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PriceNotFoundException.class)
    public ProblemDetail handlePriceNotFoundException(PriceNotFoundException ex, HttpServletRequest request) {
        log.info("Price not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://api.inditex.com/errors/price-not-found"));
        problem.setTitle("Price Not Found");
        problem.setInstance(URI.create(request.getRequestURI()));
        enrichWithMetadata(problem);
        return problem;
    }

    @ExceptionHandler(DomainValidationException.class)
    public ProblemDetail handleDomainValidationException(DomainValidationException ex, HttpServletRequest request) {
        log.warn("Domain validation error: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Bad Request");
        problem.setInstance(URI.create(request.getRequestURI()));
        enrichWithMetadata(problem);
        return problem;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Missing Required Parameter");
        problem.setInstance(URI.create(request.getRequestURI()));
        enrichWithMetadata(problem);
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String detail = String.format("Parameter '%s' value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        log.warn("Type mismatch error: {}", detail);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle("Parameter Type Mismatch");
        problem.setInstance(URI.create(request.getRequestURI()));
        enrichWithMetadata(problem);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("Constraint violation: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Constraint Violation");
        problem.setInstance(URI.create(request.getRequestURI()));
        enrichWithMetadata(problem);
        return problem;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        log.debug("Resource not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setInstance(URI.create(request.getRequestURI()));
        enrichWithMetadata(problem);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled internal server error: ", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred. Please contact support with the correlation ID.");
        problem.setTitle("Internal Server Error");
        problem.setInstance(URI.create(request.getRequestURI()));
        enrichWithMetadata(problem);
        return problem;
    }

    private void enrichWithMetadata(ProblemDetail problem) {
        String correlationId = MDC.get("X-Correlation-Id");
        if (correlationId != null) {
            problem.setProperty("correlationId", correlationId);
        }
        problem.setProperty("timestamp", Instant.now().toString());
    }
}
