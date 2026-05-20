package com.kharlamova.user_service.specification;

import com.kharlamova.user_service.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {
    public static Specification<PaymentCard> hasHolderLike(String holder) {
        return (root, query, cb) -> {
            if (holder == null || holder.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("holder")),
                    "%" + holder.toLowerCase() + "%");
        };
    }

    public static Specification<PaymentCard> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("user").get("id"), userId);
        };
    }
}
