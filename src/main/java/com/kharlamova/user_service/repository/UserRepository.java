package com.kharlamova.user_service.repository;

import com.kharlamova.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);
}
