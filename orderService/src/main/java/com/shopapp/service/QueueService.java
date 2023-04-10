package com.shopapp.service;

import com.shopapp.service.dto.QueueDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shopapp.domain.Queue}.
 */
public interface QueueService {
    /**
     * Save a queue.
     *
     * @param queueDTO the entity to save.
     * @return the persisted entity.
     */
    QueueDTO save(QueueDTO queueDTO);

    /**
     * Updates a queue.
     *
     * @param queueDTO the entity to update.
     * @return the persisted entity.
     */
    QueueDTO update(QueueDTO queueDTO);

    /**
     * Partially updates a queue.
     *
     * @param queueDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<QueueDTO> partialUpdate(QueueDTO queueDTO);

    /**
     * Get all the queues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<QueueDTO> findAll(Pageable pageable);

    /**
     * Get the "id" queue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<QueueDTO> findOne(Long id);

    /**
     * Delete the "id" queue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
