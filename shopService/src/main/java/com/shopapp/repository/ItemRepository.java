package com.shopapp.repository;

import com.shopapp.domain.Item;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Item entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface ItemRepository extends JpaRepository<Item, Long> {}
