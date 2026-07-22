package com.kharlamova.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {
    private Long id;

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String email;

    @NotBlank(message = "Name should not be blank")
    @Size(min = 1, max = 50, message = "Name should be between {min} and {max} characters")
    private String name;

    @NotBlank(message = "Surname should not be blank")
    @Size(min = 1, max = 50, message = "Surname should be between {min} and {max} characters")
    private String surname;

    @Past(message = "Birth date should be in the past")
    @NotNull(message = "Birth date should not be null")
    private LocalDate birthDate;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
