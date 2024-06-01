package com.library.management.service;

import com.library.management.entities.Book;
import com.library.management.entities.Order;
import com.library.management.entities.User;
import com.library.management.exceptionhandler.MultipleIssueException;
import com.library.management.exceptionhandler.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import com.library.management.repository.OrderRepository;
import com.library.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    public Order createOrder(int userId, int bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " +userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Check if the book is already issued
        if (book.getIsIssued()) {
            throw new MultipleIssueException("The book with id " + bookId + " is already issued.");
        }

        //check if user already issued any book
        if (user.getIssuedBook() != null) {
            throw new MultipleIssueException("User already issued a book");
        }

        Order order = new Order();
        order.setUser(user);
        order.setBook(book);
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);

        //update book entity
        book.setIsIssued(true);
        book.setUser(user);
        bookRepository.save(book);
        //update user entity
        user.setIssuedBook(book);
        userRepository.save(user);

        return order;
    }
}
