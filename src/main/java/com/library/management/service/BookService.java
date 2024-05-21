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

    public Book addBook(BookDto bookDto) {
        //check if book already exist by title
        Optional<Book> bookExistByTitle = Optional.ofNullable(bookRepository.findByTitle(bookDto.getTitle()));
        if (bookExistByTitle.isPresent()) {
            throw new DuplicateEntryException("Book with title '" + bookDto.getTitle() + "' already exists.");
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

    public Optional<BookDto> getBookById(int id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        } else {
            BookDto bookDto = new BookDto();
            bookDto.setId(book.get().getId());
            bookDto.setTitle(book.get().getTitle());
            bookDto.setAuthor(book.get().getAuthor());
            bookDto.setIsIssued(book.get().getIsIssued());
            // Check if the User object is null before accessing its properties
            if (book.get().getUser() != null) {
                bookDto.setUsername(book.get().getUser().getUsername());
            } else {
                bookDto.setUsername(null); // Or set a default value if necessary
            }
            return Optional.of(bookDto);
        }
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

    public BookDto updateBook(String name, BookDto bookDto) {
        Book bookToUpdate = bookRepository.findByTitle(bookDto.getTitle());
        bookToUpdate.setTitle(bookDto.getTitle());
        bookToUpdate.setAuthor(bookDto.getAuthor());
        bookToUpdate.setIsIssued(bookDto.getIsIssued());
        // If username is not provided, remove existing user association
        if (bookDto.getUsername() == null) {
            bookToUpdate.setUser(null);
            return convertToBookDto(bookRepository.save(bookToUpdate));
        } else {
            // If username is provided
            User user = userRepository.findByUsername(bookDto.getUsername());
            // If user does not exist
            if (user == null) {
                throw new ResourceNotFoundException("User not found with name: " + bookDto.getUsername());
            }
            // If user is present
            User userExist = userRepository.findByUsername(bookDto.getUsername());
            //if user is not associated with a book
            if (userExist != null && userExist.getIssuedBook() == null) {
                bookToUpdate.setUser(user);
                Book savedBook = bookRepository.save(bookToUpdate);
                user.setIssuedBook(savedBook);
                userRepository.save(user);
                return convertToBookDto(savedBook);
            }
            //if user is already associated with another book
            if (userExist != null && !userExist.getIssuedBook().getTitle().equals(bookDto.getTitle())) {
                throw new IllegalArgumentException("User '" + userExist.getUsername() + "' already issued a book.");
            }
            //if same user provided
            if (userExist != null && userExist.getIssuedBook().getTitle().equals(bookDto.getTitle())) {
                bookToUpdate.setUser(user);
                Book savedBook = bookRepository.save(bookToUpdate);
                user.setIssuedBook(savedBook);
                userRepository.save(user);
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
