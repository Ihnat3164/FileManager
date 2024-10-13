package org.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.mainservice.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
        Optional<User> findUserByEmail(String email);
        Optional<User> findUserById(Long id);
}
