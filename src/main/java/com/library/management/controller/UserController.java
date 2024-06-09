package com.library.management.controller;

import com.library.management.dto.AdminUpdateUserDto;
import com.library.management.dto.GetUserDto;
import com.library.management.dto.PostUserDto;
import com.library.management.dto.UserUpdateUserDto;
import com.library.management.entities.User;
import com.library.management.model.CustomUserDetail;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
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
    @GetMapping("/public/user/{username}")
    public ResponseEntity<GetUserDto> getUserByName(@PathVariable("username") String username) {
        GetUserDto user = userService.getUserByName(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
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
    @GetMapping("/public/user")
    public ResponseEntity<List<GetUserDto>> getAllUsers() {
        List<GetUserDto> list = userService.getAllUsers();
        return new ResponseEntity<>(list, HttpStatus.OK);
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
    @PostMapping("/public/user")
    public ResponseEntity<GetUserDto> addUser(@Valid @RequestBody PostUserDto postUserDto) {
        GetUserDto userAdded = userService.addUser(postUserDto);
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
    @PatchMapping("/admin/user/{username}")
    public ResponseEntity<GetUserDto> updateUserByAdmin(@Valid @RequestBody AdminUpdateUserDto adminUpdateUserDto, @PathVariable("username") String username) {
        userService.getUserByName(username);
        GetUserDto userUpdated = userService.updateUserByAdmin(adminUpdateUserDto, username);
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
    @PatchMapping("/user/user/{username}")
    public ResponseEntity<GetUserDto> updateByUser(@Valid @RequestBody UserUpdateUserDto userUpdateUserDto, @PathVariable("username") String username) {
        userService.getUserByName(username);
        GetUserDto userUpdated = userService.updateUserByUser(userUpdateUserDto, username);
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete user by name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
    })
    @DeleteMapping("/admin/user/{username}")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable("username") String username) {
        userService.getUserByName(username);
        userService.deleteUserByName(username);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/public/current-user")
    public ResponseEntity<CustomUserDetail> getCurrentUser(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        return new ResponseEntity<>(customUserDetail, HttpStatus.OK);
    }
}
