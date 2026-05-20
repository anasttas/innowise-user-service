package com.kharlamova.user_service.service;

import com.kharlamova.user_service.dto.AskDto;
import com.kharlamova.user_service.dto.PaymentCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentCardService {
    PaymentCardDto getPaymentCard(Long id);

    Page<PaymentCardDto> getAllPaymentCard(String hold, Pageable pageable);

    Page<PaymentCardDto> getAllPaymentCardByUserId(Long userId, Pageable pageable);

    PaymentCardDto createPaymentCard(PaymentCardDto PaymentCardDto);

    PaymentCardDto updatePaymentCard(Long id, PaymentCardDto PaymentCardDto);

    PaymentCardDto activatePaymentCard(Long id);

    PaymentCardDto deactivatePaymentCard(Long id);

    AskDto deletePaymentCard(Long id);
}
