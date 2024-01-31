package com.project.dogwalker.domain.walkerservice;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WalkerServiceRouteRepository extends JpaRepository<WalkerServiceRoute,Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO walker_service_route (walker_reserve_service_id, walker_route, created_at) VALUES (:reserveId, ST_GeomFromText(:lineString), now())", nativeQuery = true)
    void saveWithLineString(@Param("reserveId") Long reserveId, @Param("lineString") String lineString);

    @Query(value = "SELECT walker_service_route_id, walker_reserve_service_id, "
        + "ST_AsText(walker_route) as walker_route,created_at FROM walker_service_route WHERE walker_reserve_service_id = :reserveId", nativeQuery = true)
    Optional<WalkerServiceRoute> findByReserveInfoReserveId(@Param("reserveId") final Long reserveId);

}
