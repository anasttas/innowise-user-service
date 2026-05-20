package com.kharlamova.user_service.mapper;


import com.kharlamova.user_service.dto.PaymentCardDto;
import com.kharlamova.user_service.entity.PaymentCard;
import com.kharlamova.user_service.entity.User;

public class PaymentCardMapper {
    public static PaymentCardDto makePaymentCardDto (PaymentCard paymentCard) {
        return PaymentCardDto.builder()
                .id(paymentCard.getId())
                .userId(paymentCard.getUser().getId())
                .number(paymentCard.getNumber())
                .holder(paymentCard.getHolder())
                .expirationDate(paymentCard.getExpirationDate())
                .active(paymentCard.isActive())
                .createdAt(paymentCard.getCreatedAt())
                .updatedAt(paymentCard.getUpdatedAt())
                .build();
    }

    public static PaymentCard makePaymentCard (PaymentCardDto paymentCardDto, User user) {
        return PaymentCard.builder()
                .id(paymentCardDto.getId())
                .user(user)
                .number(paymentCardDto.getNumber())
                .holder(paymentCardDto.getHolder())
                .expirationDate(paymentCardDto.getExpirationDate())
                .active(paymentCardDto.isActive())
                .build();
    }
}
