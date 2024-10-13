package org.mainservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mainservice.model.User;
import org.mainservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Admin", description = "Controller for performing CRUD operations on user accounts by administrators.")
public class AdminController {

    private final UserService userService;

    @Operation(summary = "Get all users", description = "Returns a list of all users in the system.")
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @Operation(summary = "Create a new user", description = "Creates a new user account with the provided information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
    })
    @PostMapping
    public ResponseEntity<?> createUser(
            @Parameter(description = "User details for creating a new user.")
            @Valid @RequestBody User user){
        return userService.createUser(user);
    }

    @Operation(summary = "Edit an existing user", description = "Updates the details of an existing user based on their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully edited"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    @PatchMapping
    public ResponseEntity<?> editUser(
            @Parameter(description = "ID of the user to be edited.")
            @RequestParam(value= "id") Long id,
            @Parameter(description = "Updated user information.")
            @Valid @RequestBody User user) throws IllegalAccessException {
        return userService.editUserById(id, user);
    }

    @Operation(summary = "Delete a user", description = "Deletes a user from the system based on their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    @DeleteMapping
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID of the user to be deleted.")
            @RequestParam(value = "id" ,required = false) Long id){
        return userService.deleteUserById(id);
    }

}
