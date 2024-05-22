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
public class AdminControlUserDto {

    @NotEmpty(message = "Username is mandatory")
    private String username;
    @NotEmpty(message = "Role is mandatory")
    private String role;
}
