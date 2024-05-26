package com.library.management.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDto {

    @Min(value = 1, message = "User id is mandatory and should be greater than 0")
    private int userId;

    @Min(value = 1, message = "Book id is mandatory and should be greater than 0")
    private int bookId;
}
