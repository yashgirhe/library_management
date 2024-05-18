package com.library.management.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Component
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private
    @Setter int id;

    @NotEmpty(message = "Username is mandatory")
    private
    @Setter String username;
    @NotEmpty(message = "Password is mandatory")
    private
    @Setter String password;

    private
    @Setter String role;

    @OneToOne(mappedBy = "user")
    private
    @Setter Book issuedBook;
}
