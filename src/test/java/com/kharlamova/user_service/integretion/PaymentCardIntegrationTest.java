package com.kharlamova.user_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kharlamova.user_service.dto.PaymentCardDto;
import com.kharlamova.user_service.entity.PaymentCard;
import com.kharlamova.user_service.entity.User;
import com.kharlamova.user_service.repository.PaymentCardRepository;
import com.kharlamova.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentCardIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("TRUNCATE TABLE payment_cards RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldCreateCardAndSaveToDatabase() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        PaymentCardDto dto = PaymentCardDto.builder()
                .holder("Ivan Ivanov")
                .userId(user.getId())
                .number("1111222233334444")
                .expirationDate(LocalDate.of(2030, 12, 31))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/shop/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holder").value("Ivan Ivanov"));

        assertThat(paymentCardRepository.findAll()).hasSize(1);
        PaymentCard saved = paymentCardRepository.findAll().get(0);

        assertThat(saved.getHolder()).isEqualTo("Ivan Ivanov");
        assertThat(saved.getNumber()).isEqualTo("1111222233334444");
    }

    @Test
    void shouldReturnCardById() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        PaymentCard card = paymentCardRepository.save(PaymentCard.builder()
                .holder("Ivan Ivanov")
                .number("1111222233334444")
                .user(user)
                .expirationDate(LocalDate.of(2030, 12, 31))
                .active(true)
                .build());

        mockMvc.perform(get("/shop/cards/{card_id}", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("Ivan Ivanov"))
                .andExpect(jsonPath("$.number").value("1111222233334444"));
    }

    @Test
    void shouldReturnAllCards() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        paymentCardRepository.save(PaymentCard.builder()
                .holder("Ivan Ivanov")
                .number("1111222233334444")
                .user(user)
                .expirationDate(LocalDate.of(2030, 12, 31))
                .active(true)
                .build());

        mockMvc.perform(get("/shop/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void shouldReturnCardsByUserId() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        PaymentCard card = paymentCardRepository.save(PaymentCard.builder()
                .holder("Ivan Ivanov")
                .number("1111222233334444")
                .user(user)
                .expirationDate(LocalDate.of(2030, 12, 31))
                .active(true)
                .build());

        mockMvc.perform(get("/shop/cards/users")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldUpdateCard() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        PaymentCard card = paymentCardRepository.save(PaymentCard.builder()
                .holder("Ivan Ivanov")
                .number("1111222233334444")
                .user(user)
                .expirationDate(LocalDate.of(2030, 12, 31))
                .active(true)
                .build());

        PaymentCardDto updateDto = PaymentCardDto.builder()
                .holder("Petr Petrov")
                .number("9999888877776666")
                .userId(user.getId())
                .expirationDate(LocalDate.of(2035, 1, 1))
                .active(true)
                .build();

        mockMvc.perform(patch("/shop/cards/{card_id}", card.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("Petr Petrov"));

        PaymentCard updated = paymentCardRepository.findById(card.getId()).get();
        assertThat(updated.getHolder()).isEqualTo("Petr Petrov");
    }

    @Test
    void shouldDeleteCard() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        PaymentCard card = paymentCardRepository.save(PaymentCard.builder()
                .holder("Ivan Ivanov")
                .number("1111222233334444")
                .user(user)
                .expirationDate(LocalDate.of(2030, 12, 31))
                .active(true)
                .build());

        mockMvc.perform(delete("/shop/cards/{card_id}", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").exists());

        assertThat(paymentCardRepository.findById(card.getId())).isEmpty();
    }

    @Test
    void shouldActivateCard() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        PaymentCard card = paymentCardRepository.save(PaymentCard.builder()
                .holder("Ivan Ivanov")
                .number("1111222233334444")
                .user(user)
                .expirationDate(LocalDate.of(2030, 12, 31))
                .active(false)
                .build());

        mockMvc.perform(patch("/shop/cards/activate/{card_id}", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        assertThat(paymentCardRepository.findById(card.getId()).get().isActive()).isTrue();
    }

    @Test
    void shouldDeactivateCard() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .surname("Ivanov")
                .email("ivan@mail.com")
                .birthDate(LocalDate.of(2000, 5, 10))
                .active(true)
                .build());

        PaymentCard card = paymentCardRepository.save(PaymentCard.builder()
                .holder("Ivan Ivanov")
                .number("1111222233334444")
                .user(user)
                .expirationDate(LocalDate.of(2030, 12, 31))
                .active(true)
                .build());

        mockMvc.perform(patch("/shop/cards/deactivate/{card_id}", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        assertThat(paymentCardRepository.findById(card.getId()).get().isActive()).isFalse();
    }
}