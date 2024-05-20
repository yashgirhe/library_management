package com.library.management.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookDto {
    private int id;

    @NotEmpty(message = "Title is mandatory")
    private String title;

    private String author;
    private Boolean isIssued;
    private String username;
}
