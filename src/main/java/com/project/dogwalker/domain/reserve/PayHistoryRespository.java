package com.project.dogwalker.domain.reserve;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayHistoryRespository extends JpaRepository<PayHistory,Long> {

  Optional<PayHistory> findByWalkerReserveInfoReserveId(final Long reserveId);
}
