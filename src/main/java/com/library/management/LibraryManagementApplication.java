package com.library.management;

import com.library.management.entities.User;
import com.library.management.repository.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Library Management API",
                version = "1.0.0",
                description = "Library API documentation"
        ),
        servers = @Server(
                url = "http://localhost:8080/",
                description = "Library API"
        ))
public class LibraryManagementApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User user1 = new User();
        user1.setUsername("admin");
        user1.setPassword(this.passwordEncoder.encode("admin"));
        user1.setRole("ADMIN");
        Optional<User> userExist1 = Optional.ofNullable(this.userRepository.findByUsername("admin"));
        if (userExist1.isEmpty()) {
            this.userRepository.save(user1);
        }

        User user2 = new User();
        user2.setUsername("user1");
        user2.setPassword(this.passwordEncoder.encode("user1"));
        user2.setRole("USER");
        Optional<User> userExist2 = Optional.ofNullable(this.userRepository.findByUsername("user1"));
        if (userExist2.isEmpty()) {
            this.userRepository.save(user2);
        }
    }
}
