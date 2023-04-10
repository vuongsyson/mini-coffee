package com.shopapp.repository;

import com.shopapp.domain.Order;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface OrderRepository extends JpaRepository<Order, Long> {}
