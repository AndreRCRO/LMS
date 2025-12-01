package com.app.emsx.exceptions;

import com.app.emsx.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🌐 GlobalExceptionHandler
 * ------------------------------------------------------------
 * Centraliza el manejo de errores de toda la aplicación EMSX.
 *
 * ✅ Unifica respuestas JSON con formato ApiResponse<T>.
 * ✅ Captura excepciones de validación, negocio y sistema.
 * ✅ Evita duplicación de código en controladores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ⚠️ Validaciones con @Valid (campos requeridos, formatos, etc.)
     * Retorna: HTTP 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Error de validación en uno o más campos")
                        .data(fieldErrors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * 📝 Errores de parseo JSON (formato incorrecto de fechas, tipos, etc.)
     * Retorna: HTTP 400 (Bad Request)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonParseError(HttpMessageNotReadableException ex) {
        String message = "Error en el formato de los datos enviados";
        String errorMessage = ex.getMessage();
        
        // Verificar si es una propiedad no reconocida
        Throwable cause = ex.getCause();
        
        // Manejar InvalidFormatException (cuando se envía un tipo incorrecto, ej: string en lugar de número)
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            String fieldName = ife.getPath().size() > 0 ? ife.getPath().get(ife.getPath().size() - 1).getFieldName() : null;
            String targetType = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "";
            
            // Detectar campos numéricos
            if (fieldName != null) {
                if (fieldName.equals("amount")) {
                    message = "El monto solo puede contener números y un punto decimal (ejemplo: 100.50). No se permiten letras ni símbolos.";
                } else if (fieldName.equals("totalCopies") || fieldName.equals("availableCopies") || fieldName.equals("borrowedCopies")) {
                    message = "Las copias solo pueden contener números (0-9). No se permiten letras ni símbolos.";
                } else if (fieldName.equals("bookId") || fieldName.equals("studentId") || fieldName.equals("authorId")) {
                    message = "El ID debe ser un número, no un texto. Solo se permiten números (0-9).";
                } else if (targetType.equals("Double") || targetType.equals("double") || targetType.equals("Integer") || targetType.equals("int") || targetType.equals("Long") || targetType.equals("long")) {
                    message = "El campo '" + fieldName + "' solo puede contener números. No se permiten letras ni símbolos.";
                } else {
                    message = "El campo '" + fieldName + "' tiene un formato incorrecto. Verifica el tipo de dato esperado.";
                }
            } else if (targetType.equals("Double") || targetType.equals("double") || targetType.equals("Integer") || targetType.equals("int") || targetType.equals("Long") || targetType.equals("long")) {
                message = "Error: Se envió un valor no numérico en un campo que requiere números. No se permiten letras ni símbolos.";
            } else {
                message = "Error en el formato de los datos. Verifica que los tipos de datos sean correctos.";
            }
        }
        // Manejar MismatchedInputException (similar a InvalidFormatException)
        else if (cause instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) cause;
            String fieldName = mie.getPath().size() > 0 ? mie.getPath().get(mie.getPath().size() - 1).getFieldName() : null;
            String targetType = mie.getTargetType() != null ? mie.getTargetType().getSimpleName() : "";
            
            if (fieldName != null) {
                if (fieldName.equals("amount")) {
                    message = "El monto solo puede contener números y un punto decimal (ejemplo: 100.50). No se permiten letras ni símbolos.";
                } else if (fieldName.equals("totalCopies") || fieldName.equals("availableCopies") || fieldName.equals("borrowedCopies")) {
                    message = "Las copias solo pueden contener números (0-9). No se permiten letras ni símbolos.";
                } else if (fieldName.equals("bookId") || fieldName.equals("studentId") || fieldName.equals("authorId")) {
                    message = "El ID debe ser un número, no un texto. Solo se permiten números (0-9).";
                } else if (targetType.equals("Double") || targetType.equals("double") || targetType.equals("Integer") || targetType.equals("int") || targetType.equals("Long") || targetType.equals("long")) {
                    message = "El campo '" + fieldName + "' solo puede contener números. No se permiten letras ni símbolos.";
                } else {
                    message = "El campo '" + fieldName + "' tiene un formato incorrecto. Verifica el tipo de dato esperado.";
                }
            } else if (targetType.equals("Double") || targetType.equals("double") || targetType.equals("Integer") || targetType.equals("int") || targetType.equals("Long") || targetType.equals("long")) {
                message = "Error: Se envió un valor no numérico en un campo que requiere números. No se permiten letras ni símbolos.";
            } else {
                message = "Error en el formato de los datos. Verifica que los tipos de datos sean correctos.";
            }
        }
        // Manejar UnrecognizedPropertyException (propiedades no reconocidas)
        else if (cause instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException upe = (UnrecognizedPropertyException) cause;
            String propertyName = upe.getPropertyName();
            
            // Mensajes específicos para campos comunes que no se pueden editar
            if (propertyName != null) {
                if (propertyName.equals("bookTitle") || propertyName.equals("studentName")) {
                    message = "No se puede modificar '" + propertyName + "'. Este campo es de solo lectura y no se puede editar.";
                } else {
                    message = "Campo desconocido: '" + propertyName + "'. Este campo no es válido para esta operación.";
                }
            } else {
                message = "Se enviaron campos que no son válidos para esta operación. Verifica los campos permitidos.";
            }
        }
        // Fallback: analizar el mensaje de error
        else if (errorMessage != null) {
            if (errorMessage.contains("LocalDate") || errorMessage.contains("date")) {
                message = "Error en el formato de fecha. Debe ser YYYY-MM-DD (ejemplo: 2025-11-25)";
            } else if (errorMessage.contains("Cannot deserialize") || errorMessage.contains("not a valid representation") || errorMessage.contains("not a valid")) {
                // Detectar si es un error de tipo numérico
                if (errorMessage.contains("amount") || errorMessage.contains("Amount")) {
                    message = "El monto solo puede contener números y un punto decimal (ejemplo: 100.50). No se permiten letras ni símbolos.";
                } else if (errorMessage.contains("totalCopies") || errorMessage.contains("availableCopies") || errorMessage.contains("borrowedCopies")) {
                    message = "Las copias solo pueden contener números (0-9). No se permiten letras ni símbolos.";
                } else if (errorMessage.contains("bookId") || errorMessage.contains("studentId") || errorMessage.contains("authorId")) {
                    message = "El ID debe ser un número, no un texto. Solo se permiten números (0-9).";
                } else if (errorMessage.contains("Double") || errorMessage.contains("Integer") || errorMessage.contains("Long") || errorMessage.contains("int") || errorMessage.contains("double") || errorMessage.contains("long")) {
                    message = "Error: Se envió un valor no numérico en un campo que requiere números. No se permiten letras ni símbolos.";
                } else {
                    message = "Error en el formato de los datos. Verifica que los tipos de datos sean correctos (números, fechas, etc.)";
                }
            } else if (errorMessage.contains("bookId") || errorMessage.contains("studentId")) {
                message = "Error: El ID del libro o estudiante debe ser un número, no un texto";
            } else {
                message = "Error en el formato de los datos enviados. Verifica que el JSON sea válido";
            }
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    /**
     * 🚫 Recursos no encontrados (404)
     * Ejemplo: buscar un empleado o departamento inexistente
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(ex.getMessage()));
    }

    /**
     * ⚖️ Violación de reglas de negocio (409)
     * Ejemplo: duplicar un registro único, violar restricción lógica, etc.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(BusinessRuleException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(ex.getMessage()));
    }

    /**
     * 🔒 Violación de integridad de datos (409)
     * Ejemplo: intentar guardar un email duplicado cuando hay constraint único en BD
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Error de integridad de datos";
        
        // Intentar extraer un mensaje más específico del error
        String errorMessage = ex.getMessage();
        if (errorMessage != null) {
            if (errorMessage.contains("email") || errorMessage.contains("EMAIL") || errorMessage.contains("Email")) {
                message = "El correo electrónico ya está registrado. Intenta con otro correo o inicia sesión.";
            } else if (errorMessage.contains("unique") || errorMessage.contains("UNIQUE") || errorMessage.contains("duplicate")) {
                message = "Ya existe un registro con estos datos. Verifica la información e intenta nuevamente.";
            } else if (errorMessage.contains("constraint") || errorMessage.contains("CONSTRAINT")) {
                message = "Los datos ingresados violan una restricción de la base de datos. Verifica la información.";
            }
        }
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(message));
    }

    /**
     * 🔐 Credenciales inválidas (401)
     * Ejemplo: contraseña incorrecta en el login
     * Nota: Por seguridad, siempre devolvemos el mismo mensaje genérico
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("Credenciales no válidas. Verifica tu contraseña."));
    }

    /**
     * 👤 Usuario no encontrado (401)
     * Ejemplo: intentar iniciar sesión con un email que no existe
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameNotFound(UsernameNotFoundException ex) {
        // Devolver el mensaje específico de la excepción
        String message = ex.getMessage();
        if (message != null && message.contains("no existe en el sistema")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(message));
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("El usuario no existe en el sistema. Verifica tu correo electrónico o crea una cuenta."));
    }

    /**
     * 💥 Errores genéricos no controlados (500)
     * Retorna: HTTP 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex, HttpServletRequest request) {
        // No imprimir stack trace para excepciones de autenticación conocidas
        if (!(ex instanceof BadCredentialsException) && 
            !(ex instanceof UsernameNotFoundException) &&
            !(ex instanceof BusinessRuleException) &&
            !(ex instanceof ResourceNotFoundException)) {
            ex.printStackTrace(); // 🔍 log útil en desarrollo; puede omitirse en producción
        }
        
        // Si es un RuntimeException relacionado con autenticación, devolver 401 con mensaje genérico
        if (ex instanceof RuntimeException && ex.getMessage() != null) {
            String message = ex.getMessage();
            if (message.contains("Usuario no encontrado") || message.contains("usuario no encontrado")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.fail("Credenciales no válidas. Verifica tu contraseña."));
            }
        }
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message("Error interno del servidor")
                        .data(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
