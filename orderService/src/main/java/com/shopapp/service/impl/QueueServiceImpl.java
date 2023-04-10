package com.shopapp.service.impl;

import com.shopapp.config.Constants;
import com.shopapp.domain.Queue;
import com.shopapp.repository.QueueRepository;
import com.shopapp.service.QueueService;
import com.shopapp.service.dto.QueueDTO;
import com.shopapp.service.mapper.QueueMapper;
import java.util.Optional;

import com.shopapp.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Queue}.
 */
@Service
@Transactional
public class QueueServiceImpl implements QueueService {

    private final Logger log = LoggerFactory.getLogger(QueueServiceImpl.class);

    private final QueueRepository queueRepository;

    private final QueueMapper queueMapper;

    public QueueServiceImpl(QueueRepository queueRepository, QueueMapper queueMapper) {
        this.queueRepository = queueRepository;
        this.queueMapper = queueMapper;
    }

    @Override
    public QueueDTO save(QueueDTO queueDTO) {
        log.debug("Request to save Queue : {}", queueDTO);
        Queue queue = queueMapper.toEntity(queueDTO);
        int queueMaxNum = queueRepository.countAllByShopId(queueDTO.getShopId());
        // Check Max Queue exist
        if(queueMaxNum == Constants.MAX_NUMBER_QUEUE) {
            throw new BadRequestAlertException("bss not found", Queue.class.getName(), queueDTO.getShopId().toString());
        }
        queue = queueRepository.save(queue);
        return queueMapper.toDto(queue);
    }

    @Override
    public QueueDTO update(QueueDTO queueDTO) {
        log.debug("Request to update Queue : {}", queueDTO);
        Queue queue = queueMapper.toEntity(queueDTO);
        queue = queueRepository.save(queue);
        return queueMapper.toDto(queue);
    }

    @Override
    public Optional<QueueDTO> partialUpdate(QueueDTO queueDTO) {
        log.debug("Request to partially update Queue : {}", queueDTO);

        return queueRepository
            .findById(queueDTO.getId())
            .map(existingQueue -> {
                queueMapper.partialUpdate(existingQueue, queueDTO);

                return existingQueue;
            })
            .map(queueRepository::save)
            .map(queueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QueueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Queues");
        return queueRepository.findAll(pageable).map(queueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QueueDTO> findOne(Long id) {
        log.debug("Request to get Queue : {}", id);
        return queueRepository.findById(id).map(queueMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Queue : {}", id);
        queueRepository.deleteById(id);
    }
}
