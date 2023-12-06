package com.project.dogwalker.domain.reserve;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkerReserveServiceRepository extends JpaRepository<WalkerReserveServiceInfo,Long> {
 Optional<WalkerReserveServiceInfo> findByWalkerUserIdAndServiceDateTime(Long walkerId, LocalDateTime serviceDate);

 Optional<WalkerReserveServiceInfo> findByReserveIdAndStatusAndWalkerUserId(Long reserveId,WalkerServiceStatus walkerServiceStatus,Long walkerId);
}
