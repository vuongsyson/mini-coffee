package com.shopapp.service.mapper;

import com.shopapp.domain.Item;
import com.shopapp.domain.Shop;
import com.shopapp.service.dto.ItemDTO;
import com.shopapp.service.dto.ShopDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Item} and its DTO {@link ItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ItemMapper extends EntityMapper<ItemDTO, Item> {
    @Mapping(target = "shop", source = "shop", qualifiedByName = "shopId")
    ItemDTO toDto(Item s);

    @Named("shopId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShopDTO toDtoShopId(Shop shop);
}
