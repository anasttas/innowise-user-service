package com.kharlamova.user_service.service;

import com.kharlamova.user_service.dto.PaymentCardDto;
import com.kharlamova.user_service.entity.PaymentCard;
import com.kharlamova.user_service.entity.User;
import com.kharlamova.user_service.exceptions.PaymentCardNotFoundException;
import com.kharlamova.user_service.mapper.PaymentCardMapper;
import com.kharlamova.user_service.repository.PaymentCardRepository;
import com.kharlamova.user_service.repository.UserRepository;
import com.kharlamova.user_service.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentCardServiceTest {
    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Test
    void getPaymentCard_shouldReturnPaymentCardDto() {
        User user = new User();

        user.setId(1L);

        PaymentCard card = new PaymentCard();

        card.setId(1L);
        card.setHolder("IVAN IVANOV");
        card.setUser(user);

        when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        PaymentCardDto dto = new PaymentCardDto();
        dto.setHolder("IVAN IVANOV");

        when(paymentCardMapper.makePaymentCardDto(card))
                .thenReturn(dto);

        PaymentCardDto result = paymentCardService.getPaymentCard(1L);

        assertNotNull(result);

        assertEquals("IVAN IVANOV", result.getHolder());

        verify(paymentCardRepository, times(1))
                .findById(1L);
    }

    @Test
    void getPaymentCard_shouldThrowException_whenNotFound() {
        when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentCardNotFoundException.class,
                () -> paymentCardService.getPaymentCard(1L)
        );

        verify(paymentCardRepository, times(1))
                .findById(1L);
    }

    @Test
    void updatePaymentCard_shouldUpdateAndReturnDto() {
        PaymentCard card = new PaymentCard();

        card.setId(1L);
        card.setHolder("Old");

        User user = new User();
        user.setId(1L);

        PaymentCardDto dto = new PaymentCardDto();

        dto.setHolder("New");
        dto.setUserId(1L);

        when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(paymentCardRepository.save(any(PaymentCard.class)))
                .thenReturn(card);

        PaymentCardDto resultDto = new PaymentCardDto();
        resultDto.setHolder("New");

        when(paymentCardMapper.makePaymentCardDto(card))
                .thenReturn(resultDto);

        PaymentCardDto result = paymentCardService.updatePaymentCard(1L, dto);

        assertEquals("New", result.getHolder());

        verify(paymentCardRepository, times(1))
                .save(card);
    }

    @Test
    void activatePaymentCard_shouldActivate() {
        User user = new User();
        user.setId(1L);

        PaymentCard card = new PaymentCard();

        card.setId(1L);
        card.setActive(false);
        card.setUser(user);

        PaymentCardDto dto = new PaymentCardDto();
        dto.setActive(true);

        when(paymentCardMapper.makePaymentCardDto(card))
                .thenReturn(dto);

        when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        when(paymentCardRepository.save(any(PaymentCard.class)))
                .thenReturn(card);

        PaymentCardDto result = paymentCardService.activatePaymentCard(1L);

        assertTrue(result.isActive());

        verify(paymentCardRepository, times(1))
                .save(card);
    }

    @Test
    void deactivatePaymentCard_shouldDeactivate() {
        User user = new User();
        user.setId(1L);

        PaymentCard card = new PaymentCard();

        card.setId(1L);
        card.setActive(true);
        card.setUser(user);

        PaymentCardDto dto = new PaymentCardDto();
        dto.setActive(false);

        when(paymentCardMapper.makePaymentCardDto(card))
                .thenReturn(dto);

        when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        when(paymentCardRepository.save(any(PaymentCard.class)))
                .thenReturn(card);

        PaymentCardDto result = paymentCardService.deactivatePaymentCard(1L);

        assertFalse(result.isActive());

        verify(paymentCardRepository, times(1))
                .save(card);
    }

    @Test
    void deletePaymentCard_shouldDelete() {
        PaymentCard card = new PaymentCard();

        card.setId(1L);

        when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        paymentCardService.deletePaymentCard(1L);

        verify(paymentCardRepository, times(1))
                .deleteById(1L);
    }
}