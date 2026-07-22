package com.kharlamova.user_service.service;

import com.kharlamova.user_service.dto.UserDto;
import com.kharlamova.user_service.entity.User;
import com.kharlamova.user_service.exceptions.UserNotFoundException;
import com.kharlamova.user_service.mapper.UserMapper;
import com.kharlamova.user_service.repository.UserRepository;
import com.kharlamova.user_service.security.UserPrincipal;
import com.kharlamova.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Test
    void getUser_shouldReturnUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Anastasiya");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Anastasiya");

        UserPrincipal principal = new UserPrincipal(1L, "ADMIN");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.makeUserDto(user))
                .thenReturn(dto);

        UserDto result = userService.getUser(1L, principal);

        assertNotNull(result);
        assertEquals("Anastasiya", result.getName());

        verify(userRepository).findById(1L);
        verify(userMapper).makeUserDto(user);
    }

    @Test
    void getUser_shouldThrowException_whenUserNotFound() {
        UserPrincipal principal = new UserPrincipal(1L, "ADMIN");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUser(1L, principal)
        );

        verify(userRepository, times(1))
                .findById(1L);
    }

    @Test
    void createUser_shouldReturnUserDto() {
        UserDto dto = new UserDto();
        dto.setEmail("test@mail.com");

        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findUserByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        when(userMapper.makeUser(dto))
                .thenReturn(user);

        when(userMapper.makeUserDto(user))
                .thenReturn(dto);

        UserDto result = userService.createUser(dto);

        assertNotNull(result);

        verify(userRepository).save(user);
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

        when(userMapper.makeUserDto(user))
                .thenReturn(dto);

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

        UserDto dto = new UserDto();
        dto.setActive(true);

        when(userMapper.makeUserDto(user))
                .thenReturn(dto);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto result = userService.activateUser(1L);

        assertTrue(result.getActive());

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void deactivateUser_shouldDeactivateUser() {
        User user = new User();

        user.setId(1L);
        user.setActive(true);

        UserDto dto = new UserDto();
        dto.setActive(false);

        when(userMapper.makeUserDto(user))
                .thenReturn(dto);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto result = userService.deactivateUser(1L);

        assertFalse(result.getActive());

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
