package com.shopapp.service.impl;

import com.shopapp.config.Constants;
import com.shopapp.domain.Order;
import com.shopapp.domain.Queue;
import com.shopapp.repository.OrderRepository;
import com.shopapp.repository.QueueRepository;
import com.shopapp.service.OrderService;
import com.shopapp.service.dto.OrderDTO;
import com.shopapp.service.mapper.OrderMapper;

import java.util.List;
import java.util.Optional;

import com.shopapp.web.rest.errors.BadRequestAlertException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final QueueRepository queueRepository;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,QueueRepository queueRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.queueRepository = queueRepository;
    }

    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        List<Queue> listQueue = queueRepository.findByShopId(orderDTO.getShopId());
        int countStack = 0;
        if(CollectionUtils.isNotEmpty(listQueue)) {
            for (Queue queue:
                 listQueue) {
                if(queue.getQueueArray().length < Constants.MAX_SIZE_QUEUE) {
                    countStack++;
                    int size = queue.getQueueArray().length;
                    queue.getQueueArray()[size] = (Long) order.getId();
                    queueRepository.save(queue);
                }
            }
        }
        if(countStack==0) {
            throw new BadRequestAlertException("Order queue is fully", Queue.class.getName(), order.getShopId().toString());
        }
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDTO update(OrderDTO orderDTO) {
        log.debug("Request to update Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public Optional<OrderDTO> partialUpdate(OrderDTO orderDTO) {
        log.debug("Request to partially update Order : {}", orderDTO);

        return orderRepository
            .findById(orderDTO.getId())
            .map(existingOrder -> {
                orderMapper.partialUpdate(existingOrder, orderDTO);

                return existingOrder;
            })
            .map(orderRepository::save)
            .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }
}
