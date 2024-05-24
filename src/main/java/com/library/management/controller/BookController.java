package com.library.management.controller;

import com.library.management.dto.BookDto;
import com.library.management.entities.Book;
import com.library.management.service.BookService;
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
@Tag(name = "Book", description = "Perform CRUD operations on books")
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
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid data or user already has a book issued",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found: User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict: Book with the given title already exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))})})
    @PostMapping("/admin/book")
    public ResponseEntity<BookDto> addBook(@Valid @RequestBody BookDto bookDto) {
        BookDto savedBook = bookService.addBook(bookDto);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
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
            @ApiResponse(responseCode = "404",
                    description = "Book not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
    })
    @GetMapping("/public/book/{name}")
    public ResponseEntity<BookDto> getBookByName(@PathVariable("name") String name) {
        BookDto book = bookService.getBookByName(name);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve all books"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Books found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))})
    })
    @GetMapping("/public/book")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<BookDto> list = bookService.getAllBooks();
        return new ResponseEntity<>(list, HttpStatus.OK);
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
    @PutMapping("/admin/book/{name}")
    public ResponseEntity<BookDto> updateBook(@PathVariable("name") String name, @Valid @RequestBody BookDto book) {
        bookService.getBookByName(name);
        BookDto updatedBook = bookService.updateBook(name, book);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete book by name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Book deleted Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Book not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
    })
    @DeleteMapping("/admin/book/{name}")
    public ResponseEntity<?> deleteBookByName(@PathVariable("name") String name) {
        bookService.getBookByName(name);
        bookService.deleteByName(name);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
