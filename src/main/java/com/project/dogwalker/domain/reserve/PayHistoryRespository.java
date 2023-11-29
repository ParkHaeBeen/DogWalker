package com.project.dogwalker.domain.reserve;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayHistoryRespository extends JpaRepository<PayHistory,Long> {

}
