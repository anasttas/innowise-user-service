package com.kharlamova.user_service.repository;

import com.kharlamova.user_service.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {
    @Query("SELECT c FROM PaymentCard c WHERE c.number = :number")
    Optional<PaymentCard> findUserByNumber(@Param("number") String number);
}
