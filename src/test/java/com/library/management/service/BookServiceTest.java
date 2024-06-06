package com.library.management.service;

import com.library.management.dto.BookDto;
import com.library.management.entities.Book;
import com.library.management.exceptionhandler.DuplicateEntryException;
import com.library.management.exceptionhandler.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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

    @Test
    void testGetBookByName_BookNotFound() {
        //Arrange
        String bookName = "Unknown Book";
        when(bookRepository.findByTitle(bookName)).thenReturn(null);
        //Act
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.getBookByName(bookName);
        });
        //Assert
        assertEquals("Book not found with name: " + bookName, exception.getMessage());
    }

    @Test
    void testGetAllBooks() {
        //Arrange
        Book book1 = new Book(1, "Harry Potter 1", "J.K.R", false, null, null);
        Book book2 = new Book(2, "Harry Potter 2", "J.K.R", false, null, null);
        List<Book> books = Arrays.asList(book1, book2);

        BookDto bookDto1 = new BookDto(1, "Harry Potter 1", "J.K.R", null, false);
        BookDto bookDto2 = new BookDto(2, "Harry Potter 2", "J.K.R", null, false);
        List<BookDto> expectedBookDtos = Arrays.asList(bookDto1, bookDto2);

        when(bookService.convertToBookDto(book1)).thenReturn(bookDto1);
        when(bookService.convertToBookDto(book2)).thenReturn(bookDto2);
        when(bookRepository.findAll()).thenReturn(books);
        //Act
        List<BookDto> bookResult = bookService.getAllBooks();
        //Assert
        assertNotNull(bookResult);
        assertEquals(2, bookResult.size());
        assertEquals(expectedBookDtos, bookResult);
    }

    @Test
    void testAddBook_success() {
        //Arrange
        BookDto bookDto = new BookDto(1, "Harry Potter", "J.K.R", null, false);
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setUser(null);
        book.setIsIssued(false);

        BookDto expectedBookDto = new BookDto(1, "Harry Potter", "J.K.R", null, false);

        when(bookRepository.findByTitle(bookDto.getTitle())).thenReturn(null);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        doReturn(expectedBookDto).when(bookService).convertToBookDto(any(Book.class));
        //Act
        BookDto result = bookService.addBook(bookDto);
        //Assert
        assertNotNull(result);
        assertEquals(expectedBookDto.getTitle(), result.getTitle());
        assertEquals(expectedBookDto.getAuthor(), result.getAuthor());
    }

    @Test
    void testAddBook_DuplicateEntryException() {
        //Arrange
        BookDto bookDto = new BookDto(1, "Harry Potter", "J.K.R", null, false);
        Book book = new Book(1, "Harry Potter", "J.K.R", false, null, null);

        when(bookRepository.findByTitle(bookDto.getTitle())).thenReturn(book);
        //Act
        DuplicateEntryException exception = assertThrows(DuplicateEntryException.class, () -> {
            bookService.addBook(bookDto);
        });
        //Assert
        assertEquals("Book with title '" + bookDto.getTitle() + "' already exists.", exception.getMessage());
    }
}
