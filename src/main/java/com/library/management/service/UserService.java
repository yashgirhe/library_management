package com.library.management.service;

import com.library.management.dto.UserDto;
import com.library.management.entities.User;
import com.library.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public UserDto getUserByName(String username) {
        User user = userRepository.findByUsername(username);
        String issuedBook = (user.getIssuedBook() != null) ? user.getIssuedBook().getTitle() : null;

        return new UserDto(user.getId(), user.getUsername(), user.getRole(), issuedBook);
    }
}
