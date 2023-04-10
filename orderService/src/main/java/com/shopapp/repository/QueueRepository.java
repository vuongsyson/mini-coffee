package com.shopapp.repository;

import com.shopapp.domain.Queue;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Queue entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface QueueRepository extends JpaRepository<Queue, Long> {
    int countAllByShopId(Long id);

    List<Queue> findByShopId(Long id);
}
