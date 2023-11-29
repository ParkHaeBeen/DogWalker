package com.project.dogwalker.domain.reserve;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkerReserveServiceRepository extends JpaRepository<WalkerReserveService,Long> {
 Optional<WalkerReserveService> findByWalkerUserIdAndServiceDate(Long walkerId, LocalDateTime serviceDate);
}
