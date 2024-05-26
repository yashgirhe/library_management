package com.library.management.controller;

import com.library.management.dto.OrderDto;
import com.library.management.entities.Book;
import com.library.management.entities.Order;
import com.library.management.service.OrderService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping("/public/order")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDto orderDto) {
        Order order = orderService.createOrder(orderDto);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
