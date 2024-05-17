package com.library.management.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "title")})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Getter
    @Setter int id;
    @NotEmpty(message = "Title is mandatory")
    private @Getter
    @Setter String title;
    private @Getter
    @Setter String author;
    private @Getter
    @Setter Boolean isIssued;
    private @Getter
    @Setter String issuedBy;
}
