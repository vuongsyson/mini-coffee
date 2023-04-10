package com.shopapp.repository;

import com.shopapp.domain.OrderItem;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderItem entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
