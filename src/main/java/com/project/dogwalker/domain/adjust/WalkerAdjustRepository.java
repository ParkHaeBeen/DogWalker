package com.project.dogwalker.domain.adjust;


import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkerAdjustRepository extends JpaRepository<WalkerAdjust,Long> {
  @Query("SELECT wa FROM WalkerAdjust wa WHERE wa.userId = :walkerId AND wa.walkerAdjustDate = :adjustDate")
  Optional<WalkerAdjust> findByUserIdAndAndWalkerAdjustDate(@Param("walkerId") Long walkerId,@Param("adjustDate") LocalDate adjustDate);

}
