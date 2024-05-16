package com.library.management.controller;

import com.library.management.entities.Book;
import com.library.management.model.ErrorResponse;
import com.library.management.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/book")
@Validated
@Tag(name = "BookController", description = "Perform CRUD operations on books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(
            summary = "Add new book"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PostMapping("/")
    public ResponseEntity<?> addBook(@Valid @RequestBody Book book) {
        try {
            Book savedBook = bookService.addBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (ConstraintViolationException e) {
            ErrorResponse errorResponse = new ErrorResponse("Invalid input");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Operation(
            summary = "Retrieve book by book name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Book not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
    })
    @GetMapping("/{name}")
    public ResponseEntity<?> getBookByName(@PathVariable("name") String name) {
        Optional<Book> book = Optional.ofNullable(bookService.getBookByName(name));
        if (book.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        return ResponseEntity.ok(book);
    }

    @Operation(
            summary = "Retrieve all books from database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Books found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No book found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))})
    })
    @GetMapping("/")
    public ResponseEntity<?> getAllBooks() {
        List<Book> list = bookService.getAllBooks();
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "Update book by name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book Updated Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Book not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
    })
    @PutMapping("/{name}")
    public ResponseEntity<?> updateBook(@Valid @PathVariable("name") String name, @RequestBody Book book) {
        Optional<Book> isPresent = Optional.ofNullable(bookService.getBookByName(name));
        if (!isPresent.isEmpty()) {
            Book updatedBook = bookService.updateBook(name, book);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedBook);
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @Operation(
            summary = "Delete book by name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Student deleted Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Book not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
    })
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteBook(@PathVariable("name") String name) {
        Optional<Book> isPresent = Optional.ofNullable(bookService.getBookByName(name));
        if (!isPresent.isEmpty()) {
            bookService.deleteByName(name);
            return ResponseEntity.status(HttpStatus.OK).body("book removed");
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @DeleteMapping("/id/{id}")
    public void deleteBookById(@PathVariable("id") int id) {
        bookService.deleteById(id);
    }
}
