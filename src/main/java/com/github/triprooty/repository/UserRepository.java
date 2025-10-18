package com.github.triprooty.repository;

import com.github.triprooty.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByName(String name);

}

