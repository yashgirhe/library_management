package com.library.management.service;

import com.library.management.dto.AdminUpdateUserDto;
import com.library.management.dto.PostUserDto;
import com.library.management.dto.UserUpdateUserDto;
import com.library.management.dto.GetUserDto;
import com.library.management.entities.User;
import com.library.management.exceptionhandler.DuplicateEntryException;
import com.library.management.exceptionhandler.ResourceNotFoundException;
import com.library.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private GetUserDto convertToGetUserDto(User user) {
        GetUserDto getUserDto = new GetUserDto();
        getUserDto.setId(user.getId());
        getUserDto.setUsername(user.getUsername());
        getUserDto.setRole(user.getRole());
        // Check if the book object is null before accessing its properties
        if (user.getIssuedBook() != null) {
            getUserDto.setIssuedBook(user.getIssuedBook().getTitle());
        } else {
            getUserDto.setIssuedBook(null); // Or set a default value if necessary
        }
        return getUserDto;
    }

    public GetUserDto getUserByName(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with name: " + username);
        }
        String issuedBook = (user.getIssuedBook() != null) ? user.getIssuedBook().getTitle() : null;

        return convertToGetUserDto(user);
    }

    public List<GetUserDto> getAllUsers() {
        List<User> user = userRepository.findAll();
        return user.stream()
                .map(this::convertToGetUserDto)
                .collect(Collectors.toList());
    }

    public GetUserDto addUser(PostUserDto postUserDto) {
        Optional<User> userPresent = Optional.ofNullable(userRepository.findByUsername(postUserDto.getUsername()));
        if (userPresent.isPresent()) {
            throw new DuplicateEntryException("User with name '" + postUserDto.getUsername() + "' already exists.");
        }
        User user = new User();
        user.setUsername(postUserDto.getUsername());
        user.setPassword(passwordEncoder().encode(postUserDto.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return convertToGetUserDto(user);
    }

    public GetUserDto updateUserByAdmin(AdminUpdateUserDto userDto, String username) {
        GetUserDto getUserDto = new GetUserDto();
        User user = userRepository.findByUsername(username);
        //updated username passed should not be taken
        if (userRepository.findByUsername(userDto.getUsername()) != null && !userDto.getUsername().equals(username)) {
            throw new DuplicateEntryException("Username already exist");
        }
        user.setUsername(userDto.getUsername());
        user.setRole(userDto.getRole());
        if (!Arrays.asList("ADMIN", "USER").contains(userDto.getRole())) {
            throw new ResourceNotFoundException("Assigned role should be either ADMIN or USER");
        }
        userRepository.save(user);

        getUserDto.setId(user.getId());
        getUserDto.setUsername(user.getUsername());
        getUserDto.setRole(user.getRole());
        if (user.getIssuedBook() == null) {
            getUserDto.setIssuedBook(null);
        } else {
            getUserDto.setIssuedBook(user.getIssuedBook().getTitle());
        }
        return getUserDto;
    }

    public GetUserDto updateUserByUser(UserUpdateUserDto userDto, String username) {
        GetUserDto getUserDto = new GetUserDto();
        User user = userRepository.findByUsername(username);
        //updated username passed should not be taken
        if (userRepository.findByUsername(userDto.getUsername()) != null && !userDto.getUsername().equals(username)) {
            throw new DuplicateEntryException("Username already exist");
        }
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder().encode(userDto.getPassword()));
        userRepository.save(user);

        getUserDto.setId(user.getId());
        getUserDto.setUsername(user.getUsername());
        getUserDto.setRole(user.getRole());
        if (user.getIssuedBook() == null) {
            getUserDto.setIssuedBook(null);
        } else {
            getUserDto.setIssuedBook(user.getIssuedBook().getTitle());
        }
        return getUserDto;
    }

    public void deleteUserByName(String name) {
        userRepository.removeByUsername(name);
    }
}
