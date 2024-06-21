package com.library.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.dto.BookDto;
import com.library.management.exceptionhandler.DuplicateEntryException;
import com.library.management.exceptionhandler.GlobalExceptionHandler;
import com.library.management.exceptionhandler.ResourceNotFoundException;
import com.library.management.repository.UserRepository;
import com.library.management.service.BookService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testAddBook() throws Exception {
        BookDto bookDto = new BookDto(1, "Harry Potter", "J.K.R", null, false);
        when(bookService.addBook(any(BookDto.class))).thenReturn(bookDto);

        mockMvc.perform(post("/admin/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Harry Potter"));
    }

    @Test
    void testAddBook_duplicateEntry() throws Exception {
        BookDto bookDto = new BookDto(1, "Harry Potter", "J.K.R", null, false);
        when(bookService.addBook(any(BookDto.class))).thenThrow(new DuplicateEntryException("Conflict: Book with the given title already exists"));

        mockMvc.perform(post("/admin/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Conflict: Book with the given title already exists"));
    }

    @Test
    void testGetBookByName() throws Exception {
        String bookName = "Harry Potter";
        BookDto bookDto = new BookDto(1, "Harry Potter", "J.K.R", null, false);
        when(bookService.getBookByName(bookName)).thenReturn(bookDto);

        mockMvc.perform(get("/public/book/" + bookName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(bookName));
    }

    @Test
    void testGetBookByName_notFound() throws Exception {
        when(bookService.getBookByName(anyString())).thenThrow(new ResourceNotFoundException("Book not found"));

        mockMvc.perform(get("/public/book/NonExistingBook"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void testGetAllBooks() throws Exception {
        List<BookDto> books = Arrays.asList(
                new BookDto(1, "Harry Potter 1", "J.K.R", null, false),
                new BookDto(2, "Harry Potter 2", "J.K.R", null, false));
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/public/book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Harry Potter 1"));
    }

    @Test
    void testUpdateBook() throws Exception {
        String bookName = "Harry Potter";
        BookDto bookDto = new BookDto(1, "Harry Potter", "J.K.R", null, false);
        when(bookService.updateBook(eq(bookName), any(BookDto.class))).thenReturn(bookDto);

        mockMvc.perform(put("/admin/book/" + bookName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Harry Potter"));
    }

    @Test
    void testUpdateBook_bookNotFound() throws Exception {
        String bookName = "Harry Potter";
        BookDto bookDto = new BookDto(1, "Harry Potter", "J.K.R", null, false);
        when(bookService.updateBook(eq(bookName), any(BookDto.class))).thenThrow(new ResourceNotFoundException("Book not found with name: " + bookName));

        mockMvc.perform(put("/admin/book/" + bookName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with name: " + bookName));
    }

    @Test
    @Disabled
    void testUpdateBook_userAlreadyIssuedAnotherBook() throws Exception {
        String bookName = "Harry Potter";
        String userName = "user1";
        BookDto bookDto = new BookDto(1, "Harry Potter", "J.K.R", userName, false);
        doThrow(new IllegalArgumentException("User '" + userName + "' already issued a book.")).when(bookService).updateBook(bookName, bookDto);

        mockMvc.perform(put("/admin/book/"+bookName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User '" + userName + "' already issued a book."));
    }

    @Test
    void testDeleteBookByName() throws Exception {
        String bookName = "Harry Potter";
        doNothing().when(bookService).deleteByName(bookName);

        mockMvc.perform(delete("/admin/book/" + bookName)
                        .with(csrf()))
                .andExpect(status().isAccepted());
    }

    @Test
    void testDeleteBookByName_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Book not found")).when(bookService).getBookByName(anyString());

        mockMvc.perform(delete("/admin/book/nonExistingBook")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

}
