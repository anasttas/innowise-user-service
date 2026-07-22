package com.kharlamova.user_service.mapper;

import com.kharlamova.user_service.dto.UserDto;
import com.kharlamova.user_service.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto makeUserDto (User user);

    User makeUser (UserDto userDto);
}
