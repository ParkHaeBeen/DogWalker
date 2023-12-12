package com.project.dogwalker.domain.user.walker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkerScheduleTempRepository extends JpaRepository<WalkerScheduleTemp,Long> {

}
