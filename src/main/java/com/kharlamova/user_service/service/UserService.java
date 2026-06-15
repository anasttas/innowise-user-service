package com.kharlamova.user_service.service;

import com.kharlamova.user_service.dto.AskDto;
import com.kharlamova.user_service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto getUser(Long id);

    Page<UserDto> getAllUsers(String name, String surname, Pageable pageable);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    UserDto activateUser(Long id);

    UserDto deactivateUser(Long id);

    AskDto deleteUser(Long id);

    UserDto getUserByEmail(String email);
}
