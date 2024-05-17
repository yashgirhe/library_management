package com.library.management.service;

import com.library.management.entities.Book;
import com.library.management.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Book getBookByName(String name) {
        return bookRepository.findByTitle(name);
    }

    public Optional<Book> getBookById(int id) {
        return bookRepository.findById(id);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book updateBook(String name, Book book) {
        Book bookToBeUpdated = bookRepository.findByTitle(name);
        bookToBeUpdated.setTitle(book.getTitle());
        bookToBeUpdated.setAuthor(book.getAuthor());
        bookToBeUpdated.setIsIssued(book.getIsIssued());
        bookToBeUpdated.setIssuedBy(book.getIssuedBy());
        return bookRepository.save(bookToBeUpdated);
    }

    public void deleteByName(String name) {
        bookRepository.removeByTitle(name);
    }

    public void deleteById(int id) {
        bookRepository.deleteById(id);
    }
}
