package com.shopapp.repository;

import com.shopapp.domain.Shop;
import org.springframework.data.repository.query.Param;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Spring Data JPA repository for the Shop entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query( value = "SELECT *, (3959 * " +
        "   acos(cos(radians(:lat)) *  " +
        "   cos(radians(latitude)) *  " +
        "   cos(radians(longitude) -  " +
        "   radians(:lon)) +  " +
        "   sin(radians(:lat)) *  " +
        "   sin(radians(latitude)))) as dis"+
        " FROM store HAVING dis < :distance ORDER BY dis",
        countQuery = "SELECT count(*)" +
            " FROM shop WHERE " +
            " (\n" +
            "   3959 *\n" +
            "   acos(cos(radians(:lat)) * \n" +
            "   cos(radians(latitude)) * \n" +
            "   cos(radians(longitude) - \n" +
            "   radians(:lon)) + \n" +
            "   sin(radians(:lat)) * \n" +
            "   sin(radians(latitude)))\n" +
            ") < :distance",
        nativeQuery = true)
    Page<Shop> findByDistance(@Param("lon")Double lon, @Param("lat") Double lat, @Param("distance") Double distance, Pageable pageable);
}
