package com.library.management.service;

import com.library.management.dto.BookDto;
import com.library.management.entities.Book;
import com.library.management.entities.User;
import com.library.management.exceptionhandler.DuplicateTitleException;
import com.library.management.exceptionhandler.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import com.library.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    public Book addBook(BookDto bookDto) {
        //check if book already exist by title
        Optional<Book> bookExistByTitle = Optional.ofNullable(bookRepository.findByTitle(bookDto.getTitle()));
        if (bookExistByTitle.isPresent()) {
            throw new DuplicateTitleException("Book with title '" + bookDto.getTitle() + "' already exists.");
        }
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsIssued(bookDto.getIsIssued());
        // If username is not provided, save the book without a user
        if (bookDto.getUsername() == null) {
            return bookRepository.save(book);
        } else {
            // If username is provided
            User user = userRepository.findByUsername(bookDto.getUsername());
            // If user does not exist
            if (user == null) {
                throw new ResourceNotFoundException("User not found with name: " + bookDto.getUsername());
            }
            // If user is present
            //check if user is already associated with a book
            User userExist = userRepository.findByUsername(bookDto.getUsername());
            Optional<Book> bookMapped = Optional.ofNullable(userExist.getIssuedBook());
            if (bookMapped.isPresent()) {
                throw new IllegalArgumentException("User '" + userExist.getUsername() + "' already issued a book.");
            }
            //else associate the user with the book
            book.setUser(user);
            Book savedBook = bookRepository.save(book);

            // Update the user's issued book and save the user entity
            user.setIssuedBook(savedBook);
            userRepository.save(user);

            return savedBook;
        }
    }

    public BookDto getBookByName(String name) {
        Book book = bookRepository.findByTitle(name);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found with name: " + name);
        } else {
            BookDto bookDto = new BookDto();
            bookDto.setId(book.getId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setIsIssued(book.getIsIssued());
            // Check if the User object is null before accessing its properties
            if (book.getUser() != null) {
                bookDto.setUsername(book.getUser().getUsername());
            } else {
                bookDto.setUsername(null); // Or set a default value if necessary
            }
            return bookDto;
        }
    }

    public Optional<Book> getBookById(int id) {
        return bookRepository.findById(id);
    }

    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(this::convertToBookDto)
                .collect(Collectors.toList());
    }

    private BookDto convertToBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsIssued(book.getIsIssued());
        // Check if the User object is null before accessing its properties
        if (book.getUser() != null) {
            bookDto.setUsername(book.getUser().getUsername());
        } else {
            bookDto.setUsername(null); // Or set a default value if necessary
        }
        return bookDto;
    }

    public Book updateBook(String name, Book book) {
        Book bookToBeUpdated = bookRepository.findByTitle(name);
        bookToBeUpdated.setTitle(book.getTitle());
        bookToBeUpdated.setAuthor(book.getAuthor());
        bookToBeUpdated.setIsIssued(book.getIsIssued());
        bookToBeUpdated.setUser(book.getUser());
        return bookRepository.save(bookToBeUpdated);
    }

    public void deleteByName(String name) {
        bookRepository.removeByTitle(name);
    }

    public void deleteById(int id) {
        bookRepository.deleteById(id);
    }
}
