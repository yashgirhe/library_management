package com.library.management.service;

import com.library.management.dto.AdminControlUserDto;
import com.library.management.dto.UserControlUserDto;
import com.library.management.dto.UserDto;
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

    private UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole());
        // Check if the book object is null before accessing its properties
        if (user.getIssuedBook() != null) {
            userDto.setIssuedBook(user.getIssuedBook().getTitle());
        } else {
            userDto.setIssuedBook(null); // Or set a default value if necessary
        }
        return userDto;
    }

    private User convertDtoToUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder().encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        return user;
    }

    public UserDto getUserByName(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with name: " + username);
        }
        String issuedBook = (user.getIssuedBook() != null) ? user.getIssuedBook().getTitle() : null;

        return convertToUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        List<User> user = userRepository.findAll();
        return user.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    public User addUser(UserDto userDto) {
        Optional<User> userPresent = Optional.ofNullable(userRepository.findByUsername(userDto.getUsername()));
        if (userPresent.isPresent()) {
            throw new DuplicateEntryException("User with name '" + userDto.getUsername() + "' already exists.");
        }
        User user = convertDtoToUser(userDto);
        return userRepository.save(user);
    }

    public UserDto updateUserByAdmin(AdminControlUserDto userDto, String username) {
        UserDto userDto1 = new UserDto();
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

        userDto1.setId(user.getId());
        userDto1.setUsername(user.getUsername());
        userDto1.setPassword(user.getPassword());
        userDto1.setRole(user.getRole());
        if (user.getIssuedBook() == null) {
            userDto1.setIssuedBook(null);
        } else {
            userDto1.setIssuedBook(user.getIssuedBook().getTitle());
        }
        return userDto1;
    }

    public UserDto updateByUser(UserControlUserDto userDto, String username) {
        UserDto userDto1 = new UserDto();
        User user = userRepository.findByUsername(username);
        //updated username passed should not be taken
        if (userRepository.findByUsername(userDto.getUsername()) != null && !userDto.getUsername().equals(username)) {
            throw new DuplicateEntryException("Username already exist");
        }
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder().encode(userDto.getPassword()));
        userRepository.save(user);

        userDto1.setId(user.getId());
        userDto1.setUsername(user.getUsername());
        userDto1.setPassword(user.getPassword());
        userDto1.setRole(user.getRole());
        if (user.getIssuedBook() == null) {
            userDto1.setIssuedBook(null);
        } else {
            userDto1.setIssuedBook(user.getIssuedBook().getTitle());
        }
        return userDto1;
    }

    public void deleteUserByName(String name) {
        userRepository.removeByUsername(name);
    }
}
