package com.kharlamova.user_service.service.impl;

import com.kharlamova.user_service.dto.AskDto;
import com.kharlamova.user_service.dto.UserDto;
import com.kharlamova.user_service.entity.User;
import com.kharlamova.user_service.exceptions.UserAlreadyExistsException;
import com.kharlamova.user_service.exceptions.UserNotFoundException;
import com.kharlamova.user_service.mapper.UserMapper;
import com.kharlamova.user_service.repository.UserRepository;
import com.kharlamova.user_service.security.UserPrincipal;
import com.kharlamova.user_service.service.UserService;
import com.kharlamova.user_service.specification.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserDto getUser(Long id, UserPrincipal principal) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!principal.isAdmin()
                && !user.getId().equals(principal.getUserId())) {
            throw new AccessDeniedException("Access denied");
        }

        return userMapper.makeUserDto(user);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public UserDto getUserByEmail(String email, UserPrincipal userPrincipal) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!userPrincipal.isAdmin()
                && !user.getId().equals(userPrincipal.getUserId())) {
            throw new AccessDeniedException("Access denied");
        }

        return userMapper.makeUserDto(user);
    }

    @Override
    public Page<UserDto> getAllUsers(String name, String surname, Pageable pageable) {
        Specification<User> specification = Specification
                .where(UserSpecification.hasNameLike(name))
                .and(UserSpecification.hasSurnameLike(surname));

        return userRepository.findAll(specification, pageable)
                .map(userMapper::makeUserDto);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userRepository.findUserByEmail(userDto.getEmail())
            .ifPresent(foundUser -> {
                throw new UserAlreadyExistsException("User already exists");
            });

        User user = userMapper.makeUser(userDto);

        user.setActive(true);

        userRepository.save(user);

        return userMapper.makeUserDto(user);
    }

    @Transactional
    @Override
    @CachePut(value = "users", key = "#id")
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setBirthDate(userDto.getBirthDate());

        userRepository.save(user);

        return userMapper.makeUserDto(user);
    }

    @Transactional
    @Override
    @CachePut(value = "users", key = "#id")
    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setActive(true);

        userRepository.save(user);

        return userMapper.makeUserDto(user);
    }

    @Transactional
    @Override
    @CachePut(value = "users", key = "#id")
    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setActive(false);

        userRepository.save(user);

        return userMapper.makeUserDto(user);
    }

    @Transactional
    @Override
    @CacheEvict(value = "users", key = "#id")
    public AskDto deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.deleteById(id);

        return AskDto.makeDefault(true);
    }
}
