package com.kharlamova.user_service.controller;

import com.kharlamova.user_service.dto.AskDto;
import com.kharlamova.user_service.dto.UserDto;
import com.kharlamova.user_service.security.UserPrincipal;
import com.kharlamova.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{user_id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("user_id") Long id,
                                               Authentication authentication
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(userService.getUser(id, principal));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email,
                                                  Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(userService.getUserByEmail(email, principal));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            Pageable pageable
    ) {
        Page<UserDto> userDtos = userService.getAllUsers(name, surname, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtos);
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    @PatchMapping("/{user_id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("user_id") Long userId,
                              @RequestBody @Valid UserDto userDto
    ) {
        UserDto updatedUserDto = userService.updateUser(userId, userDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUserDto);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    @DeleteMapping("/{user_id}")
    public ResponseEntity<AskDto> deleteUser(@PathVariable("user_id") Long userId) {
        AskDto deletedUserDto = userService.deleteUser(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(deletedUserDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate/{user_id}")
    public ResponseEntity<UserDto> activateUser(@PathVariable("user_id") Long userId) {
        UserDto activatedUserDto = userService.activateUser(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(activatedUserDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate/{user_id}")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable("user_id") Long userId) {
        UserDto deactivatedUserDto = userService.deactivateUser(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(deactivatedUserDto);
    }
}
