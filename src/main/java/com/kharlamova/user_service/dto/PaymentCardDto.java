package com.kharlamova.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCardDto {
    private Long id;

    @NotNull(message = "User id should not be blank")
    private Long userId;

    @NotBlank(message = "Number should not be blank")
    @Pattern(regexp = "\\d{16}", message = "Number should be contain 16 digits")
    private String number;

    @Size(min = 3, max = 100, message = "Holder name should be between {min} and {max} characters")
    @NotBlank(message = "Holder should not be blank")
    private String holder;

    @Future(message = "Expiration date should be in the future")
    @NotNull(message = "Expiration date should not be null")
    private LocalDate expirationDate;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
