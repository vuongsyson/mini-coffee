package com.shopapp.service.impl;

import com.shopapp.domain.Shop;
import com.shopapp.repository.ShopRepository;
import com.shopapp.service.ShopService;
import com.shopapp.service.dto.ShopDTO;
import com.shopapp.service.mapper.ShopMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Shop}.
 */
@Service
@Transactional
public class ShopServiceImpl implements ShopService {

    private final Logger log = LoggerFactory.getLogger(ShopServiceImpl.class);

    private final ShopRepository shopRepository;

    private final ShopMapper shopMapper;

    public ShopServiceImpl(ShopRepository shopRepository, ShopMapper shopMapper) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
    }

    @Override
    public ShopDTO save(ShopDTO shopDTO) {
        log.debug("Request to save Shop : {}", shopDTO);
        Shop shop = shopMapper.toEntity(shopDTO);
        shop = shopRepository.save(shop);
        return shopMapper.toDto(shop);
    }

    @Override
    public ShopDTO update(ShopDTO shopDTO) {
        log.debug("Request to update Shop : {}", shopDTO);
        Shop shop = shopMapper.toEntity(shopDTO);
        shop = shopRepository.save(shop);
        return shopMapper.toDto(shop);
    }

    @Override
    public Optional<ShopDTO> partialUpdate(ShopDTO shopDTO) {
        log.debug("Request to partially update Shop : {}", shopDTO);

        return shopRepository
            .findById(shopDTO.getId())
            .map(existingShop -> {
                shopMapper.partialUpdate(existingShop, shopDTO);

                return existingShop;
            })
            .map(shopRepository::save)
            .map(shopMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShopDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Shops");
        return shopRepository.findAll(pageable).map(shopMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShopDTO> findOne(Long id) {
        log.debug("Request to get Shop : {}", id);
        return shopRepository.findById(id).map(shopMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Shop : {}", id);
        shopRepository.deleteById(id);
    }
}
