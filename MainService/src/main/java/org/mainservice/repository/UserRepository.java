package org.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.mainservice.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
        public Optional<User> findUserByEmail(String email);
}
