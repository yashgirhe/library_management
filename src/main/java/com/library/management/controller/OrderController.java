package com.library.management.controller;

import com.library.management.entities.Book;
import com.library.management.entities.Order;
import com.library.management.model.CustomUserDetail;
import com.library.management.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Tag(name = "Order", description = "Order API to issue book by user")
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @Operation(
            summary = "Issue book"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book issued successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid data or user already has a book issued or book is already issued",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found: Book/User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))})})
    @PostMapping("/public/order/{id}")
    public ResponseEntity<Order> issueBook(@AuthenticationPrincipal CustomUserDetail customUserDetail, @PathVariable("id") int bookId) {
        int userId = customUserDetail.getId();
        Order order = orderService.issueBook(userId, bookId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Return book"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book returned successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))}),
            @ApiResponse(responseCode = "404", description = "No book to return",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))})})
    @PostMapping("/public/order/")
    public ResponseEntity<String> returnBook(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        orderService.returnBook(customUserDetail.getId());
        return new ResponseEntity<>("Book returned successfully", HttpStatus.OK);
    }
}
