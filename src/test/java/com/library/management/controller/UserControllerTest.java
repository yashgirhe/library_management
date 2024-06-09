package com.library.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.dto.GetUserDto;
import com.library.management.exceptionhandler.ResourceNotFoundException;
import com.library.management.repository.UserRepository;
import com.library.management.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetUserByName() throws Exception {
        String userName = "admin";
        GetUserDto getUserDto = new GetUserDto(1, userName, "ADMIN", null);
        when(userService.getUserByName(anyString())).thenReturn(getUserDto);

        mockMvc.perform(get("/public/user/" + userName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userName));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetUserByName_notFound() throws Exception {
        String userName = "admin";
        when(userService.getUserByName(anyString())).thenThrow(new ResourceNotFoundException("User not found with name: " + userName));

        mockMvc.perform(get("/public/user/" + userName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with name: " + userName));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllUsers() throws Exception {
        List<GetUserDto> userList = Arrays.asList(new GetUserDto(1, "admin", "ADMIN", null),
                new GetUserDto(2, "user1", "USER", null));
        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/public/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].username").value("user1"));
    }

//    @Test
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    public void addUser_success() throws Exception {
//        PostUserDto postUserDto = new PostUserDto("user1", "user1");
//        GetUserDto getUserDto = new GetUserDto(1,"user1","USER",null);
//        when(userService.addUser(postUserDto)).thenReturn(getUserDto);
//
//        mockMvc.perform(post("/public/user")
//                .contentType(MediaType.APPLICATION_JSON)
//                .with(csrf())
//                .content(objectMapper.writeValueAsString(postUserDto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.username").value("user1"));
//    }
}
