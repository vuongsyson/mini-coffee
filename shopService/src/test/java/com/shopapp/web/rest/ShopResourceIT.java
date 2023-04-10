package com.shopapp.web.rest;

import static com.shopapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.shopapp.IntegrationTest;
import com.shopapp.domain.Shop;
import com.shopapp.repository.ShopRepository;
import com.shopapp.service.dto.ShopDTO;
import com.shopapp.service.mapper.ShopMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ShopResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShopResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final ZonedDateTime DEFAULT_OPEN_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_OPEN_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_CLOSE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CLOSE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_QUEUE_AVAILABLE = 1;
    private static final Integer UPDATED_QUEUE_AVAILABLE = 2;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/shops";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShopMockMvc;

    private Shop shop;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shop createEntity(EntityManager em) {
        Shop shop = new Shop()
            .code(DEFAULT_CODE)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS)
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE)
            .openTime(DEFAULT_OPEN_TIME)
            .closeTime(DEFAULT_CLOSE_TIME)
            .queueAvailable(DEFAULT_QUEUE_AVAILABLE)
            .active(DEFAULT_ACTIVE);
        return shop;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shop createUpdatedEntity(EntityManager em) {
        Shop shop = new Shop()
            .code(UPDATED_CODE)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .openTime(UPDATED_OPEN_TIME)
            .closeTime(UPDATED_CLOSE_TIME)
            .queueAvailable(UPDATED_QUEUE_AVAILABLE)
            .active(UPDATED_ACTIVE);
        return shop;
    }

    @BeforeEach
    public void initTest() {
        shop = createEntity(em);
    }

    @Test
    @Transactional
    void createShop() throws Exception {
        int databaseSizeBeforeCreate = shopRepository.findAll().size();
        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);
        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isCreated());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeCreate + 1);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testShop.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testShop.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testShop.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testShop.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testShop.getOpenTime()).isEqualTo(DEFAULT_OPEN_TIME);
        assertThat(testShop.getCloseTime()).isEqualTo(DEFAULT_CLOSE_TIME);
        assertThat(testShop.getQueueAvailable()).isEqualTo(DEFAULT_QUEUE_AVAILABLE);
        assertThat(testShop.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createShopWithExistingId() throws Exception {
        // Create the Shop with an existing ID
        shop.setId(1L);
        ShopDTO shopDTO = shopMapper.toDto(shop);

        int databaseSizeBeforeCreate = shopRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = shopRepository.findAll().size();
        // set the field null
        shop.setCode(null);

        // Create the Shop, which fails.
        ShopDTO shopDTO = shopMapper.toDto(shop);

        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isBadRequest());

        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = shopRepository.findAll().size();
        // set the field null
        shop.setAddress(null);

        // Create the Shop, which fails.
        ShopDTO shopDTO = shopMapper.toDto(shop);

        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isBadRequest());

        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = shopRepository.findAll().size();
        // set the field null
        shop.setLongitude(null);

        // Create the Shop, which fails.
        ShopDTO shopDTO = shopMapper.toDto(shop);

        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isBadRequest());

        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = shopRepository.findAll().size();
        // set the field null
        shop.setLatitude(null);

        // Create the Shop, which fails.
        ShopDTO shopDTO = shopMapper.toDto(shop);

        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isBadRequest());

        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOpenTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = shopRepository.findAll().size();
        // set the field null
        shop.setOpenTime(null);

        // Create the Shop, which fails.
        ShopDTO shopDTO = shopMapper.toDto(shop);

        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isBadRequest());

        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCloseTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = shopRepository.findAll().size();
        // set the field null
        shop.setCloseTime(null);

        // Create the Shop, which fails.
        ShopDTO shopDTO = shopMapper.toDto(shop);

        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isBadRequest());

        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQueueAvailableIsRequired() throws Exception {
        int databaseSizeBeforeTest = shopRepository.findAll().size();
        // set the field null
        shop.setQueueAvailable(null);

        // Create the Shop, which fails.
        ShopDTO shopDTO = shopMapper.toDto(shop);

        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isBadRequest());

        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = shopRepository.findAll().size();
        // set the field null
        shop.setActive(null);

        // Create the Shop, which fails.
        ShopDTO shopDTO = shopMapper.toDto(shop);

        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isBadRequest());

        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShops() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        // Get all the shopList
        restShopMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shop.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].openTime").value(hasItem(sameInstant(DEFAULT_OPEN_TIME))))
            .andExpect(jsonPath("$.[*].closeTime").value(hasItem(sameInstant(DEFAULT_CLOSE_TIME))))
            .andExpect(jsonPath("$.[*].queueAvailable").value(hasItem(DEFAULT_QUEUE_AVAILABLE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        // Get the shop
        restShopMockMvc
            .perform(get(ENTITY_API_URL_ID, shop.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shop.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.openTime").value(sameInstant(DEFAULT_OPEN_TIME)))
            .andExpect(jsonPath("$.closeTime").value(sameInstant(DEFAULT_CLOSE_TIME)))
            .andExpect(jsonPath("$.queueAvailable").value(DEFAULT_QUEUE_AVAILABLE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingShop() throws Exception {
        // Get the shop
        restShopMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        int databaseSizeBeforeUpdate = shopRepository.findAll().size();

        // Update the shop
        Shop updatedShop = shopRepository.findById(shop.getId()).get();
        // Disconnect from session so that the updates on updatedShop are not directly saved in db
        em.detach(updatedShop);
        updatedShop
            .code(UPDATED_CODE)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .openTime(UPDATED_OPEN_TIME)
            .closeTime(UPDATED_CLOSE_TIME)
            .queueAvailable(UPDATED_QUEUE_AVAILABLE)
            .active(UPDATED_ACTIVE);
        ShopDTO shopDTO = shopMapper.toDto(updatedShop);

        restShopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shopDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shopDTO))
            )
            .andExpect(status().isOk());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testShop.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testShop.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testShop.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testShop.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testShop.getOpenTime()).isEqualTo(UPDATED_OPEN_TIME);
        assertThat(testShop.getCloseTime()).isEqualTo(UPDATED_CLOSE_TIME);
        assertThat(testShop.getQueueAvailable()).isEqualTo(UPDATED_QUEUE_AVAILABLE);
        assertThat(testShop.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shopDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shopDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shopDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShopWithPatch() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        int databaseSizeBeforeUpdate = shopRepository.findAll().size();

        // Update the shop using partial update
        Shop partialUpdatedShop = new Shop();
        partialUpdatedShop.setId(shop.getId());

        partialUpdatedShop.address(UPDATED_ADDRESS).longitude(UPDATED_LONGITUDE).openTime(UPDATED_OPEN_TIME).active(UPDATED_ACTIVE);

        restShopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShop.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShop))
            )
            .andExpect(status().isOk());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testShop.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testShop.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testShop.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testShop.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testShop.getOpenTime()).isEqualTo(UPDATED_OPEN_TIME);
        assertThat(testShop.getCloseTime()).isEqualTo(DEFAULT_CLOSE_TIME);
        assertThat(testShop.getQueueAvailable()).isEqualTo(DEFAULT_QUEUE_AVAILABLE);
        assertThat(testShop.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateShopWithPatch() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        int databaseSizeBeforeUpdate = shopRepository.findAll().size();

        // Update the shop using partial update
        Shop partialUpdatedShop = new Shop();
        partialUpdatedShop.setId(shop.getId());

        partialUpdatedShop
            .code(UPDATED_CODE)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .openTime(UPDATED_OPEN_TIME)
            .closeTime(UPDATED_CLOSE_TIME)
            .queueAvailable(UPDATED_QUEUE_AVAILABLE)
            .active(UPDATED_ACTIVE);

        restShopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShop.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShop))
            )
            .andExpect(status().isOk());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testShop.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testShop.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testShop.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testShop.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testShop.getOpenTime()).isEqualTo(UPDATED_OPEN_TIME);
        assertThat(testShop.getCloseTime()).isEqualTo(UPDATED_CLOSE_TIME);
        assertThat(testShop.getQueueAvailable()).isEqualTo(UPDATED_QUEUE_AVAILABLE);
        assertThat(testShop.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shopDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shopDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shopDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // Create the Shop
        ShopDTO shopDTO = shopMapper.toDto(shop);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(shopDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        int databaseSizeBeforeDelete = shopRepository.findAll().size();

        // Delete the shop
        restShopMockMvc
            .perform(delete(ENTITY_API_URL_ID, shop.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
