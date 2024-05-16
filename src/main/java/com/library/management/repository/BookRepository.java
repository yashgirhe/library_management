package com.library.management.repository;

import com.library.management.entities.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,Integer> {
    public Book findByTitle(String title);
    @Transactional
    public void removeByTitle(String title);
}
