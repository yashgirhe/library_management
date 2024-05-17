package com.library.management.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
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
