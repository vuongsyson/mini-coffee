package com.shopapp.web.rest;

import com.shopapp.domain.ExtraUser;
import com.shopapp.repository.ExtraUserRepository;
import com.shopapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.shopapp.domain.ExtraUser}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ExtraUserResource {

    private final Logger log = LoggerFactory.getLogger(ExtraUserResource.class);

    private static final String ENTITY_NAME = "extraUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExtraUserRepository extraUserRepository;

    public ExtraUserResource(ExtraUserRepository extraUserRepository) {
        this.extraUserRepository = extraUserRepository;
    }

    /**
     * {@code POST  /extra-users} : Create a new extraUser.
     *
     * @param extraUser the extraUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new extraUser, or with status {@code 400 (Bad Request)} if the extraUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/extra-users")
    public Mono<ResponseEntity<ExtraUser>> createExtraUser(@Valid @RequestBody ExtraUser extraUser) throws URISyntaxException {
        log.debug("REST request to save ExtraUser : {}", extraUser);
        if (extraUser.getId() != null) {
            throw new BadRequestAlertException("A new extraUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return extraUserRepository
            .save(extraUser)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/extra-users/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /extra-users/:id} : Updates an existing extraUser.
     *
     * @param id the id of the extraUser to save.
     * @param extraUser the extraUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated extraUser,
     * or with status {@code 400 (Bad Request)} if the extraUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the extraUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/extra-users/{id}")
    public Mono<ResponseEntity<ExtraUser>> updateExtraUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExtraUser extraUser
    ) throws URISyntaxException {
        log.debug("REST request to update ExtraUser : {}, {}", id, extraUser);
        if (extraUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, extraUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return extraUserRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return extraUserRepository
                    .save(extraUser)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /extra-users/:id} : Partial updates given fields of an existing extraUser, field will ignore if it is null
     *
     * @param id the id of the extraUser to save.
     * @param extraUser the extraUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated extraUser,
     * or with status {@code 400 (Bad Request)} if the extraUser is not valid,
     * or with status {@code 404 (Not Found)} if the extraUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the extraUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/extra-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ExtraUser>> partialUpdateExtraUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExtraUser extraUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update ExtraUser partially : {}, {}", id, extraUser);
        if (extraUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, extraUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return extraUserRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ExtraUser> result = extraUserRepository
                    .findById(extraUser.getId())
                    .map(existingExtraUser -> {
                        if (extraUser.getShopId() != null) {
                            existingExtraUser.setShopId(extraUser.getShopId());
                        }
                        if (extraUser.getFullName() != null) {
                            existingExtraUser.setFullName(extraUser.getFullName());
                        }
                        if (extraUser.getPhone() != null) {
                            existingExtraUser.setPhone(extraUser.getPhone());
                        }
                        if (extraUser.getAddress() != null) {
                            existingExtraUser.setAddress(extraUser.getAddress());
                        }
                        if (extraUser.getPoint() != null) {
                            existingExtraUser.setPoint(extraUser.getPoint());
                        }
                        if (extraUser.getCreatedBy() != null) {
                            existingExtraUser.setCreatedBy(extraUser.getCreatedBy());
                        }
                        if (extraUser.getCreatedDate() != null) {
                            existingExtraUser.setCreatedDate(extraUser.getCreatedDate());
                        }
                        if (extraUser.getLastModifiedBy() != null) {
                            existingExtraUser.setLastModifiedBy(extraUser.getLastModifiedBy());
                        }
                        if (extraUser.getLastModifiedDate() != null) {
                            existingExtraUser.setLastModifiedDate(extraUser.getLastModifiedDate());
                        }

                        return existingExtraUser;
                    })
                    .flatMap(extraUserRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /extra-users} : get all the extraUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of extraUsers in body.
     */
    @GetMapping("/extra-users")
    public Mono<List<ExtraUser>> getAllExtraUsers() {
        log.debug("REST request to get all ExtraUsers");
        return extraUserRepository.findAll().collectList();
    }

    /**
     * {@code GET  /extra-users} : get all the extraUsers as a stream.
     * @return the {@link Flux} of extraUsers.
     */
    @GetMapping(value = "/extra-users", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ExtraUser> getAllExtraUsersAsStream() {
        log.debug("REST request to get all ExtraUsers as a stream");
        return extraUserRepository.findAll();
    }

    /**
     * {@code GET  /extra-users/:id} : get the "id" extraUser.
     *
     * @param id the id of the extraUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the extraUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/extra-users/{id}")
    public Mono<ResponseEntity<ExtraUser>> getExtraUser(@PathVariable Long id) {
        log.debug("REST request to get ExtraUser : {}", id);
        Mono<ExtraUser> extraUser = extraUserRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(extraUser);
    }

    /**
     * {@code DELETE  /extra-users/:id} : delete the "id" extraUser.
     *
     * @param id the id of the extraUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/extra-users/{id}")
    public Mono<ResponseEntity<Void>> deleteExtraUser(@PathVariable Long id) {
        log.debug("REST request to delete ExtraUser : {}", id);
        return extraUserRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
