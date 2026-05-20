package com.kharlamova.user_service.mapper;

import com.kharlamova.user_service.dto.UserDto;
import com.kharlamova.user_service.entity.User;

public class UserMapper {
    public static UserDto makeUserDto (User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .birthDate(user.getBirthDate())
                .email(user.getEmail())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static User makeUser (UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .birthDate(userDto.getBirthDate())
                .email(userDto.getEmail())
                .active(userDto.isActive())
                .build();
    }
}
