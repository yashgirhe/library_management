package com.library.management.service;

import com.library.management.dto.BookDto;
import com.library.management.entities.Book;
import com.library.management.entities.User;
import com.library.management.exceptionhandler.DuplicateEntryException;
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

    private BookDto convertToBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        // Check if the User object is null before accessing its properties
        if (book.getUser() != null) {
            bookDto.setUser(book.getUser().getUsername());
            bookDto.setIsIssued(true);
        } else {
            bookDto.setUser(null);
            bookDto.setIsIssued(false);// Or set a default value if necessary
        }
        return bookDto;
    }

    public BookDto addBook(BookDto bookDto) {
        //check if book already exist by title
        Optional<Book> bookExistByTitle = Optional.ofNullable(bookRepository.findByTitle(bookDto.getTitle()));
        if (bookExistByTitle.isPresent()) {
            throw new DuplicateEntryException("Book with title '" + bookDto.getTitle() + "' already exists.");
        }
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setUser(null);
        book.setIsIssued(false);
        bookRepository.save(book);
        return convertToBookDto(book);
    }


    public BookDto getBookByName(String name) {
        Book book = bookRepository.findByTitle(name);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found with name: " + name);
        } else {
            return convertToBookDto(book);
        }
    }

    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(this::convertToBookDto)
                .collect(Collectors.toList());
    }

    public BookDto updateBook(String name, BookDto bookDto) {
        Book bookToUpdate = bookRepository.findByTitle(name);
        bookToUpdate.setTitle(bookDto.getTitle());
        bookToUpdate.setAuthor(bookDto.getAuthor());
        // If username is not provided, remove existing user association
        if (bookDto.getUser() == null) {
            bookToUpdate.setUser(null);
            bookToUpdate.setIsIssued(false);
            return convertToBookDto(bookRepository.save(bookToUpdate));
        } else {
            // If username is provided
            User userExist = userRepository.findByUsername(bookDto.getUser());
            // If user does not exist
            if (userExist == null) {
                throw new ResourceNotFoundException("User not found with name: " + bookDto.getUser());
            }
            // If user is present
            //if user is not associated with a book
            if (userExist != null && userExist.getIssuedBook() == null) {
                bookToUpdate.setUser(userExist);
                bookToUpdate.setIsIssued(true);
                Book savedBook = bookRepository.save(bookToUpdate);
                userExist.setIssuedBook(savedBook);
                userRepository.save(userExist);
                return convertToBookDto(savedBook);
            }
            //if user is already associated with another book
            if (userExist != null && !userExist.getIssuedBook().getTitle().equals(bookDto.getTitle())) {
                throw new IllegalArgumentException("User '" + userExist.getUsername() + "' already issued a book.");
            }
            //if same user provided
            if (userExist != null && userExist.getIssuedBook().getTitle().equals(bookDto.getTitle())) {
                bookToUpdate.setUser(userExist);
                Book savedBook = bookRepository.save(bookToUpdate);
                userExist.setIssuedBook(savedBook);
                userRepository.save(userExist);
                return convertToBookDto(savedBook);
            }
        }
        return convertToBookDto(bookToUpdate);
    }

    public void deleteByName(String name) {
        bookRepository.removeByTitle(name);
    }

    public void deleteById(int id) {
        bookRepository.deleteById(id);
    }
}
