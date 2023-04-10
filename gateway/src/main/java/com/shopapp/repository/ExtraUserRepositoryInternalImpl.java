package com.shopapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.shopapp.domain.ExtraUser;
import com.shopapp.repository.rowmapper.ExtraUserRowMapper;
import com.shopapp.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the ExtraUser entity.
 */
@SuppressWarnings("unused")
class ExtraUserRepositoryInternalImpl extends SimpleR2dbcRepository<ExtraUser, Long> implements ExtraUserRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final ExtraUserRowMapper extrauserMapper;

    private static final Table entityTable = Table.aliased("extra_user", EntityManager.ENTITY_ALIAS);
    private static final Table internalUserTable = Table.aliased("shop_user", "internalUser");

    public ExtraUserRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        ExtraUserRowMapper extrauserMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ExtraUser.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.extrauserMapper = extrauserMapper;
    }

    @Override
    public Flux<ExtraUser> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ExtraUser> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ExtraUserSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(internalUserTable, "internalUser"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(internalUserTable)
            .on(Column.create("internal_user_id", entityTable))
            .equals(Column.create("id", internalUserTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ExtraUser.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ExtraUser> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ExtraUser> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ExtraUser process(Row row, RowMetadata metadata) {
        ExtraUser entity = extrauserMapper.apply(row, "e");
        entity.setInternalUser(userMapper.apply(row, "internalUser"));
        return entity;
    }

    @Override
    public <S extends ExtraUser> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
