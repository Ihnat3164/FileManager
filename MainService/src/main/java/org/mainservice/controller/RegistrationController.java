package org.mainservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mainservice.DTO.UserRegistrationDTO;
import org.mainservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
@Tag(name = "Registration", description = "Controller for registration new users.")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Registers a new user in the system with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
    })
    @PostMapping
    public ResponseEntity<?> registerUser(@Parameter(description = "User registration details") @Valid @RequestBody UserRegistrationDTO userRegistrationDTO){
        return ResponseEntity.ok(userService.registerUser(userRegistrationDTO));
    }

}
