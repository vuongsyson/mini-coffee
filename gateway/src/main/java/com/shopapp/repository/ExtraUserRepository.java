package com.shopapp.repository;

import com.shopapp.domain.ExtraUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ExtraUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExtraUserRepository extends ReactiveCrudRepository<ExtraUser, Long>, ExtraUserRepositoryInternal {
    @Query("SELECT * FROM extra_user entity WHERE entity.internal_user_id = :id")
    Flux<ExtraUser> findByInternalUser(Long id);

    @Query("SELECT * FROM extra_user entity WHERE entity.internal_user_id IS NULL")
    Flux<ExtraUser> findAllWhereInternalUserIsNull();

    @Override
    <S extends ExtraUser> Mono<S> save(S entity);

    @Override
    Flux<ExtraUser> findAll();

    @Override
    Mono<ExtraUser> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ExtraUserRepositoryInternal {
    <S extends ExtraUser> Mono<S> save(S entity);

    Flux<ExtraUser> findAllBy(Pageable pageable);

    Flux<ExtraUser> findAll();

    Mono<ExtraUser> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ExtraUser> findAllBy(Pageable pageable, Criteria criteria);

}
