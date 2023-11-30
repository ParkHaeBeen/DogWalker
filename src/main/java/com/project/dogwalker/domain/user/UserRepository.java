package com.project.dogwalker.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

  Optional<User> findByUserEmail(String userEmail);

  Optional<User> findByUserEmailAndUserRole(String userEmail,Role role);

  Optional<User> findByUserIdAndUserRole(Long userId,Role role);
}
