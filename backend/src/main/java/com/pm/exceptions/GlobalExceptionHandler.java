package com.pm.exceptions;

import com.pm.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PredictionException.class)
    public ResponseEntity<ErrorResponseDTO> handlePredictionException(PredictionException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                ex.getErrorCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceAccessException(ResourceAccessException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "EXTERNAL_SERVICE_UNAVAILABLE",
                "Prediction service is currently unavailable"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpClientError(HttpClientErrorException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "INVALID_REQUEST",
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(StockIngestionException.class)
    public ResponseEntity<ErrorResponseDTO> handleStockIngestionException(StockIngestionException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                ex.getErrorCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(PortfolioTransactionException.class)
    public ResponseEntity<ErrorResponseDTO> handlePortfolioTransactionException(PortfolioTransactionException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                ex.getErrorCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    @ExceptionHandler(StockCsvLoaderException.class)
    public ResponseEntity<ErrorResponseDTO> handleStockCsvLoaderException(StockCsvLoaderException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                ex.getErrorCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }
    @ExceptionHandler(StockPriceException.class)
    public ResponseEntity<ErrorResponseDTO> handleStockPriceException(StockPriceException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                ex.getErrorCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }
    @ExceptionHandler(StockSearchException.class)
    public ResponseEntity<String> handleStockSearchException(StockSearchException e) {
        // Return a custom error response based on the exception details
        return new ResponseEntity<>(
                "Error: " + e.getMessage() + " (Error Code: " + e.getErrorCode() + ")",
                HttpStatus.valueOf(e.getHttpStatus())
        );

    }
    @ExceptionHandler(StockServiceException.class)
    public ResponseEntity<String> handleStockServiceException(StockServiceException e) {
        // Return a custom error response based on the exception details
        return new ResponseEntity<>(
                "Error: " + e.getMessage() + " (Error Code: " + e.getErrorCode() + ")",
                HttpStatus.valueOf(e.getHttpStatus())
        );
    }
    @ExceptionHandler(SubscriptionException.class)
    public ResponseEntity<String> handleSubscriptionException(SubscriptionException e) {
        // Return a custom error response based on the exception details
        return new ResponseEntity<>(
                "Error: " + e.getMessage() + " (Error Code: " + e.getErrorCode() + ")",
                HttpStatus.valueOf(e.getHttpStatus())
        );
    }

}
