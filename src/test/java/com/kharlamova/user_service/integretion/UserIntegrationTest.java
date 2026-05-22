package com.kharlamova.user_service.integration;

import com.kharlamova.user_service.dto.UserDto;
import com.kharlamova.user_service.entity.User;
import com.kharlamova.user_service.repository.UserRepository;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.testcontainers.containers.GenericContainer;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldCreateUserAndSaveToDatabase() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/shop/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.name").value("Ivan"));

        assertThat(userRepository.findAll()).hasSize(1);
        User savedUser = userRepository.findAll().get(0);
        assertThat(savedUser.getName()).isEqualTo("Ivan");
        assertThat(savedUser.getEmail()).isEqualTo("ivan@mail.com");
    }

    @Test
    void shouldReturnUserById() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        mockMvc.perform(get("/shop/users/{user_id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@mail.com"));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        mockMvc.perform(get("/shop/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        UserDto updateDto = UserDto.builder()
                .name("Petr")
                .surname("Petrov")
                .email("petrov@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build();

        mockMvc.perform(patch("/shop/users/{user_id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Petr"));

        User updated = userRepository.findById(user.getId()).get();
        assertThat(updated.getName()).isEqualTo("Petr");
    }

    @Test
    void shouldDeleteUser() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        mockMvc.perform(delete("/shop/users/{user_id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").exists());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void shouldActivateUser() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(false)
                .build());

        mockMvc.perform(patch("/shop/users/activate/{user_id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        User updated = userRepository.findById(user.getId()).get();
        assertThat(updated.isActive()).isTrue();
    }

    @Test
    void shouldDeactivateUser() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        mockMvc.perform(patch("/shop/users/deactivate/{user_id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        User updated = userRepository.findById(user.getId()).get();
        assertThat(updated.isActive()).isFalse();
    }
}