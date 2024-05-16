package com.library.management.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Getter @Setter String username;
    @Getter @Setter String password;
    @Getter @Setter String email;
}
