package com.library.management.service;

import com.library.management.dto.AdminUpdateUserDto;
import com.library.management.dto.GetUserDto;
import com.library.management.dto.PostUserDto;
import com.library.management.dto.UserUpdateUserDto;
import com.library.management.entities.User;
import com.library.management.exceptionhandler.DuplicateEntryException;
import com.library.management.exceptionhandler.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import com.library.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Spy
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByName_userFound() {
        //Arrange
        String userName = "user1";
        User user = new User(1, userName, null, "user", null, null);
        when(userRepository.findByUsername(userName)).thenReturn(user);
        //Act
        GetUserDto result = userService.getUserByName(userName);
        //Assert
        assertNotNull(result);
        assertEquals(userName, result.getUsername());
    }

    @Test
    void testGetUserByName_userNotFound() {
        //Arrange
        String userName = "user1";
        when(userRepository.findByUsername(userName)).thenReturn(null);
        //Act
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByName(userName);
        });
        //Assert
        assertEquals("User not found with name: " + userName, exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        //Arrange
        User user1 = new User(1, "user1", null, "user", null, null);
        User user2 = new User(2, "user2", null, "user", null, null);
        List<User> userList = Arrays.asList(user1, user2);

        GetUserDto userDto1 = new GetUserDto(1, "user1", "user", null);
        GetUserDto userDto2 = new GetUserDto(2, "user2", "user", null);

        when(userService.convertToGetUserDto(user1)).thenReturn(userDto1);
        when(userService.convertToGetUserDto(user2)).thenReturn(userDto2);
        when(userRepository.findAll()).thenReturn(userList);
        //Act
        List<GetUserDto> result = userService.getAllUsers();
        //Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
    }

    @Test
    void testAddUser() {
        //Arrange
        PostUserDto postUserDto = new PostUserDto("user1", "user1");
        User user = new User();
        user.setUsername(postUserDto.getUsername());
        user.setPassword(postUserDto.getPassword());

        GetUserDto userDto1 = new GetUserDto(1, "user1", "user", null);

        when(userRepository.findByUsername(postUserDto.getUsername())).thenReturn(null);
        doReturn(userDto1).when(userService).convertToGetUserDto(any(User.class));
        when(userRepository.save(any(User.class))).thenReturn(user);
        //Act
        GetUserDto result = userService.addUser(postUserDto);
        //Assert
        assertNotNull(result);
        assertEquals(userDto1.getUsername(), result.getUsername());
        assertEquals(userDto1.getId(), result.getId());
    }

    @Test
    void testAddUser_userExist() {
        //Arrange
        PostUserDto postUserDto = new PostUserDto("user1", "user1");
        User user = new User();
        user.setUsername(postUserDto.getUsername());
        user.setPassword(postUserDto.getPassword());

        when(userRepository.findByUsername(postUserDto.getUsername())).thenReturn(user);
        //Act
        DuplicateEntryException exception = assertThrows(DuplicateEntryException.class, () -> {
            userService.addUser(postUserDto);
        });
        //Assert
        assertEquals("User with name '" + postUserDto.getUsername() + "' already exists.", exception.getMessage());
    }

    @Test
    void testUpdateUserByUser() {
        //Arrange
        String userName = "user1";
        UserUpdateUserDto userDto = new UserUpdateUserDto("updatedName", "updatedPass");

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());

        GetUserDto getUserDto = new GetUserDto(1, "updatedName", "user", null);

        when(userRepository.findByUsername(userName)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doReturn(getUserDto).when(userService).convertToGetUserDto(any(User.class));
        //Act
        GetUserDto result = userService.updateUserByUser(userDto, userName);
        //Assert
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
    }

    @Test
    void testUpdateUserByAdmin() {
        //Arrange
        String userName = "user1";
        AdminUpdateUserDto userDto = new AdminUpdateUserDto("updatedName", "USER");

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setRole(userDto.getRole());

        GetUserDto getUserDto = new GetUserDto(1, "updatedName", "USER", null);

        when(userRepository.findByUsername(userName)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doReturn(getUserDto).when(userService).convertToGetUserDto(any(User.class));
        //Act
        GetUserDto result = userService.updateUserByAdmin(userDto, userName);
        //Assert
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
    }

    @Test
    void testDeleteUser() {
        //Arrange
        String userName = "user1";
        //Act
        userService.deleteUserByName(userName);
        //Assert
        verify(userRepository, times(1)).removeByUsername(userName);
    }
}
