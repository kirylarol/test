package by.kirylarol.spendsculptor.controllers;

import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

/**
 * Base controller class that provides common functionality for all controllers
 * including authentication checks and standardized response handling.
 */
public abstract class BaseController {

    @Autowired
    protected Util util;

    /**
     * Executes an authenticated operation and handles common error scenarios
     * 
     * @param operation The operation to execute if authentication succeeds
     * @return ResponseEntity with the result or appropriate error response
     */
    protected <T> ResponseEntity<?> executeAuthenticatedOperation(Supplier<T> operation) {
        try {
            User user = util.getUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
            }
            
            T result = operation.get();
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Gets the current authenticated user or throws an exception if not authenticated
     * 
     * @return The authenticated user
     * @throws Exception if no user is authenticated
     */
    protected User getAuthenticatedUser() throws Exception {
        User user = util.getUser();
        if (user == null) {
            throw new Exception("User not authenticated");
        }
        return user;
    }
    
    /**
     * Creates a standardized success response
     * 
     * @param data The data to include in the response
     * @return ResponseEntity with standardized format
     */
    protected ResponseEntity<?> successResponse(Object data) {
        return ResponseEntity.ok(data);
    }
    
    /**
     * Creates a standardized error response
     * 
     * @param status The HTTP status code
     * @param message The error message
     * @return ResponseEntity with standardized error format
     */
    protected ResponseEntity<?> errorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(message);
    }
}
