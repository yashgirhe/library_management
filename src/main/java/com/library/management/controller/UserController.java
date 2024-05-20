package com.library.management.controller;

import com.library.management.dto.UserDto;
import com.library.management.entities.User;
import com.library.management.exceptionhandler.ResourceNotFoundException;
import com.library.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Retrieve user by name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByName(@PathVariable("username") String username) {
        UserDto user = userService.getUserByName(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with name: " + username);
        }
        return ResponseEntity.ok(user);
    }
}
