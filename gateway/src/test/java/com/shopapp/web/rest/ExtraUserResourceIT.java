package com.shopapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.shopapp.IntegrationTest;
import com.shopapp.domain.ExtraUser;
import com.shopapp.repository.EntityManager;
import com.shopapp.repository.ExtraUserRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ExtraUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ExtraUserResourceIT {

    private static final Long DEFAULT_SHOP_ID = 1L;
    private static final Long UPDATED_SHOP_ID = 2L;

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Long DEFAULT_POINT = 1L;
    private static final Long UPDATED_POINT = 2L;

    private static final String ENTITY_API_URL = "/api/extra-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExtraUserRepository extraUserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ExtraUser extraUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExtraUser createEntity(EntityManager em) {
        ExtraUser extraUser = new ExtraUser()
            .shopId(DEFAULT_SHOP_ID)
            .fullName(DEFAULT_FULL_NAME)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS)
            .point(DEFAULT_POINT);
        return extraUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExtraUser createUpdatedEntity(EntityManager em) {
        ExtraUser extraUser = new ExtraUser()
            .shopId(UPDATED_SHOP_ID)
            .fullName(UPDATED_FULL_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .point(UPDATED_POINT);
        return extraUser;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ExtraUser.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        extraUser = createEntity(em);
    }

    @Test
    void createExtraUser() throws Exception {
        int databaseSizeBeforeCreate = extraUserRepository.findAll().collectList().block().size();
        // Create the ExtraUser
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeCreate + 1);
        ExtraUser testExtraUser = extraUserList.get(extraUserList.size() - 1);
        assertThat(testExtraUser.getShopId()).isEqualTo(DEFAULT_SHOP_ID);
        assertThat(testExtraUser.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testExtraUser.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testExtraUser.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testExtraUser.getPoint()).isEqualTo(DEFAULT_POINT);
    }

    @Test
    void createExtraUserWithExistingId() throws Exception {
        // Create the ExtraUser with an existing ID
        extraUser.setId(1L);

        int databaseSizeBeforeCreate = extraUserRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = extraUserRepository.findAll().collectList().block().size();
        // set the field null
        extraUser.setFullName(null);

        // Create the ExtraUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = extraUserRepository.findAll().collectList().block().size();
        // set the field null
        extraUser.setPhone(null);

        // Create the ExtraUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = extraUserRepository.findAll().collectList().block().size();
        // set the field null
        extraUser.setAddress(null);

        // Create the ExtraUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllExtraUsersAsStream() {
        // Initialize the database
        extraUserRepository.save(extraUser).block();

        List<ExtraUser> extraUserList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ExtraUser.class)
            .getResponseBody()
            .filter(extraUser::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(extraUserList).isNotNull();
        assertThat(extraUserList).hasSize(1);
        ExtraUser testExtraUser = extraUserList.get(0);
        assertThat(testExtraUser.getShopId()).isEqualTo(DEFAULT_SHOP_ID);
        assertThat(testExtraUser.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testExtraUser.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testExtraUser.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testExtraUser.getPoint()).isEqualTo(DEFAULT_POINT);
    }

    @Test
    void getAllExtraUsers() {
        // Initialize the database
        extraUserRepository.save(extraUser).block();

        // Get all the extraUserList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(extraUser.getId().intValue()))
            .jsonPath("$.[*].shopId")
            .value(hasItem(DEFAULT_SHOP_ID.intValue()))
            .jsonPath("$.[*].fullName")
            .value(hasItem(DEFAULT_FULL_NAME))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].point")
            .value(hasItem(DEFAULT_POINT.intValue()));
    }

    @Test
    void getExtraUser() {
        // Initialize the database
        extraUserRepository.save(extraUser).block();

        // Get the extraUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, extraUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(extraUser.getId().intValue()))
            .jsonPath("$.shopId")
            .value(is(DEFAULT_SHOP_ID.intValue()))
            .jsonPath("$.fullName")
            .value(is(DEFAULT_FULL_NAME))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.point")
            .value(is(DEFAULT_POINT.intValue()));
    }

    @Test
    void getNonExistingExtraUser() {
        // Get the extraUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingExtraUser() throws Exception {
        // Initialize the database
        extraUserRepository.save(extraUser).block();

        int databaseSizeBeforeUpdate = extraUserRepository.findAll().collectList().block().size();

        // Update the extraUser
        ExtraUser updatedExtraUser = extraUserRepository.findById(extraUser.getId()).block();
        updatedExtraUser
            .shopId(UPDATED_SHOP_ID)
            .fullName(UPDATED_FULL_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .point(UPDATED_POINT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedExtraUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedExtraUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeUpdate);
        ExtraUser testExtraUser = extraUserList.get(extraUserList.size() - 1);
        assertThat(testExtraUser.getShopId()).isEqualTo(UPDATED_SHOP_ID);
        assertThat(testExtraUser.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testExtraUser.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testExtraUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testExtraUser.getPoint()).isEqualTo(UPDATED_POINT);
    }

    @Test
    void putNonExistingExtraUser() throws Exception {
        int databaseSizeBeforeUpdate = extraUserRepository.findAll().collectList().block().size();
        extraUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, extraUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchExtraUser() throws Exception {
        int databaseSizeBeforeUpdate = extraUserRepository.findAll().collectList().block().size();
        extraUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamExtraUser() throws Exception {
        int databaseSizeBeforeUpdate = extraUserRepository.findAll().collectList().block().size();
        extraUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateExtraUserWithPatch() throws Exception {
        // Initialize the database
        extraUserRepository.save(extraUser).block();

        int databaseSizeBeforeUpdate = extraUserRepository.findAll().collectList().block().size();

        // Update the extraUser using partial update
        ExtraUser partialUpdatedExtraUser = new ExtraUser();
        partialUpdatedExtraUser.setId(extraUser.getId());

        partialUpdatedExtraUser.shopId(UPDATED_SHOP_ID).address(UPDATED_ADDRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExtraUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExtraUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeUpdate);
        ExtraUser testExtraUser = extraUserList.get(extraUserList.size() - 1);
        assertThat(testExtraUser.getShopId()).isEqualTo(UPDATED_SHOP_ID);
        assertThat(testExtraUser.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testExtraUser.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testExtraUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testExtraUser.getPoint()).isEqualTo(DEFAULT_POINT);
    }

    @Test
    void fullUpdateExtraUserWithPatch() throws Exception {
        // Initialize the database
        extraUserRepository.save(extraUser).block();

        int databaseSizeBeforeUpdate = extraUserRepository.findAll().collectList().block().size();

        // Update the extraUser using partial update
        ExtraUser partialUpdatedExtraUser = new ExtraUser();
        partialUpdatedExtraUser.setId(extraUser.getId());

        partialUpdatedExtraUser
            .shopId(UPDATED_SHOP_ID)
            .fullName(UPDATED_FULL_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .point(UPDATED_POINT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExtraUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExtraUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeUpdate);
        ExtraUser testExtraUser = extraUserList.get(extraUserList.size() - 1);
        assertThat(testExtraUser.getShopId()).isEqualTo(UPDATED_SHOP_ID);
        assertThat(testExtraUser.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testExtraUser.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testExtraUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testExtraUser.getPoint()).isEqualTo(UPDATED_POINT);
    }

    @Test
    void patchNonExistingExtraUser() throws Exception {
        int databaseSizeBeforeUpdate = extraUserRepository.findAll().collectList().block().size();
        extraUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, extraUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchExtraUser() throws Exception {
        int databaseSizeBeforeUpdate = extraUserRepository.findAll().collectList().block().size();
        extraUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamExtraUser() throws Exception {
        int databaseSizeBeforeUpdate = extraUserRepository.findAll().collectList().block().size();
        extraUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(extraUser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ExtraUser in the database
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteExtraUser() {
        // Initialize the database
        extraUserRepository.save(extraUser).block();

        int databaseSizeBeforeDelete = extraUserRepository.findAll().collectList().block().size();

        // Delete the extraUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, extraUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ExtraUser> extraUserList = extraUserRepository.findAll().collectList().block();
        assertThat(extraUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
