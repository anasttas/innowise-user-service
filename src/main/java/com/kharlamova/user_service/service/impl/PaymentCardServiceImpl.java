package com.kharlamova.user_service.service.impl;

import com.kharlamova.user_service.dto.AskDto;
import com.kharlamova.user_service.dto.PaymentCardDto;
import com.kharlamova.user_service.entity.PaymentCard;
import com.kharlamova.user_service.entity.User;
import com.kharlamova.user_service.mapper.PaymentCardMapper;
import com.kharlamova.user_service.repository.PaymentCardRepository;
import com.kharlamova.user_service.repository.UserRepository;
import com.kharlamova.user_service.service.PaymentCardService;
import com.kharlamova.user_service.specification.PaymentCardSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {
    private final PaymentCardRepository paymentCardRepository;

    private final UserRepository userRepository;

    @Override
    public PaymentCardDto getPaymentCard(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment card not found"));

        return PaymentCardMapper.makePaymentCardDto(paymentCard);
    }

    @Override
    public Page<PaymentCardDto> getAllPaymentCard(String holder, Pageable pageable) {
        Specification<PaymentCard> specification = Specification
                .where(PaymentCardSpecification.hasHolderLike(holder));

        return paymentCardRepository.findAll(specification, pageable)
                .map(PaymentCardMapper::makePaymentCardDto);
    }

    @Override
    public PaymentCardDto createPaymentCard(PaymentCardDto paymentCardDto) {
        paymentCardRepository.findUserByNumber(paymentCardDto.getNumber())
                .ifPresent(foundPaymentCard -> {
                    throw new RuntimeException("Payment card already exists");
                });

        User user = userRepository.findById(paymentCardDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPaymentCards().size() >= 5) {
            throw new RuntimeException("User cannot have more than 5 payment cards");
        }

        PaymentCard paymentCard = PaymentCardMapper.makePaymentCard(paymentCardDto, user);

        paymentCard.setCreatedAt(LocalDateTime.now());
        paymentCard.setUpdatedAt(LocalDateTime.now());

        paymentCardRepository.save(paymentCard);

        return PaymentCardMapper.makePaymentCardDto(paymentCard);
    }

    @Transactional
    @Override
    public PaymentCardDto updatePaymentCard(Long id, PaymentCardDto paymentCardDto) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment card not found"));

        User user = userRepository.findById(paymentCardDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        paymentCard.setHolder(paymentCardDto.getHolder());
        paymentCard.setUser(user);
        paymentCard.setExpirationDate(paymentCardDto.getExpirationDate());
        paymentCard.setUpdatedAt(LocalDateTime.now());

        paymentCardRepository.save(paymentCard);

        return PaymentCardMapper.makePaymentCardDto(paymentCard);
    }

    @Transactional
    @Override
    public PaymentCardDto activatePaymentCard(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment card not found"));

        paymentCard.setActive(true);

        paymentCardRepository.save(paymentCard);

        return  PaymentCardMapper.makePaymentCardDto(paymentCard);
    }

    @Transactional
    @Override
    public PaymentCardDto deactivatePaymentCard(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment card not found"));

        paymentCard.setActive(false);

        paymentCardRepository.save(paymentCard);

        return  PaymentCardMapper.makePaymentCardDto(paymentCard);
    }

    @Transactional
    @Override
    public AskDto deletePaymentCard(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment card not found"));

        paymentCardRepository.deleteById(id);

        return AskDto.makeDefault(true);
    }

    @Override
    public Page<PaymentCardDto> getAllPaymentCardByUserId(Long userId, Pageable pageable) {
        Specification<PaymentCard> specification =
                PaymentCardSpecification.hasUserId(userId);

        return paymentCardRepository.findAll(specification, pageable)
                .map(PaymentCardMapper::makePaymentCardDto);
    }
}
