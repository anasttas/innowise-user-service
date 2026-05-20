package com.kharlamova.user_service.controller;

import com.kharlamova.user_service.dto.AskDto;
import com.kharlamova.user_service.dto.PaymentCardDto;
import com.kharlamova.user_service.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/cards")
public class PaymentCardController {
    private final PaymentCardService paymentCardService;

    @GetMapping("/{card_id}")
    public ResponseEntity<PaymentCardDto> getCardById(
            @PathVariable("card_id") Long id
    ) {
        return ResponseEntity.ok(paymentCardService.getPaymentCard(id));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardDto>> getAllCards(
            @RequestParam(required = false) String holder,
            Pageable pageable
    ) {
        Page<PaymentCardDto> paymentCardDtos = paymentCardService
                .getAllPaymentCard(holder, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentCardDtos);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<PaymentCardDto>> getAllCardsByUserId(
            @RequestParam(required = false) Long userId,
            Pageable pageable
    ) {
        Page<PaymentCardDto> paymentCardDtos = paymentCardService
                .getAllPaymentCardByUserId(userId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentCardDtos);
    }

    @PostMapping
    public ResponseEntity<PaymentCardDto> addCard(
            @RequestBody @Valid PaymentCardDto paymentCardDto
    ) {
        PaymentCardDto createdCard = paymentCardService
                .createPaymentCard(paymentCardDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCard);
    }

    @PatchMapping("/{card_id}")
    public ResponseEntity<PaymentCardDto> updateCard(@PathVariable("card_id") Long cardId,
                                              @RequestBody @Valid PaymentCardDto cardDto
    ) {
        PaymentCardDto updatePaymentCardDto = paymentCardService
                .updatePaymentCard(cardId, cardDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatePaymentCardDto);
    }

    @DeleteMapping("/{card_id}")
    public ResponseEntity<AskDto> deleteCard(@PathVariable("card_id") Long cardId) {
        AskDto deletedCardDto = paymentCardService.deletePaymentCard(cardId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(deletedCardDto);
    }

    @PatchMapping("/activate/{card_id}")
    public ResponseEntity<PaymentCardDto> activateCard(@PathVariable("card_id") Long cardId) {
        PaymentCardDto activatedCardDto = paymentCardService.activatePaymentCard(cardId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(activatedCardDto);
    }

    @PatchMapping("/deactivate/{card_id}")
    public ResponseEntity<PaymentCardDto> deactivateCard(@PathVariable("card_id") Long cardId) {
        PaymentCardDto deactivatedCardDto = paymentCardService.deactivatePaymentCard(cardId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(deactivatedCardDto);
    }
}
