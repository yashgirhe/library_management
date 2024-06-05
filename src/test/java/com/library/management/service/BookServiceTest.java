package com.library.management.service;

import com.library.management.dto.BookDto;
import com.library.management.entities.Book;
import com.library.management.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class BookServiceTest {

    @Spy
    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBookByName_BookFound() {
        //Arrange
        String bookName = "Harry Potter";
        Book book = new Book(1, bookName, "J.K.R", false, null, null);
        BookDto bookDto = new BookDto(1, bookName, "J.K.R", null, false);
        when(bookRepository.findByTitle(bookName)).thenReturn(book);
        when(bookService.convertToBookDto(book)).thenReturn(bookDto);
        //Act
        BookDto result = bookService.getBookByName(bookName);
        //Assert
        assertNotNull(result);
        assertEquals(bookName, result.getTitle());
    }
}
