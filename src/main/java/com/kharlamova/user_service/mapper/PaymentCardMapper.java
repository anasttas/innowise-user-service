package com.kharlamova.user_service.mapper;


import com.kharlamova.user_service.dto.PaymentCardDto;
import com.kharlamova.user_service.entity.PaymentCard;
import com.kharlamova.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {
    @Mapping(source = "user.id", target = "userId")
    PaymentCardDto makePaymentCardDto(PaymentCard paymentCard);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user", target = "user")
    @Mapping(target = "active", constant = "true")
    PaymentCard makePaymentCard(PaymentCardDto paymentCardDto, User user);
}
