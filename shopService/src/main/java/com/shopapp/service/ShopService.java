package com.shopapp.service;

import com.shopapp.service.dto.ShopDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shopapp.domain.Shop}.
 */
public interface ShopService {
    /**
     * Save a shop.
     *
     * @param shopDTO the entity to save.
     * @return the persisted entity.
     */
    ShopDTO save(ShopDTO shopDTO);

    /**
     * Updates a shop.
     *
     * @param shopDTO the entity to update.
     * @return the persisted entity.
     */
    ShopDTO update(ShopDTO shopDTO);

    /**
     * Partially updates a shop.
     *
     * @param shopDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ShopDTO> partialUpdate(ShopDTO shopDTO);

    /**
     * Get all the shops.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ShopDTO> findAll(Pageable pageable);

    /**
     * Get the "id" shop.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShopDTO> findOne(Long id);

    /**
     * Delete the "id" shop.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
