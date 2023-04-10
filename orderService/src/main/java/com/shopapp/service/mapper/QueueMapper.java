package com.shopapp.service.mapper;

import com.shopapp.domain.Queue;
import com.shopapp.service.dto.QueueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Queue} and its DTO {@link QueueDTO}.
 */
@Mapper(componentModel = "spring")
public interface QueueMapper extends EntityMapper<QueueDTO, Queue> {}
