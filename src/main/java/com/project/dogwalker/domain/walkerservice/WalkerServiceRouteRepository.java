package com.project.dogwalker.domain.walkerservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkerServiceRouteRepository extends JpaRepository<WalkerServiceRoute,Long> {

}
