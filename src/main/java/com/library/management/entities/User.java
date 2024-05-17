package com.library.management.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private @Getter
    @Setter int id;
    private @Getter
    @Setter String username;
    private @Getter
    @Setter String password;
    private @Getter
    @Setter String role;
    private @Getter
    @Setter int bookId;
}
