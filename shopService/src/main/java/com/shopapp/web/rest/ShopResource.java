package com.shopapp.web.rest;

import com.shopapp.domain.Shop;
import com.shopapp.repository.ShopRepository;
import com.shopapp.service.ShopService;
import com.shopapp.service.dto.ShopDTO;
import com.shopapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shopapp.domain.Shop}.
 */
@RestController
@RequestMapping("/api")
public class ShopResource {

    private final Logger log = LoggerFactory.getLogger(ShopResource.class);

    private static final String ENTITY_NAME = "shopServiceShop";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShopService shopService;

    private final ShopRepository shopRepository;

    public ShopResource(ShopService shopService, ShopRepository shopRepository) {
        this.shopService = shopService;
        this.shopRepository = shopRepository;
    }

    /**
     * {@code POST  /shops} : Create a new shop.
     *
     * @param shopDTO the shopDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shopDTO, or with status {@code 400 (Bad Request)} if the shop has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Add Shop")
    @PostMapping("/shops")
    public ResponseEntity<ShopDTO> createShop(@Valid @RequestBody ShopDTO shopDTO) throws URISyntaxException {
        log.debug("REST request to save Shop : {}", shopDTO);
        if (shopDTO.getId() != null) {
            throw new BadRequestAlertException("A new shop cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShopDTO result = shopService.save(shopDTO);
        return ResponseEntity
            .created(new URI("/api/shops/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /shops/:id} : Updates an existing shop.
     *
     * @param id the id of the shopDTO to save.
     * @param shopDTO the shopDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shopDTO,
     * or with status {@code 400 (Bad Request)} if the shopDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shopDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Update infomation of Shop")
    @PutMapping("/shops/{id}")
    public ResponseEntity<ShopDTO> updateShop(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShopDTO shopDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Shop : {}, {}", id, shopDTO);
        if (shopDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shopDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shopRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ShopDTO result = shopService.update(shopDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shopDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /shops/:id} : Partial updates given fields of an existing shop, field will ignore if it is null
     *
     * @param id the id of the shopDTO to save.
     * @param shopDTO the shopDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shopDTO,
     * or with status {@code 400 (Bad Request)} if the shopDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shopDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shopDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Update Partition Infomation of Shop (config open/close time, config queue ")
    @PatchMapping(value = "/shops/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShopDTO> partialUpdateShop(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShopDTO shopDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Shop partially : {}, {}", id, shopDTO);
        if (shopDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shopDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shopRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShopDTO> result = shopService.partialUpdate(shopDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shopDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shops} : get all the shops.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shops in body.
     */
    @Operation(summary = "View list information of shop")
    @GetMapping("/shops")
    public ResponseEntity<List<ShopDTO>> getAllShops(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Shops");
        Page<ShopDTO> page = shopService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shops/:id} : get the "id" shop.
     *
     * @param id the id of the shopDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shopDTO, or with status {@code 404 (Not Found)}.
     */

    @Operation(summary = "Get information of specific shop")
    @GetMapping("/shops/{id}")
    public ResponseEntity<ShopDTO> getShop(@PathVariable Long id) {
        log.debug("REST request to get Shop : {}", id);
        Optional<ShopDTO> shopDTO = shopService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shopDTO);
    }

    /**
     * {@code DELETE  /shops/:id} : delete the "id" shop.
     *
     * @param id the id of the shopDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */

    @Operation(summary = "Delete Shop")
    @DeleteMapping("/shops/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        log.debug("REST request to delete Shop : {}", id);
        shopService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @Operation(summary = "Get shop by location and distance")
    @GetMapping("/shops/location")
    public ResponseEntity<List<Shop>> getShopByLocation(
        @RequestParam Double lon,
        @RequestParam Double lat,
        @RequestParam Double distance,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        Page<Shop> page = shopRepository.findByDistance(lon, lat, distance*1.609344, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
