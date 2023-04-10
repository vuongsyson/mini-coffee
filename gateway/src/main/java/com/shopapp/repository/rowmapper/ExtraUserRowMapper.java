package com.shopapp.repository.rowmapper;

import com.shopapp.domain.ExtraUser;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ExtraUser}, with proper type conversions.
 */
@Service
public class ExtraUserRowMapper implements BiFunction<Row, String, ExtraUser> {

    private final ColumnConverter converter;

    public ExtraUserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ExtraUser} stored in the database.
     */
    @Override
    public ExtraUser apply(Row row, String prefix) {
        ExtraUser entity = new ExtraUser();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setShopId(converter.fromRow(row, prefix + "_shop_id", Long.class));
        entity.setFullName(converter.fromRow(row, prefix + "_full_name", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setPoint(converter.fromRow(row, prefix + "_point", Long.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setInternalUserId(converter.fromRow(row, prefix + "_internal_user_id", Long.class));
        return entity;
    }
}
