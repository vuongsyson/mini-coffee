package com.shopapp.service.mapper;

import com.shopapp.domain.Shop;
import com.shopapp.service.dto.ShopDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shop} and its DTO {@link ShopDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShopMapper extends EntityMapper<ShopDTO, Shop> {}
