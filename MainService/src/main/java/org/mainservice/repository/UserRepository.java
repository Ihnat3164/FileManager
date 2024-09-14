package org.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.mainservice.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

}
