package com.library.management.controller;

import com.library.management.dto.AdminControllUserDto;
import com.library.management.dto.UserControllUserDto;
import com.library.management.dto.UserDto;
import com.library.management.entities.User;
import com.library.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/admin/user")
@Tag(name = "User", description = "Perform CRUD operations on user")
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
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Retrieve all users"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))})
    })
    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> list = userService.getAllUsers();
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "Add new user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid data",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict: User with the given name already exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))})})
    @PostMapping("/")
    public ResponseEntity<User> addUser(@Valid @RequestBody UserDto userDto) {
        User userAdded = userService.addUser(userDto);
        return new ResponseEntity<>(userAdded, HttpStatus.CREATED);
    }

    //used by admin to change username and role only
    @Operation(
            summary = "Update username and role by admin"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User Updated Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
    })
    @PatchMapping("/p1/{username}")
    public ResponseEntity<UserDto> updateUserByAdmin(@Valid @RequestBody AdminControllUserDto adminControllUserDto, @PathVariable("username") String username) {
        userService.getUserByName(username);
        UserDto userUpdated = userService.updateUserByAdmin(adminControllUserDto, username);
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

    //used by user to change username and password only
    @Operation(
            summary = "Update username and password by user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User Updated Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
    })
    @PatchMapping("/p2/{username}")
    public ResponseEntity<UserDto> updateByUser(@Valid @RequestBody UserControllUserDto userControllUserDto, @PathVariable("username") String username) {
        userService.getUserByName(username);
        UserDto userUpdated = userService.updateByUser(userControllUserDto, username);
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete user by name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "User deleted Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable("username") String username) {
        userService.getUserByName(username);
        userService.deleteUserByName(username);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
