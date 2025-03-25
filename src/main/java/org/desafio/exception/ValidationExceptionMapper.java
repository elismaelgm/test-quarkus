package org.desafio.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<Violation> violations = exception.getConstraintViolations().stream()
                .map(this::toViolation)
                .collect(Collectors.toList());

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ValidationErrorResponse(violations))
                .build();
    }

    private Violation toViolation(ConstraintViolation<?> violation) {
        return new Violation(
                violation.getPropertyPath().toString().split("\\.")[2],
                violation.getMessage()
        );
    }

    public static class Violation {
        public String field;
        public String message;

        public Violation(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }

    public static class ValidationErrorResponse {
        public List<Violation> violations;

        public ValidationErrorResponse(List<Violation> violations) {
            this.violations = violations;
        }
    }
}