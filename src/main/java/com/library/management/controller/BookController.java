package com.library.management.controller;

import com.library.management.entities.Book;
import com.library.management.exceptionhandler.DuplicateTitleException;
import com.library.management.exceptionhandler.ResourceNotFoundException;
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
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        if (bookService.getBookByName(book.getTitle()) == null){
            Book savedBook = bookService.addBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        }
        throw new DuplicateTitleException("Book with title '" + book.getTitle() + "' already exists.");
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
    @GetMapping("/{name}")
    public ResponseEntity<Book> getBookByName(@PathVariable("name") String name) {
        Book book = bookService.getBookByName(name);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found with name: " + name);
        }
        return ResponseEntity.ok(book);
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
    @GetMapping("/")
    public ResponseEntity<List<Book>> getAllBooks() {
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
    public ResponseEntity<Book> updateBook(@PathVariable("name") String name, @Valid @RequestBody Book book) {
        Book isPresent = bookService.getBookByName(name);
        if (isPresent == null) {
            throw new ResourceNotFoundException("Book not found with name: " + name);
        } else {
            Book updatedBook = bookService.updateBook(name, book);
            return ResponseEntity.status(HttpStatus.OK).body(updatedBook);
        }
    }

    @Operation(
            summary = "Delete book by name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Book deleted Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Book not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
    })
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteBookByName(@PathVariable("name") String name) {
        Book book = bookService.getBookByName(name);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found with name: " + name);
        } else {
            bookService.deleteByName(name);
            return ResponseEntity.ok(HttpStatus.OK);
        }
    }

    @Operation(
            summary = "Delete book by id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Book deleted Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Book not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable("id") int id) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isEmpty() || book.get().getId() != id) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        } else {
            bookService.deleteById(id);
            return ResponseEntity.ok(HttpStatus.OK);
        }
    }
}
