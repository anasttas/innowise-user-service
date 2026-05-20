package com.kharlamova.user_service.specification;

import com.kharlamova.user_service.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasNameLike(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasSurnameLike(String surname) {
        return (root, query, cb) -> {
            if (surname == null || surname.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("surname")),
                    "%" + surname.toLowerCase() + "%");
        };
    }
}
