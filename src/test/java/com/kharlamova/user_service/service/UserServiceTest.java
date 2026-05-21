package com.kharlamova.user_service.service;

import com.kharlamova.user_service.dto.AskDto;
import com.kharlamova.user_service.dto.UserDto;
import com.kharlamova.user_service.entity.User;
import com.kharlamova.user_service.exceptions.UserNotFoundException;
import com.kharlamova.user_service.repository.UserRepository;
import com.kharlamova.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUser_shouldReturnUserDto() {
        User user = new User();

        user.setId(1L);
        user.setName("Anastasiya");
        user.setSurname("Vladislavovna");
        user.setEmail("ananas@gmail.com");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals("Anastasiya", result.getName());

        verify(userRepository, times(1))
                .findById(1L);
    }

    @Test
    void getUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUser(1L)
        );

        verify(userRepository, times(1))
                .findById(1L);
    }

    @Test
    void createUser_shouldReturnUserDto() {
        UserDto dto = new UserDto();
        dto.setName("Anastasiya");
        dto.setSurname("Ivanova");
        dto.setEmail("test@mail.com");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));

        when(userRepository.findUserByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("Anastasiya", result.getName());

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateAndReturnUserDto() {
        User user = new User();

        user.setId(1L);
        user.setName("Old");
        user.setSurname("Name");

        UserDto dto = new UserDto();

        dto.setName("New");
        dto.setSurname("Surname");
        dto.setBirthDate(LocalDate.of(2001, 5, 5));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto result = userService.updateUser(1L, dto);

        assertEquals("New", result.getName());
        assertEquals("Surname", result.getSurname());

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void activateUser_shouldActivateUser() {
        User user = new User();

        user.setId(1L);
        user.setActive(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto result = userService.activateUser(1L);

        assertTrue(result.isActive());

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void deactivateUser_shouldDeactivateUser() {
        User user = new User();

        user.setId(1L);
        user.setActive(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto result = userService.deactivateUser(1L);

        assertFalse(result.isActive());

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        User user = new User();

        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1))
                .deleteById(1L);
    }
}
